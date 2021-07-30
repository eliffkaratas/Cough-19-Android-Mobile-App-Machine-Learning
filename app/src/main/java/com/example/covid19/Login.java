package com.example.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    FirebaseAuth auth;
    Button login;
    EditText LoginEmail,LoginPassword;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login_button);
        LoginEmail =findViewById(R.id.login_email);
        LoginPassword = findViewById(R.id.login_password);
        auth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.textview99);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = LoginEmail.getText().toString();
                String password = LoginPassword.getText().toString();

                if(email.isEmpty()){
                    LoginEmail.setError("E-mail is required.");
                    return;
                }
                else if(password.isEmpty()){
                    LoginPassword.setError("Password is required.");
                    return;
                }
                else {
                    LoginUser(email,password);
                }
            }
        });
    }

    private void LoginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(Login.this,"Login successful.",Toast.LENGTH_SHORT).show();
                openMenu();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(Login.this,"User not found.",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openEmpty() {
        Intent i = new Intent(Login.this, Empty.class);
        startActivity(i);
        finish();
    }
    private void openRegister() {
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
        finish();
    }
    private void openProfile() {
        Intent i = new Intent(Login.this, Profile.class);
        startActivity(i);
        finish();
    }
    private void openMenu() {
        Intent i = new Intent(Login.this, Menu.class);
        startActivity(i);
        finish();
    }
}