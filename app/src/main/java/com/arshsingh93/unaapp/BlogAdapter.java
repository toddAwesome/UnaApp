package com.arshsingh93.unaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by Arsh on 8/19/2015.
 */
public class BlogAdapter extends ArrayAdapter<ParseObject> {
    public static final String TAG = BlogAdapter.class.getSimpleName();

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ParseObject[] mBlogs;


   public BlogAdapter(Context theContext, ParseObject[] theBlogs) {
       super(theContext, R.layout.blog_list_item, theBlogs);
       mContext = theContext;
       mBlogs = theBlogs;
       mInflater = LayoutInflater.from(mContext);
   }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.blog_list_item, null);

            holder = new ViewHolder();
            holder.TitleLabel = (TextView) convertView.findViewById(R.id.blogListTitle);
            holder.AuthorLabel = (TextView) convertView.findViewById(R.id.blogListAuthor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject blog = mBlogs[position];

        holder.TitleLabel.setText(blog.getString(TheBlogUtil.BLOG_TITLE));
        holder.AuthorLabel.setText(blog.getString(TheBlogUtil.BLOG_AUTHOR));

        //TODO holder.setOnClickListener that opens up a blog.




        return convertView;
    }








    private static class ViewHolder {
        TextView TitleLabel;
        TextView AuthorLabel;
    }
}
