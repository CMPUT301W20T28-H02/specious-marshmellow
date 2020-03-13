/*
    "take picture'
    https://www.youtube.com/watch?v=LpL9akTG4hI

    load image
    https://www.youtube.com/watch?v=OPnusBmMQTw
 */


package com.example.simpleparadox.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Button mCaptureBtn;
    Button loadImageBtn;
    private static final int PICK_IMAGE = 100;

    ImageView mImageView;

    // this for take image
    Uri image_uri;
    // this for load image, possibly can combine the two into one variable?
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById( R.id.image_view );
        mCaptureBtn = findViewById( R.id.capture_image_btn );
        loadImageBtn = findViewById( R.id.load_image );


        //Button click
        mCaptureBtn.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
                {
                    if( checkSelfPermission( Manifest.permission.CAMERA ) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE ) ==
                            PackageManager.PERMISSION_DENIED )
                    {
                        // permission not enabled, request it
                        String[] permission = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
                        // show popup to request permsision
                        requestPermissions( permission, PERMISSION_CODE );
                    } else {
                        //permsision already granted
                        openCamera();
                    }
                } else {
                    // system os < marshamallow
                }
            }
        });


        loadImageBtn.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


    }


    private void openGallery()
    {
        Intent gallery = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI );
        startActivityForResult( gallery, PICK_IMAGE );
    }


//    @Override
//    protected void onLoadImageActivityResult( int requestCode, int resultCode, Intent data )
//    {
//        super.onLoadImageActivityResult( requestCode, resultCode, data );
//    }


    private void openCamera()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture" );
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera" );
        image_uri = getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values );
        // camera intent
        Intent cameraIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        cameraIntent.putExtra( MediaStore.EXTRA_OUTPUT, image_uri );
        startActivityForResult( cameraIntent, IMAGE_CAPTURE_CODE );
    }


    // handling permsission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        // this method caleed when user presses allow or deny from permission request popup
        switch( requestCode )
        {
            case PERMISSION_CODE: {
                if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    // permission from popup was granted
                    openCamera();
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied ...", Toast.LENGTH_SHORT ).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult( requestCode, resultCode, data );
        // called when image captured from camera
        if( resultCode == RESULT_OK && requestCode == PICK_IMAGE )
        {
            image_uri = data.getData();
            mImageView.setImageURI( image_uri );
        } else if ( resultCode == RESULT_OK ) {
            // set image captured to our image view

            mImageView.setImageURI( image_uri );
        }
    }
}
