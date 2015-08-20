package com.arshsingh93.unaapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * allows the posting of announcements
 * Created by Todd on 8/19/2015.
 */
public class announcementActivity extends ActionBarActivity {
    @Bind(R.id.postAnnouncement) Button post;
    @Bind(R.id.writeAnnouncment) EditText write;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TheColorUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_announcment);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_announcment, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * posts a small text view to the group
     */
    @OnClick (R.id.postAnnouncement)
    public void postAnnouncment() {
        if (TheNetUtil.isNetworkAvailable(this) != true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("it aint working");
            builder.setPositiveButton("OK", null);
            builder.show();
        } else {
            Toast.makeText(this, "post Announcement", Toast.LENGTH_SHORT).show();
            TheGroupUtil.getCurrentGroup().put(TheGroupUtil.GROUP_ANNOUNCEMENT, write.getText().toString());
            try {
                TheGroupUtil.getCurrentGroup().save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, ViewGroupActivity.class);
            startActivity(intent);
        }
    }
}
