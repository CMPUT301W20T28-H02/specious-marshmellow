package com.example.databasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditContactInformation extends AppCompatActivity {
    String TAG = "EDIT_CONTACT_INFO";
    TextView currentEmail;
    TextView currentPhone;
    EditText newEmail;
    EditText newPhone;
    Button finishEdit;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_info);

        currentEmail = findViewById(R.id.email_edit_text);
        currentPhone = findViewById(R.id.phone_edit_text);

        newEmail = findViewById(R.id.email_edit);
        newPhone = findViewById(R.id.phone_edit);

        finishEdit = findViewById(R.id.finish_edit);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("users").document(user.getDisplayName());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (document.getData().get("phone") != null) {
                            String phone = document.getData().get("phone").toString();
                            currentPhone.setText(getString(R.string.new_phone, phone));
                        }
                    }
                }
            }
        });
        currentEmail.setText(getString(R.string.new_email, user.getEmail()));

        finishEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(newEmail.getText())) {
                    user.updateEmail(newEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User email address updated.");
                                    } else {
                                        Log.d(TAG, "Email address update failed");
                                    }
                                }
                            });
                }
                if (!TextUtils.isEmpty(newPhone.getText())) {
                    String phone = newPhone.getText().toString();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("phone", phone);

                    db.collection("users")
                            .document(user.getDisplayName())
                            .update(userData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                }

                Intent intent = new Intent(EditContactInformation.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }
}
