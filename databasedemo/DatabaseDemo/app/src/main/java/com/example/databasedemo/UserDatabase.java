package com.example.databasedemo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDatabase {

    public User getCurrentUser(){
        User userObj;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String TAG = "Error";
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(user.getDisplayName());
        DocumentSnapshot doc = docRef.get().getResult();
        return doc.toObject(User.class);
    }

}
