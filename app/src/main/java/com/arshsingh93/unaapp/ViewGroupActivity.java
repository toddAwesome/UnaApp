package com.arshsingh93.unaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewGroupActivity extends AppCompatActivity {

    @Bind(R.id.viewGroupBlogButton) ImageView myBlogButton;
    @Bind(R.id.viewGroupMemberButton) ImageView myMemberButton;
    @Bind(R.id.viewGroupEventButton) ImageView myEventButton;
    @Bind(R.id.viewGroupLengthyText) TextView myDescription;
    @Bind(R.id.viewGroupTypeImage) ImageView myTypeImage;
    @Bind(R.id.viewGroupPhoto) ImageView myPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_view_group);
        ButterKnife.bind(this);

        /*
        myBlogButton.setBackgroundColor(TheColorUtil.getProperColor());
        myMemberButton.setBackgroundColor(TheColorUtil.getProperColor());
        myEventButton.setBackgroundColor(TheColorUtil.getProperColor());
         */

        myDescription.setText(TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_NAME));

        if (TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_TYPE).equals(TheGroupUtil.GROUP_PUBLIC)) {
            //set public
            myTypeImage.setImageResource(R.drawable.ic_lock_open_white_48dp);
        } else {
            //set private
            myTypeImage.setImageResource(R.drawable.ic_lock_outline_white_48dp);
        }


        if (TheGroupUtil.getCurrentGroup().get(TheGroupUtil.GROUP_PHOTO) != null) {
            ParseFile picFile = (ParseFile) TheGroupUtil.getCurrentGroup().get(TheGroupUtil.GROUP_PHOTO);
            picFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        myPhoto.setImageBitmap(bitmap);
                    } else {
                        //unable to load image. //TODO
                    }
                }

            });
        }

    }


    @OnClick (R.id.viewGroupTypeImage)
    public void seeType() {
        if (TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_TYPE).equals(TheGroupUtil.GROUP_PUBLIC)) {
            Toast.makeText(this, "This is a public group. \nIt can be seen by anyone.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "This is a private group. \nIt can only be seen by it's members.", Toast.LENGTH_SHORT).show();
        }
    }


    //TODO onClick for the Blog button should open up a screen which has a list of blogs, buttons to create/load blogs. Locked blogs at top.


    //TODO onClick for the events and calendar which opens up a screen with a calendar, option to add event.


    //TODO onClick for members button which displays a list of members and also says if those members are founder/moderator



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_edit_group) {
            //Intent intent = new Intent(this, EditGroupActivity.class); //TODO uncomment after adding EditActivity.
            //startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
