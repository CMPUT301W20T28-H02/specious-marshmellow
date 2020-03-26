package com.example.databasedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ProfilePictureFromFirebaseStorage extends AppCompatActivity {

    ImageView firebaseImage;
    ImageView profile;
    static String url;
    DatabaseReference reff;

    FirebaseAuth mAuth;
    String username;

    boolean hasProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_from_firebase_storage);

        firebaseImage = findViewById( R.id.firebase_image );

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        username = user.getDisplayName();


        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);


//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                Rider rider = documentSnapshot.toObject(Rider.class);
//                hasProfilePicture = rider.getHasProfilePicture();
//                String temp = String.valueOf( hasProfilePicture );
//                Toast.makeText(ProfilePictureFromFirebaseStorage.this, temp, Toast.LENGTH_SHORT).show();
//                Toast.makeText( ProfilePictureFromFirebaseStorage.this, username, Toast.LENGTH_SHORT).show();
//
//            }
//        });


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Rider rider = task.getResult().toObject(Rider.class);
                    hasProfilePicture = rider.getHasProfilePicture();
                }

                if( hasProfilePicture )
                {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child(username);
                } else {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child("Will_be_username");
                }



                // here gonna have to adjust reff to accurately go to the correct
                // user, so i think add an if statement
                // at dataSnapshot.child("//username").getValue().toString();


                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        url = dataSnapshot.child("imageUrl").getValue().toString();


                        Log.d("Firebase", url);
                        Picasso.get()
                                .load( url )
                                .into( firebaseImage );


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        });
    }





}

