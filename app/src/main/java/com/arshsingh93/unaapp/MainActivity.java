package com.arshsingh93.unaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Current user is null so move to login screen");
            navigateToEnter();
        } else {
            Toast.makeText(this, "Hello " + currentUser.get("origName"), Toast.LENGTH_LONG).show();
        }



    }

    /**
     * Send the user to the Enter page.
     * The Enter page is where a person logs in, signs up, or resets their password.
     */
    private void navigateToEnter() {
        Intent intent = new Intent(this, EnterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //this prevents you from getting back to the previous page.
        startActivity(intent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            ParseUser.logOut();
            navigateToEnter();
        } else if (id == R.id.action_new) {
            Toast.makeText(this, "clicked New", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CreateGroupActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_inbox) {
            Toast.makeText(this, "clicked inbox", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_search) {
            Toast.makeText(this, "clicked search", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }







}
