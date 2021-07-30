package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Menu extends AppCompatActivity {
    private Button addProfile;
    private Button listProfiles;
    private Button imageButton;
    private Button audioButton;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        addProfile = (Button)findViewById(R.id.addProfile);
        listProfiles = (Button)findViewById(R.id.profilesList);
        imageButton = (Button)findViewById(R.id.imageButton);
        audioButton = (Button)findViewById(R.id.audioButton);
        textView = findViewById(R.id.textFeedback);

        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homePageIntent = new Intent (Menu.this,EditProfile.class);
                startActivity(homePageIntent);
            }
        });

        listProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homePageIntent2 = new Intent (Menu.this,ProfileView.class);
                startActivity(homePageIntent2);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImageIntent = new Intent (Menu.this,Image.class);
                startActivity(uploadImageIntent);
            }
        });

        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uplaodAudioIntent = new Intent (Menu.this,Audio.class);
                startActivity(uplaodAudioIntent);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uplaodFeedbackIntent = new Intent (Menu.this,Feedback.class);
                startActivity(uplaodFeedbackIntent);
            }
        });
    }
}