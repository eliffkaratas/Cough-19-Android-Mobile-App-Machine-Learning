package com.example.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Result extends AppCompatActivity {
    TextView textView;
    TextView textResult;

    FirebaseUser user;
    String uid;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textView = findViewById(R.id.textView3);
        textResult = findViewById(R.id.textView18);

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();

        readData(uid);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudio();
            }
        });
    }
    private void openAudio() {
        Intent i = new Intent(Result.this, Audio.class);
        startActivity(i);
        finish();
    }
    private void readData(String uid) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");
        databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists())
                    {
                        Toast.makeText(Result.this,"Successfully get result.", Toast.LENGTH_SHORT).show();
                        DataSnapshot dataSnapshot = task.getResult();
                        String data = String.valueOf(dataSnapshot.child("Accuracy").getValue());
                        /*int j = Integer.parseInt(data);
                        j = 80;
                        for(int i = 0; i<=j; i++) {
                            textResult.setText(i + "%");
                        }*/
                        textResult.setText(data + "%");
                    }
                    else {
                        Toast.makeText(Result.this,"User doesn't exist.",Toast.LENGTH_SHORT).show();
                    }


                }else {

                    Toast.makeText(Result.this,"Failed to get result.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}