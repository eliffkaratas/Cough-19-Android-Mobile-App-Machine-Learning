package com.example.covid19;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText phoneNumberText;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        phoneNumberText = findViewById(R.id.phoneNumberText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberText.getText().toString();
                if(phoneNumber.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter your mobile number.", Toast.LENGTH_SHORT).show();
                }
                else{
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+90"+phoneNumber, 60, TimeUnit.SECONDS, MainActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            signInUser(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Log.d(TAG,"Doğrulama çalışmadı."+e.getLocalizedMessage());
                        }

                        @Override
                        public void onCodeSent(@NonNull final String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(verificationID, forceResendingToken);

                            Dialog dialog = new Dialog(MainActivity.this);
                            dialog.setContentView(R.layout.verify_popup);
                            final EditText verificationCodeText = dialog.findViewById(R.id.verificationCodeText);
                            Button verifyCodeButton = dialog.findViewById(R.id.verifyCodeButton);
                            verifyCodeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String verificationCode = verificationCodeText.getText().toString();
                                    if(verificationID.isEmpty()) return;
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,verificationCode);
                                    signInUser(credential);
                                }
                            });
                            dialog.show();
                        }
                    });
                }
            }
        });
    }
    private void signInUser(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,Menu.class));
                    finish();
                }
                else{
                    Log.d(TAG,"onComplete:"+task.getException().getLocalizedMessage());
                }
            }
        });
    }
}