package com.arshsingh93.unaapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the activity that shows the list of all blogs associated with the group.
 */
public class SelectBlogActivity extends ListActivity {

    @Bind(R.id.blogListProgressBar) ProgressBar myBar;

    protected ParseObject[] myBlogs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_select_blog);
        ButterKnife.bind(this);

        myBar.setVisibility(View.INVISIBLE);

        //TODO if internet available {
        getAllBlogs();


    }

    /**
     * This method gets all the blogs associated with this group and displays them.
     */
    private void getAllBlogs() {
        myBar.setVisibility(View.VISIBLE);

        ParseRelation blogRelation = TheGroupUtil.getCurrentGroup().getRelation(TheGroupUtil.GROUP_BLOG);
        blogRelation.getQuery().findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                myBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    myBlogs = new ParseObject[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        myBlogs[i] = (ParseObject) list.get(i);
                    }

                    BlogAdapter adapter = new BlogAdapter(SelectBlogActivity.this, myBlogs);
                    setListAdapter(adapter);

                } else {
                    /// Something went wrong.
                    Toast.makeText(SelectBlogActivity.this, "Sorry, there was an error getting users!",
                            Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void done(Object o, Throwable throwable) {

            }

    });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Blog");
        query.whereEqualTo(TheGroupUtil.GROUP_ID, TheGroupUtil.getCurrentGroup().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                myBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    myBlogs = list.toArray(new ParseObject[0]);
                } else {

                }
            }
        });

    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_blog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //create a new blog
        if (id == R.id.action_new_blog) {
            Intent intent = new Intent(this, WriteBlogActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_load_blog) {
            //load a list of blogs.
        }

        //TODO add a button for loading a blog

        //todo add a button for finding a blog (search for a word that is in the blog title).

        return super.onOptionsItemSelected(item);
    }
}
