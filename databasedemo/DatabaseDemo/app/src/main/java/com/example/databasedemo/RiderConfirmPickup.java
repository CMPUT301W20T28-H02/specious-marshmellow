package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RiderConfirmPickup extends AppCompatActivity {
    Intent i;
    FirebaseFirestore db;
    CollectionReference colRef;
    DocumentReference docRef;
    TextView driver_username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_confirm_pickup);

        i = getIntent();
        final String username = i.getStringExtra("username");

        driver_username = findViewById(R.id.driverstuff);

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("requests");
        docRef = colRef.document(username);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Request request = task.getResult().toObject(Request.class);
                    Driver driver = request.getDriver();
                    String driver_name= driver.getUsername();
                    Log.d("driver_username", driver_name);

                    driver_username.setText("" + driver_name);

                }
            }
        });



    }

}
