/*
SignInActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Allows user to sign in using email and password or create account
 * @author Marcus Blair
 */
public class SignInActivity extends AppCompatActivity {
    Button signInButton;
    Button createAccountButton;
    EditText enterEmailEditText;
    EditText enterPasswordEditText;
    FirebaseAuth mAuth;

    /**
     * Called when activity is created
     * Displays email and password text edit fields as well as two buttons
     * for sign in or create account
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = findViewById(R.id.sign_in);
        createAccountButton = findViewById(R.id.create_account);
        enterEmailEditText = findViewById(R.id.input_email);
        enterPasswordEditText = findViewById(R.id.input_password);

        mAuth = FirebaseAuth.getInstance();


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = enterEmailEditText.getText().toString();
                final String password = enterPasswordEditText.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreateAccount.class);
                //intent.putExtra("DATABASE", db);
                startActivity(intent);
            }
        });
    }
}

