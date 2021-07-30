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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    public static ArrayList<Profile> profilesEdit;

    private TextView back;

    public static FirebaseFirestore mFirestore;

    private EditText profileNameTextEdit,profileSurnameTextEdit,profileIdTextEdit,profileMobileNumberTextEdit,
            profileAddressTextEdit,profileBirthDateTextEdit;

    private Spinner selectBloodSpinnerEdit;

    private CheckBox checkBoxCancerEdit,checkBoxHypertensionEdit,checkBoxHeartRhythmEdit,checkBoxCOPDEdit,
            checkBoxAsthmaBronchitisEdit,checkBoxDiabetesEdit;

    private Button buttonAddProfileEdit;

    FirebaseUser user;
    String uid;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static ProgressDialog editProfileProgress;

    public static HashMap<String,Object> editProfileMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profilesEdit = new ArrayList<Profile>();

        profileNameTextEdit = findViewById(R.id.profileNameEdit);
        profileSurnameTextEdit = findViewById(R.id.profileSurnameEdit);
        profileIdTextEdit = findViewById(R.id.profileIdEdit);
        profileAddressTextEdit = findViewById(R.id.profileAddressEdit);
        profileMobileNumberTextEdit = findViewById(R.id.profileMobileNumberEdit);
        profileBirthDateTextEdit = findViewById(R.id.profileBirthDateEdit);

        back = findViewById(R.id.textView15Edit);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        documentReference = db.collection("Profiles").document(uid);


        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent( DocumentSnapshot documentSnapshot,  FirebaseFirestoreException e) {
                profileNameTextEdit.setText(documentSnapshot.getString("Name"));
                profileSurnameTextEdit.setText(documentSnapshot.getString("Surname"));
                profileMobileNumberTextEdit.setText(documentSnapshot.getString("Mobile Number"));
                profileIdTextEdit.setText(documentSnapshot.getString("ID"));
                profileBirthDateTextEdit.setText(documentSnapshot.getString("Birth Date"));
                profileAddressTextEdit.setText(documentSnapshot.getString("Address"));

            }
        });




        selectBloodSpinnerEdit = findViewById(R.id.selectBloodSpinnerEdit);
        final String[] bloodType = {""};

        checkBoxCancerEdit = findViewById(R.id.checkBoxCancerEdit);
        checkBoxAsthmaBronchitisEdit = findViewById(R.id.checkBoxAsthmaBronchitisEdit);
        checkBoxCOPDEdit = findViewById(R.id.checkBoxCOPDEdit);
        checkBoxDiabetesEdit = findViewById(R.id.checkBoxDiabetesEdit);
        checkBoxHeartRhythmEdit = findViewById(R.id.checkBoxHeartRhythmEdit);
        checkBoxHypertensionEdit = findViewById(R.id.checkBoxHypertensionEdit);

        buttonAddProfileEdit = findViewById(R.id.buttonAddProfileEdit);

        editProfileProgress = new ProgressDialog(this);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.bloodtypes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectBloodSpinnerEdit.setAdapter(adapter);
        selectBloodSpinnerEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] bloodTypes = getResources().getStringArray(R.array.bloodtypes);
                bloodType[0] = bloodTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonAddProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                String[] checkedIllnesses = new String[count];
                String illness = "";
                String name = profileNameTextEdit.getText().toString();
                String surname = profileSurnameTextEdit.getText().toString();
                String address = profileAddressTextEdit.getText().toString();
                String id = profileIdTextEdit.getText().toString();
                String mobileNumber = profileMobileNumberTextEdit.getText().toString();
                String birthDate = profileBirthDateTextEdit.getText().toString();

                String image = "";
                String audio = "";
                String feedback = "";

                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(surname) || !TextUtils.isEmpty(address)
                        || TextUtils.isEmpty(id) || TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(birthDate)) {
                    if (checkBoxCancerEdit.isChecked()) {
                        illness += checkBoxCancerEdit.getText().toString() + ",";
                    }
                    if (checkBoxAsthmaBronchitisEdit.isChecked()) {
                        illness += checkBoxAsthmaBronchitisEdit.getText().toString() + ",";
                    }
                    if (checkBoxCOPDEdit.isChecked()) {
                        illness += checkBoxCOPDEdit.getText().toString() + ",";
                    }
                    if (checkBoxDiabetesEdit.isChecked()) {
                        illness += checkBoxDiabetesEdit.getText().toString() + ",";
                    }
                    if (checkBoxHeartRhythmEdit.isChecked()) {
                        illness += checkBoxHeartRhythmEdit.getText().toString() + ",";
                    }
                    if (checkBoxHypertensionEdit.isChecked()) {
                        illness += checkBoxHypertensionEdit.getText().toString() + ",";
                    }
                    if (!checkBoxHypertensionEdit.isChecked() && !checkBoxHeartRhythmEdit.isChecked() && !checkBoxDiabetesEdit.isChecked()
                            && !checkBoxCOPDEdit.isChecked() && !checkBoxAsthmaBronchitisEdit.isChecked() && !checkBoxCancerEdit.isChecked()) {
                        illness = "No chronic dieases.";
                    }
                    editProfileProgress.setMessage("Please wait...");
                    editProfileProgress.setCanceledOnTouchOutside(false);
                    editProfileProgress.show();
                    EditProfile(name, surname, id, birthDate, mobileNumber, address, bloodType[0], illness, image, audio, feedback);
                    profilesEdit.add(new Profile(name, surname, id, birthDate, mobileNumber, address, bloodType[0], illness));
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackToMenu();
            }
        });

    }

    private void EditProfile(String name, String surname, String id, String birthDate, String mobileNumber,
                            String address, String bloodType, String illness, String image, String audio, String feedback){
        editProfileMap.put("Time", FieldValue.serverTimestamp());
        editProfileMap.put("Name",name);
        editProfileMap.put("Surname",surname);
        editProfileMap.put("ID",id);
        editProfileMap.put("Birth Date",birthDate);
        editProfileMap.put("Mobile Number",mobileNumber);
        editProfileMap.put("Address",address);
        editProfileMap.put("Blood Group", bloodType);
        editProfileMap.put("Chronic Diseases", illness);
        editProfileMap.put("Image",image);
        editProfileMap.put("Audio",audio);
        editProfileMap.put("Feedback",feedback);

        documentReference.set(editProfileMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfile.this,"Profile is edited.",Toast.LENGTH_SHORT).show();
                        BackToMenu();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BackToMenu() {
        Intent i = new Intent(EditProfile.this, Menu.class);
        startActivity(i);
        finish();
    }
}