package com.arshsingh93.unaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 4;

    @Bind(R.id.settingsPhoto) ImageView myPhoto;
    @Bind(R.id.settingsUsername) TextView myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }





    protected Uri mMediaUri;
    protected DialogInterface.OnClickListener mDialogInterface = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: //Take photo
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri == null) {
                        //display an error
                        Toast.makeText(getApplicationContext(), //I am not sure about this getApplicationContext thing.
                                "There was a problem accessing your devices external storage", Toast.LENGTH_LONG).show();
                    }
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    break;
                case 1: //Take Choose Photo
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);
                    break;
            }
        }
        private Uri getOutputMediaFileUri(int theMediaType) {
            if (isExternalStorageAvailable()) {
                //get the Uri
                //1. Get external storage directory
                String appName = getString(R.string.app_name);
                File mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        appName);
                //2. Create our subdirectory
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.e("ProfileFragment", "Failed to get directory");
                    }
                }
                //3. Create a file name
                //4. Create the file
                File mediaFile;
                Date now = new Date();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
                String path = mediaStorageDir.getPath() + File.separator;
                if (theMediaType == MEDIA_TYPE_IMAGE) {
                    mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
                } //else if video type...but i'm not using videos here so this is good enough.
                else {
                    Log.d("ProfileFragment", "file path is null");
                    return null;
                }
                Log.d("ProfileFragment", "File: " + Uri.fromFile(mediaFile));
                //5. Return the files Uri
                return Uri.fromFile(mediaFile);
            } else {
                return null;
            }
        }
        private boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_MOUNTED)) {
                return true;
            } else {
                return false;
            }
        }
    };
}
