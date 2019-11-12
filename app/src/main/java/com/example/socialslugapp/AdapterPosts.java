package com.example.socialslugapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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


import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<ModelPost> postList;
    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        //get data
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getUname();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String uDp = postList.get(position).getuDp();
        String pTimeStamp = postList.get(position).getpTime();

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        myHolder.uNameTv.setText(uName);
        myHolder.pTimeTv.setText(pTime);
        myHolder.pTitleTv.setText(pTitle);
        myHolder.pDescriptionTv.setText(pDescription);
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
        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
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
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
        }
    }
}
