package com.arshsingh93.unaapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the activity that shows the list of all blogs associated with the group.
 */
public class SelectBlogActivity extends AppCompatActivity {

    private static String TAG = "SelectBlog";

    @Bind(R.id.blogListProgressBar) ProgressBar myBar;
    @Bind(R.id.bloglist) ListView myList;

    protected ParseObject[] myBlogs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_select_blog);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeButtonEnabled(true);

        myBar.setVisibility(View.INVISIBLE);

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
                    myList.setAdapter(adapter);
                    myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TheBlogUtil.setCurrentBlog(myBlogs[position]);
                            Intent intent = new Intent(SelectBlogActivity.this, ViewBlogActivity.class);
                            startActivity(intent);
                        }
                    });


                } else {
                    /// Something went wrong.
                    Toast.makeText(SelectBlogActivity.this, "Sorry, there was an error getting blogs!",
                            Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                myBar.setVisibility(View.INVISIBLE);

                if (throwable == null) {
                    ArrayList<ParseObject> arrayList = (ArrayList) o;
                    //Log.e(TAG, "in second done. size of o: " + arrayList.size());

                    //Log.e(TAG, "in done() of query of getAllGroups. size of list: " + arrayList.size());

                    myBlogs = new ParseObject[arrayList.size()];
                    for (int i = 0; i < arrayList.size(); i++) {
                        myBlogs[i] = (ParseObject) arrayList.get(i);
                    }
                    //Log.e(TAG, "in done() of query of getAllGroups. Value at last position: "
                    //        + myBlogs[arrayList.size() - 1].getString(TheGroupUtil.GROUP_NAME));

                    BlogAdapter adapter = new BlogAdapter(SelectBlogActivity.this, myBlogs);
                    myList.setAdapter(adapter);
                    //myList.setEmptyView(myEmptyText);
                    //setOnclickListener to myList
                    myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TheBlogUtil.setCurrentBlog(myBlogs[position]);
                            Intent intent = new Intent(SelectBlogActivity.this, ViewBlogActivity.class);
                            startActivity(intent);
                        }
                    });
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
            TheBlogUtil.setCurrentBlog(null);
            Intent intent = new Intent(this, WriteBlogActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_load_blog) {
            //load a list of blogs.
            Intent intent = new Intent(this, SavedBlogsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
