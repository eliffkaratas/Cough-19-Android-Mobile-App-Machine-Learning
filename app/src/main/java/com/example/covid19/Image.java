package com.example.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.net.Uri;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Image extends AppCompatActivity {
    private ProgressDialog uploadImageProgress;

    private Button btnChoose, btnUpload, btnUpload2, btnCamera;
    private TextView textView;
    private ImageView iv;
    private Uri filePath;
    private Bitmap image;


    private StorageReference image_storage;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    String uid;
    DocumentReference documentReference;

    public static String urlName;

    private final int PICK_IMAGE_REQUEST = 71;
    public final int CAMERA_PERM_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        uploadImageProgress = new ProgressDialog(this);
        iv = ( ImageView ) findViewById(R.id.imageView);
        textView = findViewById(R.id.textView14);
        btnUpload = ( Button ) findViewById(R.id.uploadImage);
        btnUpload2 = ( Button ) findViewById(R.id.uploadImage2);
        btnChoose = ( Button ) findViewById(R.id.chooseImage);
        btnCamera = ( Button ) findViewById(R.id.uploadCamera);


        image_storage= FirebaseStorage.getInstance().getReference();

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        documentReference= db.collection("Profiles").document(uid);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnChoose.hasOnClickListeners()){
                    uploadFromGallery();
                }
            }
        });
        btnUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnCamera.hasOnClickListeners()){
                   uploadFromCamera();
                }
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, 0);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu();
            }
        });
    }

   private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Please select an image."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 0 && resultCode == RESULT_OK) {
            image = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(image);
        }
    }

    private  void uploadFromGallery(){
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final String random = UUID.randomUUID().toString();
            final StorageReference riversRef = image_storage.child(uid).child("Image").child(random);
            riversRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            urlName = downloadUri.toString();
                            documentReference.update("Image",urlName);

                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(Image.this, "Image is saved.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Image.this, "Image upload error."
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Image is uploading..." + ( int ) progress + "%");
                        }
                    });

        }
    }
   /* private void uploadImage () {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Yükleniyor...");
            progressDialog.show();
            final StorageReference ref = mStorageRef.child("gallery_images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri = uri;
                                    urlName = downloadUri.toString();

                                    Toast.makeText(Image.this, "111."
                                            , Toast.LENGTH_SHORT).show();

                                    uploadMap(urlName);

                                    Toast.makeText(Image.this, "2222."
                                            , Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(Image.this, "Görüntü yüklendi.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Image.this, "Görüntü yüklenemedi."
                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Görüntü yükleniyor..." + ( int ) progress + "%");
                        }
                    });
        }
    }
*/
    private boolean checkPermissions () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
           return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            return false;
        }
    }


    private  void uploadFromCamera(){
        if (image != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Image is uploading...");
            progressDialog.show();


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            final String random = UUID.randomUUID().toString();
            final StorageReference riversRef = image_storage.child(uid).child("Image").child(random);


            byte[] b = stream.toByteArray();
            riversRef.putBytes(b).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress( UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Image is uploading..." + ( int ) progress + "%");

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            urlName = downloadUri.toString();
                            documentReference.update("Image",urlName);

                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(Image.this, "Image is saved.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Image.this, "Image upload error."
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*
    private void uploadImage2() {
        if(image!=null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Yükleniyor...");
            progressDialog.show();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            final String random = UUID.randomUUID().toString();
            final StorageReference imageRef = mStorageRef.child("camera_images/" + random);

            byte[] b = stream.toByteArray();
            imageRef.putBytes(b)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Görüntü yükleniyor..." + ( int ) progress + "%");
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri = uri;
                                    urlName = downloadUri.toString();

                                    Toast.makeText(Image.this, "111."
                                            , Toast.LENGTH_SHORT).show();

                                    uploadMap(urlName);

                                    Toast.makeText(Image.this, "2222."
                                            , Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(Image.this, "Görüntü yüklendi.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Image.this, "Görüntü yüklenemedi.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadMap(String image){
        String id;
        id = imageId.getText().toString();
        Toast.makeText(Image.this, id , Toast.LENGTH_SHORT).show();
        try {
            db.collection("Profiller").document(id).update("Görüntü",image).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Başarılı.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (final Exception e) {
            e.printStackTrace();
        }*/
    private void openMenu() {
        Intent i = new Intent(Image.this, Menu.class);
        startActivity(i);
        finish();
    }
}