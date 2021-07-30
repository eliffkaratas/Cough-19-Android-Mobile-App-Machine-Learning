package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;


public class HomePage extends AppCompatActivity {
    public static ArrayList<Profile> profiles;

    public static FirebaseFirestore mFirestore;

    private EditText profileNameText,profileSurnameText,profileIdText,profileMobileNumberText,
            profileAddressText,profileBirthDateText;

    private RadioGroup rGroup;
    private RadioButton rButton;

    private Spinner selectBloodSpinner;

    private CheckBox checkBoxCancer,checkBoxHypertension,checkBoxHeartRhythm,checkBoxCOPD,
            checkBoxAsthmaBronchitis,checkBoxDiabetes;

    private Button buttonAddProfile;

    FirebaseUser user;
    String uid;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static ProgressDialog addProfileProgress;

    public static HashMap<String,Object> profileMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mFirestore=FirebaseFirestore.getInstance();

        profiles = new ArrayList<Profile>();

        rGroup= findViewById((R.id.Smoking));
        profileNameText=findViewById(R.id.profileName);
        profileSurnameText=findViewById(R.id.profileSurname);
        profileIdText=findViewById(R.id.profileId);
        profileMobileNumberText=findViewById(R.id.profileMobileNumber);
        profileBirthDateText=findViewById(R.id.profileBirthDate);
        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        documentReference= db.collection("Profiles").document(uid);

        selectBloodSpinner=findViewById(R.id.selectBloodSpinner);
        final String[] bloodType = {""};

        checkBoxCancer=findViewById(R.id.checkBoxCancer);
        checkBoxAsthmaBronchitis=findViewById(R.id.checkBoxAsthmaBronchitis);
        checkBoxCOPD=findViewById(R.id.checkBoxCOPD);
        checkBoxDiabetes=findViewById(R.id.checkBoxDiabetes);
        checkBoxHeartRhythm=findViewById(R.id.checkBoxHeartRhythm);
        checkBoxHypertension=findViewById(R.id.checkBoxHypertension);

        buttonAddProfile=findViewById(R.id.buttonAddProfile);

        addProfileProgress = new ProgressDialog(this);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.bloodtypes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectBloodSpinner.setAdapter(adapter);
        selectBloodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] bloodTypes = getResources().getStringArray(R.array.bloodtypes);
                bloodType[0] = bloodTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                String[] checkedIllnesses = new String[count];

                String smoking = "";
                String illness = "";
                String name = profileNameText.getText().toString();
                String surname = profileSurnameText.getText().toString();
                String id = profileIdText.getText().toString();
                String mobileNumber = profileMobileNumberText.getText().toString();
                String birthDate = profileBirthDateText.getText().toString();

                String image = "";
                String audio = "";
                String feedback = "";

                if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(surname) || TextUtils.isEmpty(id) || TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(birthDate)){
                    if(checkBoxCancer.isChecked()){
                        illness += checkBoxCancer.getText().toString() + ",";
                    }
                    if(checkBoxAsthmaBronchitis.isChecked()){
                        illness += checkBoxAsthmaBronchitis.getText().toString() + ",";
                    }
                    if(checkBoxCOPD.isChecked()){
                        illness += checkBoxCOPD.getText().toString() + ",";
                    }
                    if(checkBoxDiabetes.isChecked()){
                        illness += checkBoxDiabetes.getText().toString() + ",";
                    }
                    if(checkBoxHeartRhythm.isChecked()){
                        illness += checkBoxHeartRhythm.getText().toString() + ",";
                    }
                    if(checkBoxHypertension.isChecked()){
                        illness += checkBoxHypertension.getText().toString() + ",";
                    }
                    if(!checkBoxHypertension.isChecked() && !checkBoxHeartRhythm.isChecked() && !checkBoxDiabetes.isChecked()
                    && !checkBoxCOPD.isChecked() && !checkBoxAsthmaBronchitis.isChecked() && !checkBoxCancer.isChecked()){
                        illness = "No chronic dieases.";
                    }

                    int s = rGroup.getCheckedRadioButtonId();
                    rButton = findViewById(s);

                    if(rButton!=null)
                    {
                        smoking = rButton.getText().toString();
                    }
                    addProfileProgress.setMessage("Please wait...");
                    addProfileProgress.setCanceledOnTouchOutside(false);
                    addProfileProgress.show();
                    addProfile(name,surname,id,birthDate,mobileNumber,bloodType[0],illness,image,audio,feedback,smoking);
                    profiles.add(new Profile(name,surname,id,birthDate,mobileNumber,bloodType[0],illness,smoking));
                }
            }
        });
    }

    private void openEmpty() {
        Intent i = new Intent(HomePage.this, Empty.class);
        startActivity(i);
        finish();
    }
    private void openMenu() {
        Intent i = new Intent(HomePage.this, Menu.class);
        startActivity(i);
        finish();
    }

    private void addProfile(String name, String surname, String id, String birthDate, String mobileNumber,
                            String bloodType, String illness, String image, String audio, String feedback, String smoking){
        profileMap.put("Time", FieldValue.serverTimestamp());
        profileMap.put("Name",name);
        profileMap.put("Surname",surname);
        profileMap.put("ID",id);
        profileMap.put("Birth Date",birthDate);
        profileMap.put("Mobile Number",mobileNumber);
        profileMap.put("Blood Group", bloodType);
        profileMap.put("Chronic Diseases", illness);
        profileMap.put("Image",image);
        profileMap.put("Audio",audio);
        profileMap.put("Feedback",feedback);
        profileMap.put("Smoking",smoking);

        documentReference.set(profileMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomePage.this,"Profile is saved.",Toast.LENGTH_SHORT).show();
                        openMenu();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(HomePage.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}