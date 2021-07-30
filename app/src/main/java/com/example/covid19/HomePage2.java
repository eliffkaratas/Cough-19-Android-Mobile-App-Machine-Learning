package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.QueryListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class HomePage2 extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private ListView profilesList;
    private List<String> profileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page2);
        mFirestore=FirebaseFirestore.getInstance();
        profilesList = findViewById(R.id.profilesList);

        mFirestore.collection("Profiles").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                profileList.clear();
                for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                    profileList.add(snapshot.getString("İsim") + " " + snapshot.getString("Soyisim"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_selectable_list_item,
                        profileList);
                adapter.notifyDataSetChanged();
                profilesList.setAdapter(adapter);
            }
        });
    }
}