package com.arshsingh93.unaapp;

/**
 * Created by Student on 8/17/2015.
 */
public class TheColorUtil {

    /**
     * Called for updating buttons and such. Currently set to return Una_red.
     *
     * @return the preferred color that buttons and stuff should be.
     */
    public static int getProperColor() {
        return 0xFFff4444; //Una_Red
    }

    /**
     * Called for updating the color of various things.
     * @return a darker version of the preferred color.
     */
    public static int getDarkProperColor() {
        return 0xFFCC0000;
    }
}
