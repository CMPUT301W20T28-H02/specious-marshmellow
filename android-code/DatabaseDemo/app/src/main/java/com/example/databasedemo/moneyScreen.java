/*
moneyScreen
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

/**
 * Displays available money and allows user to add more
 * @author Sirjan Chawla
 */
public class moneyScreen extends AppCompatActivity {
    Button btn5, btn25, btn50, makeReq;
    TextView bal, bal_setter;

    /**
     *  Shows current balance and three buttons that allow user to add more money to their wallet
     *  balance is taken from database as well as updated when user adds more
     * @param {@code Bundle}savedInstanceState Information passed from the caller activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_screen);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        btn5 = findViewById(R.id.add_5);
        bal = findViewById(R.id.balance);
        bal_setter = findViewById(R.id.balance_set);
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Rider rider = task.getResult().toObject(Rider.class);
                            Wallet wallet = rider.getWallet();
                            wallet.deposit(5.0);
                            rider.setWallet(wallet);
                            docRef.set(rider);
                        }
                    }
                });
            }
        });
        btn25=  findViewById(R.id.add_25);
        btn25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Rider rider = task.getResult().toObject(Rider.class);
                            Wallet wallet = rider.getWallet();
                            wallet.deposit(25.0);
                            rider.setWallet(wallet);
                            docRef.set(rider);
                        }
                    }
                });
            }
        });
        btn50 = findViewById(R.id.add_50);
        btn50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Rider rider = task.getResult().toObject(Rider.class);
                            Wallet wallet = rider.getWallet();
                            wallet.deposit(50.0);
                            rider.setWallet(wallet);
                            docRef.set(rider);
                        }
                    }
                });
            }
        });
        makeReq =  findViewById(R.id.back_home);
        makeReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
                intent.putExtra("driver", false);
                startActivity(intent);
            }
        });
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Rider rider = documentSnapshot.toObject(Rider.class);
                Wallet wallet = rider.getWallet();
                bal_setter.setText(String.valueOf(wallet.getBalance()));
            }
        });

        /*docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Rider rider = task.getResult().toObject(Rider.class);
                    Wallet wallet = rider.getWallet();
                    bal_setter.setText(String.valueOf(wallet.getBalance()));

                }
            }
        });
*/






    }
}
