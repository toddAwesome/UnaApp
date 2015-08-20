package com.arshsingh93.unaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectInviteActivity extends AppCompatActivity {

    @Bind(R.id.inviteList) ListView myList;

    ParseObject[] mInvites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TheColorUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_select_invite);

        ButterKnife.bind(this);

        getAllInvites();

    }

    private void getAllInvites() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Invite");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    mInvites = new ParseObject[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        mInvites[i] = list.get(i);
                    }

                    InviteAdapter adapter = new InviteAdapter(SelectInviteActivity.this, mInvites);
                    myList.setAdapter(adapter);
                    myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ParseRelation relation = ParseUser.getCurrentUser().getRelation(TheGroupUtil.MEMBERSHIP);
                            relation.add((ParseObject) mInvites[position].get(TheGroupUtil.GROUP_NAME));
                            ParseUser.getCurrentUser().saveInBackground();

                            ParseObject object = (ParseObject) mInvites[position].get(TheGroupUtil.GROUP_NAME);
                            ParseRelation relation1 = object.getRelation(TheGroupUtil.GROUP_MEMBERS);
                            relation1.add(ParseUser.getCurrentUser());
                            object.saveInBackground();

                            ParseObject thing = mInvites[position];
                            thing.deleteInBackground();

                            Intent intent = new Intent(SelectInviteActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

                }
            }
        });



    }


}
