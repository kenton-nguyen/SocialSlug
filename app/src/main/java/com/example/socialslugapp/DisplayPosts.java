package com.example.socialslugapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayPosts extends AppCompatActivity {
    ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_posts);

        actionbar = getSupportActionBar();
        actionbar.setTitle("EVENT FEED");

        // this is the back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        viewPosts vP = new viewPosts();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, vP).commitNow();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // return back to the previous activity
        return super.onSupportNavigateUp();
    }

}
