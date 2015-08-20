package com.arshsingh93.unaapp;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Student on 8/17/2015.
 */
public class TheGroupUtil {


    public static String GROUP_NAME = "GroupName";
    public static String GROUP_TYPE = "GroupType"; //whether a group is public or private
    public static String GROUP_FOUNDER = "GroupFounder";
    public static String GROUP_ONE_WORD = "OneWordDescription";
    public static String GROUP_LENGTHY_DESCRIPTION = "LongDescription";
    public static String GROUP_BLOG_EXIST = "BlogsAvailable";
    public static String GROUP_CALENDAR_EXIST = "CalendarAvailable";
    public static String GROUP_MEMBERS = "GroupMembers";
    public static String GROUP_MODERATORS = "GroupModerators";
    public static String GROUP_SIZE = "GroupSize";
    public static String GROUP_PHOTO = "GroupPhoto";

    public static String GROUP_ID = "objectId";
    public static String GROUP_BLOG = "GroupBlogs";

    public static String GROUP_PUBLIC = "public";
    public static String GROUP_PRIVATE = "private";

    /**
     * Used for the user.
     */
    public static String MEMBERSHIP = "GroupMembership";


    /**
     * This is the current group that is being viewed or edited.
     */
    private static ParseObject currentGroup;

    /**
     * The user that the app user is viewing.
     */
    private static ParseUser aUser;

    /**
     * This sets the current group that the user is viewing.
     * @param theGroup a ParseObject that is a Group.
     */
    public static void setCurrentGroup(ParseObject theGroup) {
        currentGroup = theGroup;
    }

    /**
     * This returns the current group that the user is viewing or editing.
     * @return the current group (as a ParseObject).
     */
    public static ParseObject getCurrentGroup() {
        return currentGroup;
    }

    /**
     * Sets the user being viewed.
     * @param theUser a user.
     */
    public static void setAUser(ParseUser theUser) {
        aUser = theUser;
    }

    /**
     * Gets the viewed user.
     * @return a ParseUser.
     */
    public static ParseUser getAUser() {
        return aUser;
    }
}
