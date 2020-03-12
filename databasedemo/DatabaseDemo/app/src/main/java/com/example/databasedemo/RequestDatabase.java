package com.example.databasedemo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RequestDatabase {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    public RequestDatabase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
    }

    public void addRequest(Request request) {
        db.collection("requests").document(request.getRider().getUsername()).set(request);
    }

    public void deleteRequest(){
        CollectionReference innerRef = FirebaseFirestore.getInstance().collection("requests");
        innerRef.document("requests")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.i(TAG, "Data deletion successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.i(TAG, "Data deletion unsuccessful");
                    }
                });
    }


}
