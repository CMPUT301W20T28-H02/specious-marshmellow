package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    boolean riderOrDriver;
    private static String TAG = "DISPLAY_USER_ACCOUNT_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            String username = user.getDisplayName();
            String email = user.getEmail();

            displayDriverOrRiderScreen(username, email);
            Log.d(TAG, "When we do get here?");

        } else {
            // No user is signed in
            Intent intent = new Intent(getBaseContext(), SignInActivity.class);
            startActivity(intent);
        }

    }

    private boolean displayDriverOrRiderScreen(String username, String email){
        final DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (!document.getBoolean("driver")) {
                            //addDriverRating(document.getData().get("rating").toString());
                            Intent intent = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
                            intent.putExtra("driver", false);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
                            intent.putExtra("driver", true);
                            startActivity(intent);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                Log.d(TAG, "We get here inside 1, " + riderOrDriver);
            }
        });
        Log.d(TAG, "We get here inside 2, " + riderOrDriver);
        return riderOrDriver;
    }
}
