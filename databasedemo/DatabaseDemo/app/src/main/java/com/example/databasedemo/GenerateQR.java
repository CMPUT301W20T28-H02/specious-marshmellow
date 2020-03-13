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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    String TAG = "GenerateQRCode";
    TextView thankYouTextView;
    QRGEncoder qrgEncoder;
    ImageView qrImage;
    Bitmap bitmap;
    String inputValue;
    Button start, save;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";

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
        final String username = i.getStringExtra("username");

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        thankYouTextView = findViewById(R.id.thank_you_TextView);
        start = (Button) findViewById(R.id.start);

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Request request = documentSnapshot.toObject(Request.class);



                if (request.getPaymentComplete()){

                    Log.i("Hello", "We are here 1");

                    thankYouTextView.setText("Thank you for riding with Marshmellow!");

                    Log.i("Hello", "We are here 2");

                    final Intent i = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
                    i.putExtra("username", username);
                    i.putExtra("driver", false);
                    i.putExtra("email", request.getRider().getEmail());

                    /*try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch(InterruptedException interrupted){
                        Log.e("Hello", interrupted.toString());
                    }*/
                    Log.i("Hello", "We are here 3");


                    final Runnable r = new Runnable() {
                        public void run() {
                            startActivity(i);
                        }
                    };

                    final Handler handler = new Handler();
                    handler.postDelayed(r,5000);


                }
                else {
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
                        Log.v(TAG, error.toString());
                    }
                }

            }
        });

//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()){
//                    Request request = task.getResult().toObject(Request.class);
//                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
//                    Display display = manager.getDefaultDisplay();
//                    Point point = new Point();
//                    display.getSize(point);
//                    int width = point.x;
//                    int height = point.y;
//                    int smallerDimension = width < height ? width : height;
//                    smallerDimension = smallerDimension * 3 / 4;
//
//                    qrgEncoder = new QRGEncoder(
//                            String.valueOf(request.getFare()), null,
//                            QRGContents.Type.TEXT,
//                            smallerDimension);
//                    try {
//                        bitmap = qrgEncoder.encodeAsBitmap();
//                        qrImage.setImageBitmap(bitmap);
//                    } catch (WriterException e) {
//                        Log.v(TAG, e.toString());
//                    }
//                }
//            }
//        });





    }


}





