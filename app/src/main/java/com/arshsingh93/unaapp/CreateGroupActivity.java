package com.arshsingh93.unaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateGroupActivity extends AppCompatActivity {

    @Bind(R.id.createGroupNameText) TextView myName;
    @Bind(R.id.createGroupOneText) TextView myOneWord;
    @Bind(R.id.createGroupPrivateButton) RadioButton myPrivateButton;
    @Bind(R.id.createGroupLengthyText) TextView myDescription;
    @Bind(R.id.createGroupBlogCheck) CheckBox myBlogCheck;
    @Bind(R.id.createGroupCalendarCheck) CheckBox myCalendarCheck;
    @Bind(R.id.createGroupButton) Button myCreateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);

        myCreateButton.setBackgroundColor(TheColorUtil.getDarkProperColor());
    }


    @OnClick(R.id.createGroupButton)
    public void createGroup() {
        final ParseObject groupObject = new ParseObject("Group");

        if (createValid()) {
            groupObject.put(TheGroupUtil.GROUP_NAME, myName.getText().toString());
            //groupObject.put(TheGroupUtil.GROUP_TYPE, getGroupType());
            groupObject.put(TheGroupUtil.GROUP_FOUNDER, ParseUser.getCurrentUser());
            groupObject.put(TheGroupUtil.GROUP_ONE_WORD, myOneWord.getText().toString());
            groupObject.put(TheGroupUtil.GROUP_LENGTHY_DESCRIPTION, myDescription.getText().toString());
            //groupObject.put(TheGroupUtil.GROUP_BLOG_EXIST, getBlogCheck());
            //groupObject.put(TheGroupUtil.GROUP_CALENDAR_EXIST, getCalendarCheck());
            //groupObject.put(TheGroupUtil.GROUP_SIZE, getGroupSize());

            if (TheNetUtil.isNetworkAvailable(this)) {
                groupObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        //send user to the group looker page.
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Can not create group").setMessage("Please make sure all of the information is filled out");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
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



}
