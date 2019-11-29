package com.example.socialslugapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.opencensus.metrics.export.Value;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<ModelPost> postList;
    boolean mProcessLike = false;
    FirebaseAuth mAuth;
    private DatabaseReference likesRef;
    private DatabaseReference postsRef;
    PostDetailActivity testing;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {
        //get data
        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getUname();
        final String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String uDp = postList.get(position).getuDp();
        String pTimeStamp = postList.get(position).getpTime();
        String pLikes = postList.get(position).getpLikes();
        String pComments = postList.get(position).getpComments();

        setLikes(myHolder, pId, uid);
        
        
        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        //set data
        myHolder.uNameTv.setText(uName);
        myHolder.pTimeTv.setText(pTime);
        myHolder.pTitleTv.setText(pTitle);
        myHolder.pDescriptionTv.setText(pDescription);
        myHolder.pLikesTv.setText(pLikes + " Likes");
        myHolder.pCommentsTv.setText(pComments + " Comments");

        //set user dp
        //need to fix user PICTuRE
        try {
            Picasso.get().load(uDp).placeholder(R.mipmap.ic_default_img).into(myHolder.uPictureIv);
        }catch (Exception e){

        }

        //set post image
        //if there is no image then hide ImageView
        if (pImage.equals("noImage")){
            myHolder.pImageIv.setVisibility(View.GONE);
        }else{
            try{
                Picasso.get().load(pImage).into(myHolder.pImageIv);
            }catch (Exception e) {
            }
        }

        //handle button clicks
//        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View v) {
//                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
//            }
//        });

        myHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike = true;
                final String postId = postList.get(position).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postId).hasChild(uid)){
                                postsRef.child(postId).child("pLikes").setValue(""+(pLikes - 1));
                                likesRef.child(postId).child(uid).removeValue();
                                mProcessLike = false;
                            }else{
                                postsRef.child(postId).child("pLikes").setValue(""+(pLikes+1));
                                likesRef.child(postId).child(uid).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        myHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
                // start PostDetailActivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                Intent postId = intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });


    }

    private void setLikes(final MyHolder holder,  final String postKey, final String myUid) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(myUid)){
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up, 0, 0, 0);
                    holder.likeBtn.setText("Liked");

                }
                else{
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_2, 0, 0, 0);
                    holder.likeBtn.setText("Liked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_posts.xml

        ImageView uPictureIv;
        ImageView pImageIv;
        TextView uNameTv;
        TextView pTimeTv;
        TextView pTitleTv;
        TextView pDescriptionTv;
        TextView pLikesTv;
        TextView pCommentsTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn, shareBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            pCommentsTv = itemView.findViewById(R.id.pCommentsTv);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);

        }
    }
}
