package com.arshsingh93.unaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.zip.Inflater;

import butterknife.Bind;

/**
 * Created by Arsh on 8/20/2015.
 */
public class UserAdapter  extends ArrayAdapter<ParseObject> {

    public static final String TAG = UserAdapter.class.getSimpleName();


    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ParseUser[] mUsers;


    public UserAdapter(Context theContext, ParseUser[] theUsers) {
        super(theContext, R.layout.user_list_item, theUsers);
        mContext = theContext;
        mInflater = LayoutInflater.from(theContext);
        mUsers = theUsers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_list_item, null);

            holder = new ViewHolder();
            holder.NameLabel = (TextView) convertView.findViewById(R.id.userListName);
            holder.FounderLabel = (TextView) convertView.findViewById(R.id.userListFounder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseUser user = mUsers[position];
        holder.NameLabel.setText(user.getString("origName"));
        ParseUser founder = (ParseUser) TheGroupUtil.getCurrentGroup().get(TheGroupUtil.GROUP_FOUNDER);
        if (founder.getObjectId() == ParseUser.getCurrentUser().getObjectId()) {
            holder.FounderLabel.setVisibility(View.VISIBLE);
        } else {
            holder.FounderLabel.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }





    private static class ViewHolder {
        TextView NameLabel;
        TextView FounderLabel;
    }
}
