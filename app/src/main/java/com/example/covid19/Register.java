package com.example.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText Registeremail,Registerpassword,Registerconfirm_password;
    Button Register_register;
    FirebaseAuth auth;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Registeremail = findViewById(R.id.register_email);
        Registerpassword = findViewById(R.id.register_password);
        Registerconfirm_password = findViewById(R.id.confirm_password);
        Register_register = findViewById(R.id.register_register);
        textView = findViewById(R.id.textView5);

        auth = FirebaseAuth.getInstance();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });

        Register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Registeremail.getText().toString();
                String password = Registerpassword.getText().toString();
                String confirm_password = Registerconfirm_password.getText().toString();

                if(email.isEmpty()){
                    Registeremail.setError("E-mail is required.");
                    return;
                }
                else if(password.isEmpty()){
                    Registerpassword.setError("Password is required.");
                    return;
                }
                else if(password.length()<8){
                    Registerpassword.setError("Password must contains at least 8 characters.");
                    return;
                }
                else if(!password.equals(confirm_password)){
                    Registerconfirm_password.setError("Password does not match.");
                    return;
                }
                else {
                    registerUser(email,password);
                }
            }
        });
    }

    private void openEmpty() {
        Intent i = new Intent(Register.this, Empty.class);
        startActivity(i);
        finish();
    }
    private void openLogin() {
        Intent i = new Intent(Register.this, Login.class);
        startActivity(i);
        finish();
    }
    private void openHomepage() {
        Intent i = new Intent(Register.this, HomePage.class);
        startActivity(i);
        finish();
    }
    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register.this,"Registration successful.",Toast.LENGTH_SHORT).show();
                    openHomepage();

                } else {
                    Toast.makeText(Register.this, "E-mail address is registred.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
/*   auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        openEmpty();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
*/