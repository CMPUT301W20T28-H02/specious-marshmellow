/*
CreateAccount
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Shows the user different fields to input account information, checks for valid input,
 * and creates a new {@code User}, then adds it to the database
 * @author Marcus Blair and Micheal Antifaoff
 * */
public class CreateAccount extends AppCompatActivity {
    String TAG = "CREATE_ACCOUNT";
    EditText usernameEditText;
    EditText passwordEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText addressEditText;
    Spinner driverOrRiderSpinner;
    Button finishCreateButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    boolean returnVal = true;

    /**
     * Called when the activity is created.
     * Uses {@link CreateAccount#checkInput(List) checkInput()} for valid input, then connects to
     * the database and if username does not already exist, continues
     * to {@link CreateAccount#createUser() createUser()} when valid input is provided
     * */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_test);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        addressEditText = findViewById(R.id.address);
        driverOrRiderSpinner = findViewById(R.id.driver_or_rider);
        finishCreateButton = findViewById(R.id.finish_create);

        String[] items = new String[]{"Rider", "Driver"};

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        driverOrRiderSpinner.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        final List<EditText> editTextList = new ArrayList<>();
        editTextList.add(usernameEditText);
        editTextList.add(passwordEditText);
        editTextList.add(emailEditText);

        finishCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if username, password or driver/rider are empty
                // if not create user object with the data
                // add user to db

                if (checkInput(editTextList)) {
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    String username = usernameEditText.getText().toString();
                    DocumentReference docIdRef = rootRef.collection("users").document(username);
                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    usernameEditText.setError("Username is not unique.");
                                    Toast.makeText(CreateAccount.this, "Username is not unique.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "Document does not exist!");
                                    createUser();
                                }
                            } else {
                                Log.d(TAG, "Failed with: ", task.getException());
                            }
                        }
                    });
                }
                else{
                    Log.i(TAG, "Check input returns false");
                }

            }
        });
    }

    /**
     * Checks if input text is valid
     * @param {@code List<EditText>} editTextList list of strings to validate
     * @return {@code boolean} returns true of all fields entered are valid
     */
    private boolean checkInput(List<EditText> editTextList) {
        for (EditText e : editTextList) {
            if (TextUtils.isEmpty(e.getText())) {
                e.setError(e.getHint().toString() + " is required");
                Toast.makeText(CreateAccount.this, e.getHint().toString() + "is required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (passwordEditText.getText().toString().length() < 6){
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }
        // increment Idling resource for UI test
        EspressoIdlingResource.increment();

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = rootRef.collection("users");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // decrement Idling resource
                EspressoIdlingResource.decrement();

                if (task.isSuccessful()){
                    List<User> users = task.getResult().toObjects(User.class);
                    for (User user : users){
                        if (user.getEmail().equals(emailEditText.getText().toString())){
                            emailEditText.setError("Email is in use by another user");
                            returnVal = false;
                            // checkInputReturnsFalse()
                        }
                    }
                    // checkInputReturnsTrue()

                }
            }
        });

        if(returnVal == false){
            returnVal = true;
            return false;
        }

        return true;
    }

    /**
     * Adds user to database. On successful creation, calls
     * {@link CreateAccount#finishUserCreate(String, String) finishUserCreate}
     */
    private void createUser() {
        final String password = passwordEditText.getText().toString();
        final String username = usernameEditText.getText().toString();
        final String email = emailEditText.getText().toString();

        Log.d(TAG, "We are in create user");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "We complete something");
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "We get the current user");
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                                finishUserCreate(username, email);

                                            }
                                        }
                                    });
                        }
                        else{
                            Log.d(TAG, "Task not successful");
                            Toast.makeText(CreateAccount.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /**
     * Finalizes addition of user to database by creating a User object with the given information
     * @param {@code String}username username to add
     * @param {@code String}email email to add
     */
    private void finishUserCreate(String username, String email) {
        final String phone = phoneEditText.getText().toString();
        final String address = addressEditText.getText().toString();
        final String driver = driverOrRiderSpinner.getSelectedItem().toString();
        boolean driverTemp = false;
        if (driver.equals("Driver")) {
            driverTemp = true;
        }
        final boolean driverBoolean = driverTemp;

        User currentUser = new User(username, email, new Wallet(0), phone, 5.0, 1, driverBoolean);

//        Map<String, User> userData = new HashMap<>();
//        userData.put("username", username);
//        userData.put("username", username);
//        userData.put("email", email);
//        userData.put("wallet", new Wallet(0));
//        userData.put("phone", phone);
//        userData.put("rating", 5.0);
//        userData.put("numRatings", 1);
//        userData.put("address", address);
//        userData.put("driver", driverBoolean);

        db.collection("users")
                .document(username)
                .set(currentUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(CreateAccount.this, MainActivity.class);
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