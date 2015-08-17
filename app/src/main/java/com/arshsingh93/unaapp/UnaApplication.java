package com.arshsingh93.unaapp;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Student on 8/17/2015.
 */
public class UnaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ZKorAmUCQmL03WosweB6tJNQXSwedHT1tPVbC6BB", "U6H8f6cbLGB2UDjLl9z6RLc3MjWVfpHWbUa6gH3r");
    }

}