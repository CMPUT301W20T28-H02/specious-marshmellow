package com.example.databasedemo;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.databasedemo.Rider;
import com.example.databasedemo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ImageLoader {


    public static void loadImage(final CircularImageView picture, final String username) {
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                boolean hasProfilePicture = false;
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    hasProfilePicture = user.getHasProfilePicture();
                }

                Log.i("Hello", "Username is " + username + " and whether they have a profile photo is " + hasProfilePicture);

                DatabaseReference reff;
                if (hasProfilePicture) {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child(username);
                } else {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child("Will_be_username");
                }
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String url = dataSnapshot.child("imageUrl").getValue().toString();
                        Log.d("Firebase", url);
                        Picasso.get().load(url).into(picture);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
