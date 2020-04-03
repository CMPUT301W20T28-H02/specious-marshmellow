/*
GenerateQRCode
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
 * @author Michael Antifaoff, Hussein Warsame, Aanand Shekhar Roy.
 * Please see this medium post:
 * https://medium.com/@aanandshekharroy/generate-barcode-in-android-app-using-zxing-64c076a5d83a.
 * The article helped us enable QR Generation via ZXING barcode library.
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
        email = i.getStringExtra("email");

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
                                promptRating();

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

    /**
     *  Shows AlertDialog with thumbs up and thumbs down options
     *  to rate {@code Driver}.
     */
    //
    public void promptRating(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Add the buttons
        builder.setPositiveButton(getString(R.string.thumbs_up), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User thumbs up the driver
                setDriverRating(5);
            }
        });
        builder.setNegativeButton(getString(R.string.thumbs_down), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User thumbs down the driver
                setDriverRating(0);
            }
        });

        // do nothing when user touches area outside dialog
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });


        builder.setMessage(getString(R.string.ratings_message))
                .setTitle(getString(R.string.ratings_title))
                .setCancelable(false);


        AlertDialog dialog = builder.create();

        dialog.show();

        Log.i("rateDriver", "we are inside rateDriver");


    }
    /**
     *   get {@code Driver} object from {@code Request} collection in Firestore database
     *   and sets new driver rating locally.
     *
     */
    //
    public void setDriverRating(final double rating){
        db.collection("requests").document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Request request = documentSnapshot.toObject(Request.class);

                        Driver driver = request.getDriver();
                        String driver_username = driver.getUsername();
                        double new_rating = driver.calculateNewRating(rating);
                        driver.setRating(new_rating);

                        changeDriverRatingFirestore(driver_username, driver);


                    }
                });
    }

    /**
     *   Changes {@code Driver} rating in Firestore and moves to
     *  {@code RiderStartActivity}.
     */
    //
    public void changeDriverRatingFirestore(String driver_username, Driver driver) {

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



    @Override
    public void onDestroy(){
        registration.remove();
        super.onDestroy();
    }

}





