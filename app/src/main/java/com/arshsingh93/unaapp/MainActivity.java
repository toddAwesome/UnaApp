package com.arshsingh93.unaapp;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * created by Arsh 8/19/2015
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected ParseObject[] myGroups;

    @Bind(R.id.mainProgressBar) ProgressBar myBar;
    @Bind(R.id.mainList) ListView myList;
    @Bind(android.R.id.empty) TextView myEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TheColorUtil.onActivityCreateSetTheme(this);
        TheColorUtil.loadColorTheme(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
           // Log.e(TAG, "Current user is null so move to login screen");
            navigateToEnter();
        } else {
            Toast.makeText(this, "Hello " + currentUser.get("origName"), Toast.LENGTH_LONG).show();
        }

        myBar.setVisibility(View.INVISIBLE);

        getAllGroups();

    }


    /**
     * This gets and displays all groups that the user is a part of.
     */
    private void getAllGroups() {
        myBar.setVisibility(View.VISIBLE);
       // Log.e(TAG, "in getAllGroups()");

        //ParseQuery<ParseObject> query = new ParseQuery<ParseObject>()

        ParseRelation relation = ParseUser.getCurrentUser().getRelation(TheGroupUtil.MEMBERSHIP);
        relation.getQuery().findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                myBar.setVisibility(View.INVISIBLE);
                //Log.e(TAG, "in done()  of getAllGroups");
                if (e == null) {
                   // Log.e(TAG, "in done() of query of getAllGroups. size of list: " + list.size());
                    myGroups = new ParseObject[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        myGroups[i] = (ParseObject) list.get(i);
                    }
                   // Log.e(TAG, "in done() of query of getAllGroups. Value at last position: "
                    //        + myGroups[list.size()].getString(TheGroupUtil.GROUP_NAME));

                    GroupAdapter adapter = new GroupAdapter(MainActivity.this, myGroups);
                    myList.setAdapter(adapter);

                } else {
                    /// Something went wrong.
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                myBar.setVisibility(View.INVISIBLE);
               // Log.e(TAG, "in second done method of query of getAllgroups()");
                //Log.e(TAG, "second done. o is: " + o.getClass());

                ArrayList<ParseObject> arrayList = (ArrayList) o;
               // Log.e(TAG, "in second done. size of o: " + arrayList.size());

                if (throwable == null) {
                    //Log.e(TAG, "in done() of query of getAllGroups. size of list: " + arrayList.size());
                    myGroups = new ParseObject[arrayList.size()];
                    for (int i = 0; i < arrayList.size(); i++) {
                        myGroups[i] = (ParseObject) arrayList.get(i);
                    }
                    //Log.e(TAG, "in done() of query of getAllGroups. Value at last position: "
                    //        + myGroups[arrayList.size() - 1].getString(TheGroupUtil.GROUP_NAME));

                    GroupAdapter adapter = new GroupAdapter(MainActivity.this, myGroups);
                    myList.setAdapter(adapter);
                    myList.setEmptyView(myEmptyText);
                    //setOnclickListener to myList
                    myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TheGroupUtil.setCurrentGroup(myGroups[position]);
                            Intent intent = new Intent(MainActivity.this, ViewGroupActivity.class);
                            startActivity(intent);
                        }
                    });





                } else {
                    /// Something went wrong.
                    Toast.makeText(MainActivity.this, "Sorry, there was an error getting groups!",
                            Toast.LENGTH_LONG).show();
                }





            }
        });


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
            Intent intent = new Intent(MainActivity.this, SelectInviteActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }







}
