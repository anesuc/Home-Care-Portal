package com.anesu.homecareportal.ui.main;

import android.content.Intent;

import com.anesu.homecareportal.HomeActivity;
import com.anesu.homecareportal.TaskActivity;
public class taskItem {
    private int mImageResource;
    private String mText1;
    private String mText2;
    private String mText3;
    private String mId;
    private String locationId;
    private String completed;

    public taskItem(int imageResource, String text1, String text2, String text3,String id, String location) {
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
        mText3 = text3;
        mId = id;
        locationId = location;

    }

    public int getmImageResource() {
        return mImageResource;
    }

    public String getmText1() {
        return mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public String getmText3() {
        return mText3;
    }

    public String getmId() {
        return mId;
    }

    public String getLocation() {
        return locationId;
    }


    public void openHomeScreen() {
        //Intent intent = new Intent(this, TaskActivity.class);
        //startActivity(intent);
    }
}
