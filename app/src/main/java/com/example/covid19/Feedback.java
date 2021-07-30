package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class Feedback extends AppCompatActivity {
    EditText feedbackText;
    TextView textView;
    Button sendButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    ////// devam edecek
    FirebaseDatabase db1 = FirebaseDatabase.getInstance();

    FirebaseUser user;
    String uid;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        textView = findViewById(R.id.textView2);
        feedbackText = findViewById(R.id.editTextFeedback);
        sendButton = findViewById(R.id.sendFeedbackButton);

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();

        documentReference= db.collection("Profiles").document(uid);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback = feedbackText.getText().toString();

                documentReference.update("Feedback",feedback);

                Toast.makeText(Feedback.this,"Thanks for your feedback",Toast.LENGTH_SHORT).show();
                feedbackText.setText("");
            }
        });
    }

    private void openMenu() {
        Intent i = new Intent(Feedback.this, Menu.class);
        startActivity(i);
        finish();
    }
}