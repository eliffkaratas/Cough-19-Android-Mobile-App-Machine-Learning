package com.example.covid19;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.UUID;

public class RecordFragment extends Fragment implements View.OnClickListener {

    private TextView textView1;
    private TextView textView2;

    private StorageReference mStorageRef;
    private Uri uri;
    public static String urlName;

    private StorageReference voice_storage;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    String uid;
    DocumentReference documentReference;

    private int PERMISSION_CODE = 21;
    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recordBtn;
    private TextView filenameText;

    private String recordPermission =  Manifest.permission.RECORD_AUDIO;

    private boolean isRecording = false;

    private MediaRecorder mediaRecorder;
    private String recordFile;
    private String recordPath;

    private Chronometer timer;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn=view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);
        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        textView1 = view.findViewById(R.id.textView16);
        textView2 = view.findViewById(R.id.textResult);

        //mStorageRef = FirebaseStorage.getInstance().getReference();

        voice_storage= FirebaseStorage.getInstance().getReference();

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        documentReference= db.collection("Profiles").document(uid);

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isRecording=false;
                        }
                    });
                    alertDialog.setNegativeButton("No",null);
                    alertDialog.setTitle("Audio is still recording.");
                    alertDialog.setMessage("Are you sure you want to stop recording?");
                    alertDialog.create().show();
                }else {
                    Intent i = new Intent(v.getContext(), Menu.class);
                    RecordFragment.this.startActivity(i);
                }
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isRecording=false;
                        }
                    });
                    alertDialog.setNegativeButton("No",null);
                    alertDialog.setTitle("Audio is still recording.");
                    alertDialog.setMessage("Are you sure you want to stop recording?");
                    alertDialog.create().show();
                }else {
                    Intent i = new Intent(v.getContext(), Result.class);
                    RecordFragment.this.startActivity(i);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_list_btn:
                if(isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isRecording=false;
                        }
                    });
                    alertDialog.setNegativeButton("No",null);
                    alertDialog.setTitle("Audio is still recording.");
                    alertDialog.setMessage("Are you sure you want to stop recording?");
                    alertDialog.create().show();
                }else{
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }
                break;

            case R.id.record_btn:
                if(isRecording){
                    // Stop recording
                    stopRecording();
                    recordBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.hyeu6xg));
                    isRecording = false;
                } else {
                    // Start recording
                    if(checkPermission()) {
                        startRecording();
                        recordBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.stop_button_red));
                        isRecording = true;
                    }
                }
                break;
        }
    }

    private void stopRecording() {
        timer.stop();
        filenameText.setText("Audio recorded. \n File: " + recordFile);
        mediaRecorder.stop();
        Log.d("111","8468465454");
        UploadAudio();
        Log.d("222","8468465454");
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
        Date now = new Date();

        recordFile = "Recording_" + formatter.format(now) + ".wav";
        filenameText.setText("Audio is recording. \n File: " + recordFile);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setOutputFile(recordPath+"/"+recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private boolean checkPermission() {
        if(ActivityCompat.checkSelfPermission(getContext(),recordPermission)== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission},PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording){
            stopRecording();
        }
    }

    private void UploadAudio(){
        final String random = UUID.randomUUID().toString();
        final StorageReference riversRef = voice_storage.child(uid).child("Audio").child(random + ".wav");
            uri = Uri.fromFile(new File(recordPath+"/"+recordFile));

        riversRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Log.d("download url",url);
                        documentReference.update("Audio",url);
                        //Toast.makeText(RecordFragment.this,"Successful",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Toast.makeText(VoiceRecord.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}