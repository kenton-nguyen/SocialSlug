package com.example.socialslugapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.firebase.ui.auth.data.model.User;

// this activity will display the options of either adding a post or viewing the post feed (to be implemented)
// other options may be added to this menu

public class MainMenu extends AppCompatActivity {

    Button addPost;
    Button viewPost;
    ImageButton viewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        addPost = (Button)findViewById(R.id.addPost);
        viewPost = (Button)findViewById(R.id.viewPost);
        viewProfile = (ImageButton)findViewById(R.id.profile);

        viewPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                view_Posts();
            }
        });

        viewProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewProfile();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainMenu.this, UserProfile.class);
        startActivity(intent);
        finish();
    }

    public void viewAddPost (View view){
        Intent intent = new Intent(this, AddPost.class);
        startActivity(intent);

    }

    public void view_Posts(){
        Intent intent = new Intent(MainMenu.this, DisplayPosts.class);
        startActivity(intent);
    }

    public void viewProfile(){
        Intent intent = new Intent(MainMenu.this, UserProfile.class);
        startActivity(intent);
    }

}
