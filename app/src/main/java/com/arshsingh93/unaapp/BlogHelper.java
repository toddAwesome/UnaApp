package com.arshsingh93.unaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Student on 8/19/2015.
 */
public class BlogHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "una.db";
    public static final int DB_VERSION = 1;


    public static final String BLOGS_TABLE = "BLOGS";
    public static final String COLUMN_BLOG_TITLE = "TITLE";
    public static final String COLUMN_BLOG_BODY = "BODY";


    private static final String CREATE_BLOGS = "CREATE TABLE " + BLOGS_TABLE + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_BLOG_BODY + " TEXT, " +
            COLUMN_BLOG_TITLE + " TEXT)"; //insert second parenthesis somewhere




    public BlogHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    public BlogHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BLOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

