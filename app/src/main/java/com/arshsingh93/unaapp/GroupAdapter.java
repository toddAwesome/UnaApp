package com.arshsingh93.unaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

/**
 * Created by Student on 8/19/2015.
 */
public class GroupAdapter extends ArrayAdapter<ParseObject> {
    public static final String TAG = GroupAdapter.class.getSimpleName();

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ParseObject[] mGroups;


    public GroupAdapter(Context theContext, ParseObject[] theGroups) {
        super(theContext, R.layout.group_list_item, theGroups);
        mContext = theContext;
        mGroups = theGroups;
        mInflater = LayoutInflater.from(mContext);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_list_item, null);

            holder = new ViewHolder();
            holder.GroupName = (TextView) convertView.findViewById(R.id.groupListNameText);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject group = mGroups[position];

        holder.GroupName.setText(group.getString(TheGroupUtil.GROUP_NAME));


        return convertView;
    }



    private static class ViewHolder {
        TextView GroupName;
        //TODO add group image
    }


}
