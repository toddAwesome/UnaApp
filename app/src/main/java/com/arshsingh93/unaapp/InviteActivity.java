package com.arshsingh93.unaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteActivity extends AppCompatActivity {

    @Bind(R.id.inviteButton)
    Button inviteButton;

    @Bind(R.id.inviteNameText)
    TextView nameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_invite);

        ButterKnife.bind(this);


    }


    @OnClick (R.id.inviteButton)
    public void invite() {
        String name = nameText.getText().toString();

        ParseQuery query = ParseUser.getQuery();

        query.whereEqualTo("username", name);
        query.getFirstInBackground(new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseUser user = (ParseUser) parseObject;
                    ParseObject invite = new ParseObject("Invite");
                    invite.put("user", user);
                    invite.put(TheGroupUtil.GROUP_NAME, TheGroupUtil.getCurrentGroup());
                    invite.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(InviteActivity.this, "Invite sent", Toast.LENGTH_SHORT);
                            Intent intent = new Intent(InviteActivity.this, ViewGroupActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (throwable == null) {
                    ParseUser user = (ParseUser) o;
                    ParseObject invite = new ParseObject("Invite");
                    invite.put("user", user);
                    invite.put(TheGroupUtil.GROUP_NAME, TheGroupUtil.getCurrentGroup());
                    invite.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(InviteActivity.this, "Invite sent", Toast.LENGTH_SHORT);
                            Intent intent = new Intent(InviteActivity.this, ViewGroupActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }

}
