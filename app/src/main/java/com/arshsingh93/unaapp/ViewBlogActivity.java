package com.arshsingh93.unaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Arsh & Todd 8/19/2015
 */
public class ViewBlogActivity extends AppCompatActivity {


    @Bind(R.id.viewBlogTitle) TextView myTitle;
    @Bind(R.id.viewBlogAuthor) TextView myAuthor;
    @Bind(R.id.viewBlogBody) TextView myBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        ButterKnife.bind(this);
        TheColorUtil.onActivityCreateSetTheme(this);
        getSupportActionBar().setHomeButtonEnabled(true);

        myTitle.setText(TheBlogUtil.getCurrentBlog().getString(TheBlogUtil.BLOG_TITLE));
        ParseUser user = (ParseUser) TheBlogUtil.getCurrentBlog().get(TheBlogUtil.BLOG_AUTHOR);
        myAuthor.setText("Author: " + user.getString("origName"));
        myBody.setText(TheBlogUtil.getCurrentBlog().getString(TheBlogUtil.BLOG_BODY));



    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_blog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO allow author to edit this blog.

        return super.onOptionsItemSelected(item);
    }
}
