package com.arshsingh93.unaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateGroupActivity extends AppCompatActivity {

    @Bind(R.id.createGroupNameText) TextView myName;
    @Bind(R.id.createGroupOneText) TextView myOneWord;
    @Bind(R.id.createGroupPrivateButton) CheckBox myPrivateCheck;
    @Bind(R.id.createGroupLengthyText) TextView myDescription;
    @Bind(R.id.createGroupBlogCheck) CheckBox myBlogCheck;
    @Bind(R.id.createGroupCalendarCheck) CheckBox myCalendarCheck;
    @Bind(R.id.createGroupButton) Button myCreateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);

        myCreateButton.setBackgroundColor(TheColorUtil.getDarkProperColor());
    }


    /**
     * Creates the group on parse by taking in information from the various fields on the xml that matches this activity.
     * If the network is not available when user attempts to create a group, then the user will be given the option
     * of having the group get automatically created when network becomes available.
     */
    @OnClick(R.id.createGroupButton)
    public void createGroup(View view) {

        final ParseObject groupObject = new ParseObject("Group");

        if (createValid()) {
            groupObject.put(TheGroupUtil.GROUP_NAME, myName.getText().toString());
            groupObject.put(TheGroupUtil.GROUP_TYPE, getGroupType());
            groupObject.put(TheGroupUtil.GROUP_FOUNDER, ParseUser.getCurrentUser());
            groupObject.put(TheGroupUtil.GROUP_ONE_WORD, myOneWord.getText().toString().toLowerCase());
            groupObject.put(TheGroupUtil.GROUP_LENGTHY_DESCRIPTION, myDescription.getText().toString());
            groupObject.put(TheGroupUtil.GROUP_BLOG_EXIST, getBlogCheck());
            groupObject.put(TheGroupUtil.GROUP_CALENDAR_EXIST, getCalendarCheck());

            ParseRelation members = groupObject.getRelation(TheGroupUtil.GROUP_MEMBERS);
            members.add(ParseUser.getCurrentUser());

            ParseRelation moderators = groupObject.getRelation(TheGroupUtil.GROUP_MODERATORS);
            moderators.add(ParseUser.getCurrentUser());

            groupObject.put(TheGroupUtil.GROUP_SIZE, 1); //the first member is the founder.

            //ParseRelation blogRelation = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_BLOG); //empty at first, no need to do anything with this in this class.


            if (TheNetUtil.isNetworkAvailable(this)) {
                Toast.makeText(this, "network is available", Toast.LENGTH_SHORT).show();
                groupObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(CreateGroupActivity.this, "You group has been created", Toast.LENGTH_LONG).show();
                            //set the value of current group to this group in TheGroupUtil
                            TheGroupUtil.setCurrentGroup(groupObject);
                            //send user to the group looker page.
                            Intent intent = new Intent(CreateGroupActivity.this, ViewGroupActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //this prevents you from getting back to the previous page.
                            startActivity(intent);

                        } else {
                            Toast.makeText(CreateGroupActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Network is currently unavailable")
                        .setMessage("This group will be created and shared with the world automatically once network is connected!");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groupObject.saveEventually();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.show();

            }
        } else {
            /* //NOTE: A dialog pops up due to the createValid() method so the following dialog is unnecessary.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Can not create group").setMessage("Please make sure all of the information is filled out");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
            */
        }
    }

    /**
     * Checks to see if the group should be allowed to be created.
     * @return true if it should be, false otherwise.
     */
    private boolean createValid() {
        if (myName.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Unable to Create Group").setMessage("Please give this group a name")
                    .setPositiveButton(android.R.string.ok, null).show();
            return false;
        }
        //maybe include more checks here in the future.

        return true; //if no problem encountered.
    }

    /**
     * Gets the type of group this is.
     * @return private if private group, public if public group.
     */
    private String getGroupType() {
        if (myPrivateCheck.isChecked()) {
            return TheGroupUtil.GROUP_PRIVATE;
        } else if (!myPrivateCheck.isChecked()) {
            return TheGroupUtil.GROUP_PUBLIC;
        }
        //default
        return TheGroupUtil.GROUP_PUBLIC;
    }

    /**
     * Checks to see if the blog button is marked.
     * @return true if it is.
     */
    private boolean getBlogCheck() {
        return myBlogCheck.isChecked();
    }

    /**
     * Checks to see if the calendar button is checked.
     * @return true if it is.
     */
    private boolean getCalendarCheck() {
        return myCalendarCheck.isChecked();
    }




}
