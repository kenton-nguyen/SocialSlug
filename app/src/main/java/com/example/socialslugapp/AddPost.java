package com.example.socialslugapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

public class AddPost extends AppCompatActivity {
    ActionBar actionbar;
    FirebaseAuth firebaseAuth;
    DatabaseReference userDB;

    EditText title, description;
    ImageView imageView;
    Button postButton;
    Uri image_rui = null;

    //permission for camera
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permission array
    String [] cameraPermissions;
    String [] storagePermissions;

    //user info
    String name, email, uid, dp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        actionbar = getSupportActionBar();
        actionbar.setTitle("Add New Post");

        // this is the back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

//        firebaseAuth = firebaseAuth.getInstance();
//        checkUserStatus();

        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        userDB = FirebaseDatabase.getInstance().getReference("Users");
        Query query =  userDB.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    name = ""+ ds.child("name").getValue();
                    email = ""+ ds.child("email").getValue();
                    dp = ""+ ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        title = findViewById(R.id.add_title);
        description = findViewById(R.id.add_description);
        imageView = findViewById(R.id.add_image);
        postButton = findViewById(R.id.add_post);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve data from editTexts
                String title1 = title.getText().toString().trim();
                String description1 = description.getText().toString().trim();
                if (TextUtils.isEmpty(title1)){
                    Toast.makeText(AddPost.this, "Enter title....", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(description1)){
                    Toast.makeText(AddPost.this, "Enter description....", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (image_rui ==null){
                    // post without image
                    uploadData(title1,description1, "noImage");
                }
                else{
                    // post with image
                    uploadData(title1,description1, String.valueOf(image_rui));
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

    }


    private void uploadData (String title, String description, String uri){
        String timestamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Post/" + "post_" + timestamp;

//        if (!uri.equals("noImage")){
//            StorageReference ref =
//        }

    }


    private void showImagePickDialog(){
        String [] options = {"Camera", "Gallery"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //camera clicked
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }
                }
                if (which == 1){
                    //gallery clicked
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }





    private void pickFromCamera (){
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);

    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        checkUserStatus();
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        checkUserStatus();
//    }
//
//    private void checkUserStatus () {
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null) { // if they are logged in then ...
//
//        }else {
//            startActivity(new Intent(this, AddPost.class)); // if user is not logged in then direct them to MainMenu for now....
//            finish();
//        }
//    }



    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed(); // return back to the previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this, "Camera and Storage both permissions are necessary", Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }
                }
                else{
                    Toast.makeText(this, "Storage both permissions are necessary", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }



    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        //this method will be called after picking image from camera / gallery
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery
                image_rui = data.getData();


                //set to imageView
                imageView.setImageURI(image_rui);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera
                imageView.setImageURI(image_rui);

            }
        }
    }

}
