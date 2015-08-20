package com.arshsingh93.unaapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WriteBlogActivity extends AppCompatActivity {

    @Bind(R.id.blogWriterTitle) EditText myTitle;
    @Bind(R.id.blogWriterBody) EditText myBody;

    private BlogHelper myBlogHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_write_blog);
        ButterKnife.bind(this);

        myBlogHelper = new BlogHelper(this);

        if (TheBlogUtil.getCurrentBlog() != null) {
            myTitle.setText(TheBlogUtil.getCurrentBlog().getString(TheBlogUtil.BLOG_TITLE).toString());
            myBody.setText(TheBlogUtil.getCurrentBlog().getString(TheBlogUtil.BLOG_BODY).toString());
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_blog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_publish) {
            final String titleText = myTitle.getText().toString();
            final String bodyText = myBody.getText().toString();

            if (titleText.isEmpty() || bodyText.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please enter something for both the title and the body of this blog")
                        .setTitle("Blog not published")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to publish this blog? Everyone in this group will be able to see it if you do.");
                builder.setPositiveButton("Yes, publish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (TheNetUtil.isNetworkAvailable(WriteBlogActivity.this)) {
                            final ParseRelation blogRelation = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_BLOG);

                            final ParseObject aBlog = new ParseObject("Blog");
                            aBlog.put(TheBlogUtil.BLOG_TITLE, titleText);
                            aBlog.put(TheBlogUtil.BLOG_BODY, bodyText);
                            aBlog.put(TheBlogUtil.BLOG_AUTHOR, ParseUser.getCurrentUser());
                            aBlog.saveInBackground(new SaveCallback() { //TODO save API call  here necessary?
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        blogRelation.add(aBlog);
                                        TheGroupUtil.getCurrentGroup().saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Intent intent = new Intent(WriteBlogActivity.this, ViewGroupActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    } else {
                                        //do something
                                    }
                                }
                            });


                        } else {
                            //TODO alert user about failed network and ask if this blog should automatically be uploaded upon connection.
                        }
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        }

        if (id == R.id.action_save) {
            String titleText = myTitle.getText().toString();
            String bodyText = myBody.getText().toString();
            Log.d("BlogWriterFragment", titleText);

            if (titleText.isEmpty() || bodyText.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please enter something for both the title and the body of this blog")
                        .setTitle("Blog not saved")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                SQLiteDatabase database = myBlogHelper.getWritableDatabase();
                database.beginTransaction();

                ContentValues contentValues = new ContentValues();
                contentValues.put(BlogHelper.COLUMN_BLOG_TITLE, titleText);
                contentValues.put(BlogHelper.COLUMN_BLOG_BODY, bodyText);
                database.insert(BlogHelper.BLOGS_TABLE, null, contentValues);

                database.setTransactionSuccessful();
                database.endTransaction();
                database.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your blog was saved onto your device")
                        .setTitle("Saved")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(WriteBlogActivity.this, SelectBlogActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        }


        return super.onOptionsItemSelected(item);
    }
}
