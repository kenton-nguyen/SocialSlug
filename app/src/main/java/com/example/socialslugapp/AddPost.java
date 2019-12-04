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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.CaseMap;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashMap;

public class AddPost extends AppCompatActivity {
    ActionBar actionbar;
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
    String name, email, dp;

    // info of post to edit
    String editTitle, editDescription, editImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        actionbar = getSupportActionBar();
        actionbar.setTitle("ADD NEW POST");

        // this is the back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // view init
        title = findViewById(R.id.add_title);
        description = findViewById(R.id.add_description);
        imageView = findViewById(R.id.add_image);
        postButton = findViewById(R.id.add_post);

        // retrieve data thru intent from prev activity's adapter
        Intent intent = getIntent();
        final String isUpdateKey = ""+intent.getStringExtra("key");
        final String editPostId = ""+intent.getStringExtra("editPostId");

        // validation
        if (isUpdateKey.equals("editPost")){
            // update
            actionbar.setTitle("Update Post");
            postButton.setText("Update");
            loadPostData(editPostId);
        }
        else{
            actionbar.setTitle("Add New Post");
            postButton.setText("Upload");
        }


        actionbar.setSubtitle(email);

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

                if (isUpdateKey.equals("editPost")){
                    beginUpdate(title1, description1, editPostId);
                }
                else{
                    uploadData(title1, description1);
                }
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

    }

    private void beginUpdate(String title1, String description1, String editPostId) {
        if (!editImage.equals("noImage")){
            // with image
            updateWasWithImage(title1, description1, editPostId);
        }
        else if(imageView.getDrawable() != null){
            //with image
            updateWithNowImage(title1, description1, editPostId);
        }else{
            // without image
            updateWithoutImage(title1, description1, editPostId);
        }
    }

    private void updateWithoutImage(String title1, String description1, String editPostId) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(AddPost.this);

        // url is received, upload to firebase DB
        HashMap<String, Object> hashMap = new HashMap<>();

        // put post info
        hashMap.put("uid", acct.getId());
        hashMap.put("uname", acct.getDisplayName());
        hashMap.put("uEmail",acct.getEmail());
        hashMap.put("pLikes", "0");
        hashMap.put("pComments", "0");
        hashMap.put("uDp", acct.getPhotoUrl().toString());
        //hashMap.put("pId", timestamp);
        hashMap.put("pTitle", title1);
        hashMap.put("pDescr", description1);
        hashMap.put("pImage", "noImage");
        // hashMap.put("pTime", timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.child(editPostId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddPost.this, "updated", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateWithNowImage(final String title1, final String description1, final String editPostId) {
        // image deleted, upload new image
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "posts_"+timeStamp;

        // get image from imageview
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // image compress
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // image uploaded, get its url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString();
                        if(uriTask.isSuccessful()){
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(AddPost.this);

                            // url is received, upload to firebase DB
                            HashMap<String, Object> hashMap = new HashMap<>();

                            // put post info
                            hashMap.put("uid", acct.getId());
                            hashMap.put("uname", acct.getDisplayName());
                            hashMap.put("uEmail",acct.getEmail());
                            hashMap.put("pLikes", "0");
                            hashMap.put("pComments", "0");
                            hashMap.put("uDp", acct.getPhotoUrl().toString());
                            //hashMap.put("pId", timestamp);
                            hashMap.put("pTitle", title1);
                            hashMap.put("pDescr", description1);
                            hashMap.put("pImage", downloadUri);
                            // hashMap.put("pTime", timestamp);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            ref.child(editPostId)
                                    .updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AddPost.this, "updated", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddPost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void updateWasWithImage(final String title1, final String description1, final String editPostId) {
        // post is with image, so delete prev image first
        StorageReference mPictureRef = FirebaseStorage.getInstance().getReferenceFromUrl(editImage);
        mPictureRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // image deleted, upload new image
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "Posts/" + "posts_"+timeStamp;

                        // get image from imageview
                        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        // image compress
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();

                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // image uploaded, get its url
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while(!uriTask.isSuccessful());

                                        String downloadUri = uriTask.getResult().toString();
                                        if(uriTask.isSuccessful()){
                                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(AddPost.this);

                                            // url is received, upload to firebase DB
                                            HashMap<String, Object> hashMap = new HashMap<>();

                                            // put post info
                                            hashMap.put("uid", acct.getId());
                                            hashMap.put("uname", acct.getDisplayName());
                                            hashMap.put("uEmail",acct.getEmail());
                                            hashMap.put("pLikes", "0");
                                            hashMap.put("pComments", "0");
                                            hashMap.put("uDp", acct.getPhotoUrl().toString());
                                            //hashMap.put("pId", timestamp);
                                            hashMap.put("pTitle", title1);
                                            hashMap.put("pDescr", description1);
                                            hashMap.put("pImage", downloadUri);
                                           // hashMap.put("pTime", timestamp);

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                            ref.child(editPostId)
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(AddPost.this, "updated", Toast.LENGTH_SHORT).show();

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(AddPost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });


                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddPost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void loadPostData(final String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        // get detail of post using post id
        Query fquery = reference.orderByChild("pId").equalTo(editPostId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    // get data
                    editTitle = ""+ds.child("pTitle").getValue();
                    editDescription = ""+ds.child("pDescr").getValue();
                    editImage = ""+ds.child("pImage").getValue();

                    // set data to views
                    title.setText(editTitle);
                    description.setText(editDescription);

                    // set image
                    if (!editImage.equals("noImage")){
                        try {
                            Picasso.get().load(editImage).into(imageView);
                        }
                        catch (Exception e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // issue when pressing like, the feed jumps back to the top of the feed
    private void uploadData (final String s_title, final String s_description){
        final String timestamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "posts_" + timestamp;


        if (imageView.getDrawable() != null){
            // get image from imageview
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // image compress
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();


            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);

            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()){ // uri is received upload post to firebase database
                                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(AddPost.this);
                                //put post info
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid", acct.getId());
                                hashMap.put("uname", acct.getDisplayName());
                                hashMap.put("uEmail",acct.getEmail());
                                hashMap.put("pLikes", "0");
                                hashMap.put("pComments", "0");
                                hashMap.put("uDp", acct.getPhotoUrl().toString());
                                hashMap.put("pId", timestamp);
                                hashMap.put("pTitle", s_title);
                                hashMap.put("pDescr", s_description);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pTime", timestamp);

                                //path to store post data
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                                //put data in this ref
                                ref.child(timestamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //added in the data base
                                            Toast.makeText(AddPost.this, "Post Published", Toast.LENGTH_SHORT).show();
                                            //reset views
                                            title.setText("");
                                            description.setText("");
                                            imageView.setImageURI(null);
                                            image_rui = null;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //failed to added in the database
                                            Toast.makeText(AddPost.this, "Failed adding into the database", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            Toast.makeText(AddPost.this, "Failed uploading image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(AddPost.this);
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", acct.getId());
            hashMap.put("uname", acct.getDisplayName());
            hashMap.put("uEmail",acct.getEmail());
            hashMap.put("pLikes", "0");
            hashMap.put("pComments", "0");
            hashMap.put("pId", timestamp);
            hashMap.put("pTitle", s_title);
            hashMap.put("pDescr", s_description);
            hashMap.put("pImage", "no Image");
            hashMap.put("pTime", timestamp);

            //path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

            //put data in this ref
            ref.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in the data base
                            Toast.makeText(AddPost.this, "Post Published", Toast.LENGTH_SHORT).show();
                            title.setText("");
                            description.setText("");
                            imageView.setImageURI(null);
                            image_rui = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed to added in the database
                            Toast.makeText(AddPost.this, "Failed adding to data base", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

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

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        //this method will be called after picking image from camera / gallery
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //image is picked from gallery
                image_rui = data.getData();
                //set to imageView
                imageView.setImageURI(image_rui);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera
                imageView.setImageURI(image_rui);

            }
        }
    }

}
