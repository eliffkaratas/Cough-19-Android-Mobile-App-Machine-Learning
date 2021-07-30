package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProfileView extends AppCompatActivity {
    ImageView imageView;
    TextView name,surname,phone,id,bloodtype,birthdate,chronic, back;

    FirebaseUser user;
    String uid;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference image_storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        imageView = findViewById(R.id.imageViewProfile);
        name = findViewById(R.id.read_name);
        surname = findViewById(R.id.read_surname);
        phone = findViewById(R.id.read_phonenumber);
        id = findViewById(R.id.read_tcnumber);
        bloodtype = findViewById(R.id.read_bloodtype);
        birthdate = findViewById(R.id.read_birthdate);
        chronic = findViewById(R.id.read_kronik);
        back = findViewById(R.id.textView12);
        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        documentReference= db.collection("Profiles").document(uid);



        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent( DocumentSnapshot documentSnapshot,  FirebaseFirestoreException e) {
                name.setText(documentSnapshot.getString("Name"));
                surname.setText(documentSnapshot.getString("Surname"));
               phone.setText(documentSnapshot.getString("Mobile Number"));
                id.setText(documentSnapshot.getString("ID"));
                bloodtype.setText(documentSnapshot.getString("Blood Group"));
                birthdate.setText(documentSnapshot.getString("Birth Date"));
                chronic.setText(documentSnapshot.getString("Chronic Diseases"));
                Glide.with(ProfileView.this).load(documentSnapshot.getString("Image")).into(imageView);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu();
            }
        });
    }

    private void openMenu() {
        Intent i = new Intent(ProfileView.this, Menu.class);
        startActivity(i);
        finish();
    }
}