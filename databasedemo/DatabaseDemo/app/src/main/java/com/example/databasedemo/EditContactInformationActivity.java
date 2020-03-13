package com.example.databasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditContactInformationActivity extends AppCompatActivity {
    String TAG = "EDIT_CONTACT_INFO";
    TextView currentEmail;
    TextView currentPhone;
    EditText newEmail;
    EditText newPhone;
    EditText inputPassword;
    Button finishEdit;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // pass username of the account that should be editted in intent
        // this can be changed
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_info);

        currentEmail = findViewById(R.id.email_edit_text);
        currentPhone = findViewById(R.id.phone_edit_text);

        newEmail = findViewById(R.id.email_edit);
        newPhone = findViewById(R.id.phone_edit);
        inputPassword = findViewById(R.id.input_password);

        finishEdit = findViewById(R.id.finish_edit);

        db = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        final String username = i.getStringExtra("username");

        // get the user class by the username given
        final DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // display the current email and phone
                    User user = task.getResult().toObject(User.class);
                    currentEmail.setText(getString(R.string.new_email, user.getEmail()));
                    currentPhone.setText(getString(R.string.new_phone, user.getPhone()));
                    reAuthUser(user);
                }
            }
        });
    }

    private void reAuthUser(final User user) {
        // edit the account info
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser authUser = mAuth.getCurrentUser();

        // reauthorise the user to change their email
        // this is only needed for updating the email but is required regardless of input
        // can be updated later
        finishEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(inputPassword.getText())) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), inputPassword.getText().toString());

                    // Prompt the user to re-provide their sign-in credentials
                    authUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "User re-authenticated.");
                                    updateUser(user, authUser);
                                }
                            });
                } else {
                    Toast.makeText(EditContactInformationActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateUser(final User user, final FirebaseUser authUser) {
        // update the user
        final String username = user.getUsername();
        if (!TextUtils.isEmpty(newEmail.getText())) {
            // check if new email is supplied
            // if so change the auth email and the object email
            authUser.updateEmail(newEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // only change class email if the auth email change is successful
                                Log.d(TAG, "User email address updated.");
                                user.setEmail(newEmail.getText().toString());
                            } else {
                                Log.d(TAG, "Email address update failed");
                            }
                        }
                    });
        }
        if (!TextUtils.isEmpty(newPhone.getText())) {
            // check if new phone is supplied
            // if so update object phone
            user.setPhone(newPhone.getText().toString());
        }

        // upload changed class to firestore
        // on success go to main activity
        db.collection("users")
                .document(username)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(EditContactInformationActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}