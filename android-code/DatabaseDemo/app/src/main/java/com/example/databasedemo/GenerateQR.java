/*
GenerateQRCode
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.zxing.WriterException;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

/**
 *
 * Generates QR code and displays it on screen
 * @author Michael Antifaoff, Hussein Warsame
 */
public class GenerateQR extends AppCompatActivity{
    TextView thankYouTextView;
    QRGEncoder qrgEncoder;
    ImageView qrImage;
    Bitmap bitmap;
    String inputValue;
    String username, email;
    Button start, thumbs_up, thumbs_down;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    ListenerRegistration registration;

    LinearLayout ratingsLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    /**
     * Called when activity is created
     * Generates the QR code and displays it on the rider's screen
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        Intent i = getIntent();
        username = i.getStringExtra("username");

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        thankYouTextView = findViewById(R.id.thank_you_TextView);
        start = (Button) findViewById(R.id.start);

        new Thread(new Runnable() {
            public void run() {

                DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

                // Add a snapshot listener to the rider username in the user database
                // If the payment is complete, display a thank you message and then let the user make another request

                registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()){
                            final Request request = documentSnapshot.toObject(Request.class);

                            if (request.getPaymentComplete()) {
                                thankYouTextView.setVisibility(View.VISIBLE);
                                Log.i("Hello", "We have set the TextView to say thank you!");
                                rateDriver();

                            } else {

                                Log.i("Hello", "We are not about to start RiderDriverInitialActivity");

                                // Print QR Code to the screen
                                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                                Display display = manager.getDefaultDisplay();
                                Point point = new Point();
                                display.getSize(point);
                                int width = point.x;
                                int height = point.y;
                                int smallerDimension = width < height ? width : height;
                                smallerDimension = smallerDimension * 3 / 4;

                                qrgEncoder = new QRGEncoder(
                                        String.valueOf(request.getFare()), null,
                                        QRGContents.Type.TEXT,
                                        smallerDimension);
                                try {
                                    bitmap = qrgEncoder.encodeAsBitmap();
                                    qrImage.setImageBitmap(bitmap);
                                } catch (WriterException error) {
                                    Log.v("Hello", error.toString());
                                }
                            }
                        }


                    }
                });
            }
        }).start();

    }

    public void rateDriver(){

        Log.i("rateDriver", "we are inside rateDriver");

        ratingsLayout = findViewById(R.id.rating_layout);

        thumbs_up = findViewById(R.id.thumbs_up);
        thumbs_down = findViewById(R.id.thumbs_down);

        ratingsLayout.setVisibility(View.VISIBLE);



        thumbs_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("requests").document(username)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Request request = documentSnapshot.toObject(Request.class);

                                Driver driver = request.getDriver();
                                String driver_username = driver.getUsername();
                                double rating = driver.calculateNewRating(5);
                                driver.setRating(rating);

                                db.collection("users").document(driver_username)
                                        .set(driver)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("Driver rating", "new driver rating");

                                                DocumentReference innerRef = FirebaseFirestore.getInstance().collection("requests").document(username);
                                                innerRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        Log.i("Hello", "We have deleted the request from the database");
                                                        Intent intent = new Intent(GenerateQR.this, RiderStartActivity.class);
                                                        intent.putExtra("username", username);
                                                        intent.putExtra("driver", false);
                                                        intent.putExtra("email", email);
                                                        Log.i("Hello", "We are about to start RiderDriverInitialActivity");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }
                                        });


                            }
                        });



            }


        });

        thumbs_down.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {

                                               db.collection("requests").document(username)
                                                       .get()
                                                       .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                           @Override
                                                           public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                               Request request = documentSnapshot.toObject(Request.class);

                                                               Driver driver = request.getDriver();
                                                               String driver_username = driver.getUsername();
                                                               double rating = driver.calculateNewRating(0);
                                                               driver.setRating(rating);

                                                               db.collection("users").document(driver_username)
                                                                       .set(driver)
                                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void aVoid) {
                                                                               Log.i("Driver rating", "new driver rating");

                                                                               DocumentReference innerRef = FirebaseFirestore.getInstance().collection("requests").document(username);
                                                                               innerRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                   @Override
                                                                                   public void onComplete(@NonNull Task<Void> task) {

                                                                                       Log.i("Hello", "We have deleted the request from the database");
                                                                                       Intent intent = new Intent(GenerateQR.this, RiderStartActivity.class);
                                                                                       intent.putExtra("username", username);
                                                                                       intent.putExtra("driver", false);
                                                                                       intent.putExtra("email", email);
                                                                                       Log.i("Hello", "We are about to start RiderDriverInitialActivity");
                                                                                       startActivity(intent);
                                                                                       finish();
                                                                                   }
                                                                               });

                                                                           }
                                                                       });


                                                           }
                                                       });



                                           }


                                       }



        );









    }

    @Override
    public void onDestroy(){
        registration.remove();
        super.onDestroy();
    }

}





