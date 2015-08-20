package com.arshsingh93.unaapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

/**
 * Created by Student on 8/20/2015.
 */
public class InviteAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ParseObject[] mInvites;

    /**
     * Constructor.
     * @param theContext the context
     * @param theInvites the array.
     */
    public InviteAdapter(Context theContext, ParseObject[] theInvites) {
        super(theContext, R.layout.invite_list_item, theInvites);
        mContext = theContext;
        mInvites = theInvites;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.invite_list_item, null);

            holder = new ViewHolder();
            holder.GroupName = (TextView) convertView.findViewById(R.id.inviteListName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject invite = mInvites[position];

        holder.GroupName.setText(invite.toString());


        return convertView;
    }


    /**
     * Inner class.
     */
    private static class ViewHolder {
        TextView GroupName;
    }

}
