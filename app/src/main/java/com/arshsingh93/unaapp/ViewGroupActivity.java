package com.arshsingh93.unaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewGroupActivity extends AppCompatActivity {

    @Bind(R.id.viewGroupBlogImage) ImageView myBlogButton;
    @Bind(R.id.viewGroupMemberImage) ImageView myMemberButton;
    @Bind(R.id.viewGroupEventImage) ImageView myEventButton;
    @Bind(R.id.viewGroupLengthyText) TextView myDescription;
    @Bind(R.id.viewGroupTypeImage) ImageView myTypeImage;
    @Bind(R.id.viewGroupPhoto) ImageView myPhoto;
    @Bind(R.id.viewAnnoucementImage) ImageView myPostAnnouncment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TheColorUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_view_group);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_NAME));

        setScreenColor();

        myDescription.setText(TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_LENGTHY_DESCRIPTION));
        setType();
        setPhoto();
        setButtonVisibility();



    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            finish();
            Toast.makeText(this, "You pressed the home button!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ViewGroupActivity.this, MainActivity.class);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Sets the colors of this screen to the color that the user prefers.
     */
    private void setScreenColor() {
        myBlogButton.setColorFilter(TheColorUtil.getProperColor());
        myEventButton.setColorFilter(TheColorUtil.getProperColor());
        myTypeImage.setColorFilter(TheColorUtil.getProperColor());
        myMemberButton.setColorFilter(TheColorUtil.getProperColor());
        myPostAnnouncment.setColorFilter(TheColorUtil.getProperColor());
    }

    /**
     * Sets the visibility of buttons in the group depending on what the founder/moderators want.
     */
    private void setButtonVisibility() {
        if (TheGroupUtil.getCurrentGroup().getBoolean(TheGroupUtil.GROUP_BLOG_EXIST)) {
            myBlogButton.setVisibility(View.VISIBLE);
        } else {
            myBlogButton.setVisibility(View.INVISIBLE);
        }
        if (TheGroupUtil.getCurrentGroup().getBoolean(TheGroupUtil.GROUP_CALENDAR_EXIST)) {
            myEventButton.setVisibility(View.VISIBLE);
        } else {
            myEventButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Sets the group photo.
     */
    private void setPhoto() {
        if (TheGroupUtil.getCurrentGroup().get(TheGroupUtil.GROUP_PHOTO) != null) {
            ParseFile picFile = (ParseFile) TheGroupUtil.getCurrentGroup().get(TheGroupUtil.GROUP_PHOTO);
            picFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        myPhoto.setImageBitmap(bitmap);
                        myPhoto.setBackgroundColor(0xFFffffff);
                    } else {
                        //unable to load image.
                    }
                }

            });
        }
    }

    /**
     * Sets the button that lets the user know whether or not this is a private/public group.
     */
    private void setType() {
        if (TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_TYPE).equals(TheGroupUtil.GROUP_PUBLIC)) {
            //set public
            myTypeImage.setImageResource(R.drawable.ic_lock_open_white_48dp);
        } else {
            //set private
            myTypeImage.setImageResource(R.drawable.ic_lock_outline_white_48dp);
        }
    }


    /**
     * Shows the user whether or not this is a public/private group.
     */
    @OnClick (R.id.viewGroupTypeImage)
    public void seeType() {
        if (TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_TYPE).equals(TheGroupUtil.GROUP_PUBLIC)) {
            Toast.makeText(this, "This is a public group. \nIt can be seen by anyone.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "This is a private group. \nIt can only be seen by it's members.", Toast.LENGTH_SHORT).show();
        }
    }


     @OnClick (R.id.viewGroupBlogImage)
    public void seeBlogs() {
        Intent intent = new Intent(this, SelectBlogActivity.class);
        startActivity(intent);
    }



    @OnClick (R.id.viewGroupMemberImage)
    public void seeUsers() {
         Intent intent = new Intent(this, SelectUserActivity.class);
         startActivity(intent);
    }

    @OnClick (R.id.viewGroupEventImage)
    public void Event(){
        Intent intent = new Intent(this, CalenderActivity.class);
        startActivity(intent);
    }
    @OnClick (R.id.viewAnnoucementImage)
    public void post() {
        Intent intent = new Intent(this, announcementActivity.class);
        startActivity(intent);
    }
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

        if (id == R.id.action_invite) {
            ParseRelation relation = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_MEMBERS);
            int i = -1;
            try {
                i = relation.getQuery().whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId()).count();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (i == 1) {
                Intent intent = new Intent(ViewGroupActivity.this, InviteActivity.class);
                startActivity(intent);

            }
        }

        if (id == R.id.action_edit_group) {
            ParseRelation relation = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_MEMBERS);
            int i = -1;
            try {
                i = relation.getQuery().whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId()).count();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (i == 1) {
                Intent intent = new Intent(this, EditGroupActivity.class);
                startActivity(intent);
            }
        }

        if (id == R.id.action_leave_group) {
            ParseRelation relation = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_MEMBERS);
            int i = -1;
            try {
                i = relation.getQuery().whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId()).count();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (i == 1) {
                ParseRelation members = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_MEMBERS);
                members.remove(ParseUser.getCurrentUser());
                //TheGroupUtil.getCurrentGroup().get(TheGroupUtil.GROUP_SIZE).decrementSize()?
                TheGroupUtil.getCurrentGroup().saveInBackground();

                ParseRelation relation2 = ParseUser.getCurrentUser().getRelation(TheGroupUtil.MEMBERSHIP);
                relation2.remove(TheGroupUtil.getCurrentGroup());
                ParseUser.getCurrentUser().saveInBackground();

                Intent intent = new Intent(ViewGroupActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
