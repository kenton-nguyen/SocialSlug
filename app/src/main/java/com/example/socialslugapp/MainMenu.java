package com.example.socialslugapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// this activity will display the options of either adding a post or viewing the post feed (to be implemented)
// other options may be added to this menu

public class MainMenu extends AppCompatActivity {

    Button addPost;
    Button viewPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        addPost = (Button)findViewById(R.id.addPost);
        viewPost = (Button)findViewById(R.id.viewPost);

    }

    public void viewAddPost (View view){
        Intent intent = new Intent(this, AddPost.class);
        startActivity(intent);

    }

    public void viewViewPost(View view){
        Intent intent = new Intent(this, viewPosts.class);
        startActivity(intent);
    }
}
