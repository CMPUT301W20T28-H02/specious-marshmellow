package com.project.speciousmarshmellow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.project.speciousmarshmellow.R;


public class DisplayUserInfoActivity extends AppCompatActivity {
    String TAG = "DISPLAY_USER_ACCOUNT_INFO";
    TextView usernameText;
    TextView emailText;
    TextView phoneText;
    TextView addressText;
    TextView ratingText;
    Button editAccount;
    Button signOut;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameText = findViewById(R.id.username_text);
        emailText = findViewById(R.id.email_text);
        phoneText = findViewById(R.id.phone_text);
        addressText = findViewById(R.id.address_text);
        ratingText = findViewById(R.id.rating_text);

        editAccount = findViewById(R.id.edit_information);
        signOut = findViewById(R.id.sign_out);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            String username = user.getDisplayName();
            String email = user.getEmail();



            displayUser(username, email);
        } else {
            // No user is signed in
            Intent intent = new Intent(getBaseContext(), SignInActivity.class);
            startActivity(intent);
        }

        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), EditContactInformation.class));
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

    }

    private void displayDriverOrRiderScreen(String username, String email){
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
                            LinearLayout linearLayout = findViewById(R.id.main_layout);
                            linearLayout.removeView((View) ratingText.getParent());
                        } else {
                            String rating = document.getData().get("rating").toString();
                            ratingText.setText(getString(R.string.show_rating, rating));
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    private void displayUser(String username, String email) {
        usernameText.setText(getString(R.string.show_username, username));
        emailText.setText(getString(R.string.show_email, email));
        final DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (document.getData().get("phone") != null) {
                            String phone = document.getData().get("phone").toString();
                            phoneText.setText(getString(R.string.show_phone, phone));
                        }
                        if (document.getData().get("address") != null) {
                            String address = document.getData().get("address").toString();
                            addressText.setText(getString(R.string.show_address, address));
                        }
                        if (!document.getBoolean("driver")) {
                            //addDriverRating(document.getData().get("rating").toString());
                            LinearLayout linearLayout = findViewById(R.id.main_layout);
                            linearLayout.removeView((View) ratingText.getParent());
                        } else {
                            String rating = document.getData().get("rating").toString();
                            ratingText.setText(getString(R.string.show_rating, rating));
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
}
