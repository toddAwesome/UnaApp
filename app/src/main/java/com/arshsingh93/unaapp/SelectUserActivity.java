package com.arshsingh93.unaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectUserActivity extends AppCompatActivity {

    @Bind(R.id.userList) ListView myList;
    @Bind(R.id.userListProgressBar) ProgressBar myBar;

    protected ParseUser[] mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_select_user);

        ButterKnife.bind(this);


        getSupportActionBar().setHomeButtonEnabled(true);


        myBar.setVisibility(View.INVISIBLE);

        getAllUsers();
    }


    private void getAllUsers() {
        myBar.setVisibility(View.VISIBLE);

        ParseRelation userRelation = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_MEMBERS);
        userRelation.getQuery().findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                myBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    mUsers = new ParseUser[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        mUsers[i] = (ParseUser) list.get(i);
                    }
                    UserAdapter adapter = new UserAdapter(SelectUserActivity.this, mUsers);
                    myList.setAdapter(adapter);
                    myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TheGroupUtil.setAUser(mUsers[position]);

                            if (isMod(ParseUser.getCurrentUser())) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SelectUserActivity.this);
                                builder.setItems(R.array.user_choices, mDialogInterface);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                } else {
                    /// Something went wrong.
                    Toast.makeText(SelectUserActivity.this, "Sorry, there was an error getting users!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                myBar.setVisibility(View.INVISIBLE);
                if (throwable == null) {
                    ArrayList<ParseUser> arrayList = (ArrayList) o;
                    mUsers = new ParseUser[arrayList.size()];
                    for (int i = 0; i < arrayList.size(); i++) {
                        mUsers[i] = (ParseUser) arrayList.get(i);
                    }
                    UserAdapter adapter = new UserAdapter(SelectUserActivity.this, mUsers);
                    myList.setAdapter(adapter);
                    myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TheGroupUtil.setAUser(mUsers[position]);

                            if (isMod(ParseUser.getCurrentUser())) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SelectUserActivity.this);
                                builder.setItems(R.array.user_choices, mDialogInterface);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * This method checks to see if the current user is a moderator.
     * @param theUser the current user
     * @return true if he is a moderator or founder, false otherwise.
     */
    private boolean isMod(final ParseUser theUser) {
        ParseRelation moderators = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_MODERATORS);
        int i = -1;
        try {
            i = moderators.getQuery().whereEqualTo("objectId", theUser.getObjectId()).count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return i == 1;
    }


    protected DialogInterface.OnClickListener mDialogInterface = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: //make user into a moderator
                    ParseRelation moderators = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_MODERATORS);
                    if (!isMod(TheGroupUtil.getAUser())) {
                        moderators.add(TheGroupUtil.getAUser());
                    }
                    TheGroupUtil.getCurrentGroup().saveInBackground();

                    break;
                case 1: //remove user
                    ParseRelation members = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_MEMBERS);
                    members.remove(TheGroupUtil.getAUser());
                    TheGroupUtil.getCurrentGroup().saveInBackground();

                    //check this, it will throw an error b/c user is not authenticated.
                    /*ParseRelation relation = TheGroupUtil.getAUser().getRelation(TheGroupUtil.MEMBERSHIP);
                    relation.remove(TheGroupUtil.getCurrentGroup());
                    TheGroupUtil.getAUser().saveInBackground();
                    */

                    Intent intent = new Intent(SelectUserActivity.this, ViewBlogActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    break;
            }
        }
    };







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_user, menu);
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
}
