package com.example.android_nosql;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{

    private static final String TAG = "Main Activity";
    private EditText enterTitle;
    private EditText enterThought;
    private Button saveButton,showButton,updateTitle;
    private TextView recTitle,recThought;

    //keys

    public static final String KEY_TITLE = "title";
    public static final String KEY_THOUGHT= "thought";

    //connection firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference journalRef = db.collection("Journal")
            .document("First Thoughts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveButton = findViewById(R.id.save_button);
        updateTitle = findViewById(R.id.update_data);
        enterTitle = findViewById(R.id.edit_text_title);
        enterThought = findViewById(R.id.edit_text_thoughts);
        recThought = findViewById((R.id.rec_thought));
        recTitle = findViewById(R.id.rec_title);
        showButton = findViewById(R.id.show_data);

        updateTitle.setOnClickListener(this);

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    journalRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){

                                        String title = documentSnapshot.getString(KEY_TITLE);
                                        String thought = documentSnapshot.getString(KEY_THOUGHT);

                                        recTitle.setText(title);
                                        recThought.setText(thought);
                                    }else{
                                        Toast.makeText(MainActivity.this,
                                                "No Data Exists",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull  Exception e) {
                                   Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = enterTitle.getText().toString().trim();
                String thought = enterThought.getText().toString().trim();

                Map<String,Object> data = new HashMap<>();
                data.put(KEY_TITLE,title);
                data.put(KEY_THOUGHT,thought);


                journalRef.set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this,
                                        "Success",Toast.LENGTH_LONG)
                                        .show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"onFailure:"+e.toString());
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        journalRef.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable  DocumentSnapshot documentSnapshot, @Nullable  FirebaseFirestoreException e) {

                if(e !=null){

                    Toast.makeText(MainActivity.this,"Something went wrong",
                            Toast.LENGTH_LONG)
                            .show();
                }
                if(documentSnapshot != null && documentSnapshot.exists()){

                    String title = documentSnapshot.getString(KEY_TITLE);
                    String thought = documentSnapshot.getString(KEY_THOUGHT);

                    recTitle.setText(title);
                    recThought.setText(thought);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_data:
                //call update
                UpdateAll();
                break;
            
        }
    }

    private void UpdateAll() {

        String title = enterTitle.getText().toString().trim();
        String thought = enterThought.getText().toString().trim();

        Map<String,Object> data = new HashMap<>();
        data.put(KEY_TITLE,title);
        data.put(KEY_THOUGHT,thought);

        journalRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(MainActivity.this,"Updated!",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {

            }
        });

    }
}