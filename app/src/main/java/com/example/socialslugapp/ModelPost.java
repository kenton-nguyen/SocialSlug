package com.example.socialslugapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Display;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class ModelPost{
    String pDescr;
    String pId;
    String pImage;
    String pTime;
    String pTitle;
    String uEmail;
    String uid;
    String uname;
    String uDp;
    String pLikes;
    String pComments;


    public ModelPost(){

    }

    public ModelPost(String pDescr, String pId, String pImage, String pTime, String pTitle, String uEmail, String uid, String uname, String uDp, String pLikes, String pComments) {
        this.pDescr = pDescr;
        this.pId = pId;
        this.pImage = pImage;
        this.pTime = pTime;
        this.pTitle = pTitle;
        this.uEmail = uEmail;
        this.uid = uid;
        this.uname = uname;
        this.uDp = uDp;
        this.pLikes = pLikes;
        this.pComments = pComments;
    }

    public String getpDescr() {
        return pDescr;
    }

    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }
}
