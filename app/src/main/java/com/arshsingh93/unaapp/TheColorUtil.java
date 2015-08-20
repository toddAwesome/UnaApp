package com.arshsingh93.unaapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Arsh on 8/17/2015.
 */
public class TheColorUtil {
    private static SharedPreferences sharedPreferences;
    private static int sTheme;
    public final static int THEME_GREEN = 0;
    public final static int THEME_BLUE = 1;
    public final static int THEME_RED = 2;
    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        initializePref(activity);
        saveColorTheme();
        activity.finish();
        activity.getApplicationContext();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    private static void initializePref(Activity activity) {
        if (sharedPreferences == null) {
            Log.e("TheUtils class", (sharedPreferences == null) + "");
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            Log.e("TheUtils class again", (sharedPreferences == null) + "");
        }
    }
    /**
     * Saves the color theme that the user clicked on.
     */
    private static void saveColorTheme() {
        sharedPreferences.edit().putInt("Theme", sTheme).commit(); //TODO change to .apply rather than .commit
    }
    /**
     * Updates the app to the color theme that the user prefers (when app is opened).....I think....
     * @param activity the activity that this method is called from.
     */
    public static void loadColorTheme(Activity activity) {
        initializePref(activity);
        if (sharedPreferences.contains("Theme")) {
            sTheme = sharedPreferences.getInt("Theme", 0);
        }
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            case THEME_GREEN:
                activity.setTheme(R.style.GreenTheme);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.BlueTheme);
                break;
            case THEME_RED:
                activity.setTheme(R.style.RedTheme);
                break;
            default:
                activity.setTheme(R.style.GreenTheme);
                break;
        }
    }
    /**
     * Called for updating buttons and such. Currently set to return Una_red.
     *
     * @return the preferred color that buttons and stuff should be.
     */

    public static int getProperColor() {
        switch (sTheme) {
            case THEME_GREEN:
                return 0xff56c367;
            case THEME_BLUE:
                return 0xff00aac3;
            case THEME_RED:
                return 0xffff4444; //just right
            // return 0xffff0000; too red
            // return 0xffc35b4e; too brown
        }
        return 0xFFff4444;
        //Una_Red
    }

    /**
     * Called for updating the color of various things.
     * @return a darker version of the preferred color.
     */
    public static int getDarkProperColor() {

        return 0xFFCC0000;
    }
}
