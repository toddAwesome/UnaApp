package com.arshsingh93.unaapp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SavedBlogsActivity extends ListActivity {

    private List<ParseObject> list;

    BlogHelper myBlogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_saved_blogs);

        myBlogHelper = new BlogHelper(this);


        getAllBlogs();

    }

    /**
     * Gets the blogs that are saved to the device.
     */
    private void getAllBlogs() {

        String[] columns = {
                BlogHelper.COLUMN_BLOG_TITLE, BlogHelper.COLUMN_BLOG_BODY
        };


        SQLiteDatabase database = myBlogHelper.getWritableDatabase();
        database.beginTransaction();


        Cursor c = database.query(
                BlogHelper.BLOGS_TABLE,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        c.moveToFirst();
        list = new ArrayList<ParseObject>();
        for (int i = 0; i <c.getCount(); i++) {
            String title = c.getString(0);
            String body = c.getString(1);

            ParseObject object = new ParseObject("Blog");
            object.put(TheBlogUtil.BLOG_TITLE, title);
            object.put(TheBlogUtil.BLOG_BODY, body);

            list.add(object);
            c.moveToFirst();
        }

        setListAdapter(new ArrayAdapter<ParseObject>(this, android.R.layout.simple_expandable_list_item_1,
                android.R.id.text1, list));

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TheBlogUtil.setCurrentBlog(list.get(position));
                Intent intent = new Intent(SavedBlogsActivity.this, WriteBlogActivity.class);
                startActivity(intent);
            }
        });




    }













    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_blogs, menu);
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
