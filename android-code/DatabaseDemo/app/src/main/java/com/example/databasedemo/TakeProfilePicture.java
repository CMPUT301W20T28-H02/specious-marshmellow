package com.example.databasedemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

/**
 * Adds profile picture functionality to the app. Adds a image the user can set
 * to be associated with their profile and stores the image in the Firebase Storage
 * with a Firebase Realtime Database key referring to the logged in user and their
 * profile picture.
 * @author Johnas Wong
 */
public class TakeProfilePicture extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Button mCaptureBtn;
    Button loadImageBtn;
    Button mButtonUpload;
    Button done_add;
    ProgressBar mProgressBar;
    private static final int PICK_IMAGE = 100;
    ImageView mImageView;
    Uri image_uri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    StorageTask mUploadTask;
    String thumb_download_url;
    FirebaseAuth mAuth;
    String username;
    DatabaseReference reff;
    boolean hasProfilePicture;


    /**
     * Called when activity is created
     * Accesses the user object stored in database to retrieve their username and check whether
     * the user already has a profile picture or not. If user does not have a profile picture set,
     * their picture is set as the app default using the app logo.  Calls
     * {@link TakeProfilePicture#uploadFile() uploadFile()} when user selects an image to upload
     * as their picture.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_profile_picture);

        mImageView = findViewById( R.id.image_view );
        mCaptureBtn = findViewById( R.id.capture_image_btn );
        loadImageBtn = findViewById( R.id.load_image );
        mButtonUpload = findViewById( R.id.btn_upload_image );
        done_add=findViewById(R.id.done);
        mProgressBar = findViewById( R.id.progress_bar );
        mStorageRef = FirebaseStorage.getInstance().getReference("Profile pictures");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Profile pictures");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        username = user.getDisplayName();
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);


        /**
         * Create temporary rider object simply to retrieve hasProfilePicture boolean of the user
         */
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Rider rider = documentSnapshot.toObject(Rider.class);
                hasProfilePicture = rider.getHasProfilePicture();
            }
        });


        /**
         * On completion of grabbing data from the database. If the user already has a profile
         * picture, sets the image view as their current picture. Otherwise, sets the image view
         * as the app default picture.
         */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Rider rider = task.getResult().toObject(Rider.class);
                    hasProfilePicture = rider.getHasProfilePicture();
                }

                if( hasProfilePicture )
                {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child(username);
                } else {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child("Will_be_username");
                }
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String url = dataSnapshot.child("imageUrl").getValue().toString();
                        Picasso.get()
                                .load( url )
                                .into( mImageView );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });


        /**
         * "Take a picture" button was selected. Checks the current set permissions of the device
         * first. If camera functionality was not previously allowed, prompts user to allow to
         * continue with the take picture functionality. If already allowed, continue.
         */
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
                        // Permission not enabled, request it
                        String[] permission = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
                        // Show popup to request permission
                        requestPermissions( permission, PERMISSION_CODE );
                    } else {
                        // permission already granted
                        openCamera();
                    }
                } else {
                    // System os < marshmallow, can continue
                }
            }
        });


        /**
         * "Load a picture" button was selected. Opens current device's gallery to choose
         * a photo to upload.
         */
        loadImageBtn.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        /**
         * Upload picture button.
         */
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });


        /**
         * "Return" Button. Returns you to the previous activity
         */
        done_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    /**
     * Gets the extension of the file to upload
     * @param uri
     * @return extension type of file
     */
    private String getFileExtension( Uri uri )
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType( cR.getType( uri ) );
    }


    /**
     * Prepares the selected image for uploading and uploads it to the Firebase Storage as well as
     * to the Firebase Realtime Database
     */
    private void uploadFile()
    {
        //----------------------------------------------------------------------------------
        // getUploadSessionUri() instead of depreciated taskSnapshot.getDownloadURL
        // from Waleed Younis

        if (image_uri != null)
        {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(image_uri));

            mUploadTask = fileReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    // Continue with the task to get the download URL
                                    return fileReference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {

                                        // Get and convert to string the image online URL
                                        thumb_download_url = task.getResult().toString();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                mProgressBar.setProgress(0);
                                            }
                                        }, 500);
                                        DynamicToast.make(TakeProfilePicture.this, getString(R.string.upload_successful), Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

                                        Upload upload = new Upload( "Profile Picture", thumb_download_url );     // here, need to get a valid uri or a valid url
                                        mDatabaseRef.child(username).setValue(upload);
                                        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);

                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                // Update the user to 'has a profile picture'
                                                if(task.isSuccessful()){

                                                    User user = task.getResult().toObject(User.class);
                                                    Driver driver;
                                                    Rider rider;
                                                    if(user.getDriver()){

                                                        driver = new Driver(user);
                                                        driver.setHasProfilePicture( true );
                                                        docRef.set(driver);
                                                    } else {
                                                        Log.i("Hello", "We are about to cast " + user.getUsername() + " to a Rider");
                                                        rider = new Rider(user);
                                                        Log.i("Hello", "We just cast " + rider.getUsername() + " to a Rider");
                                                        rider.setHasProfilePicture( true );
                                                        docRef.set(rider);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            DynamicToast.make(TakeProfilePicture.this, e.getMessage(), Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            DynamicToast.make(TakeProfilePicture.this, getString(R.string.no_file_selected), Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
        }
    }
    //----------------------------------------------------------------------------------


    /**
     * Open device's image gallery
     */
    private void openGallery()
    {
        Intent gallery = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI );
        startActivityForResult( gallery, PICK_IMAGE );
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }


    /**
     * Access device's camera
     */
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


    /**
     * Handle device's permissions
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        // this method called when user presses allow or deny from permission request popup
        switch( requestCode )
        {
            case PERMISSION_CODE: {
                if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    // permission from popup was granted
                    openCamera();
                } else {
                    // permission from popup denied
                    DynamicToast.make(TakeProfilePicture.this, getString(R.string.no_camera_permissions), Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /**
     * Called when an image is taken after the "take an image" functionality selected
     * @param requestCode
     * @param resultCode
     * @param data
     */
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