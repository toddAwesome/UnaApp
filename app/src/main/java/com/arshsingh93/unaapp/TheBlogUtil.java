package com.arshsingh93.unaapp;

import com.parse.ParseObject;

/**
 * Created by Student on 8/19/2015.
 */
public class TheBlogUtil {

    public static String BLOG_TITLE = "BlogTitle";
    public static String BLOG_BODY = "BlogBody";
    public static String BLOG_AUTHOR = "BlogAuthor";

    private static ParseObject currentBlog;


    public static void setCurrentBlog(ParseObject theBlog) {
        currentBlog = theBlog;
    }

    public static ParseObject getCurrentBlog() {
        return currentBlog;
    }


}
