package com.arshsingh93.unaapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Todd on 8/19/2015.
 */
public class SettingsActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 4;
    private LoginButton faceButton;
    private Dialog progressDialog;
    private CallbackManager callbackManager;

    @Bind(R.id.settingBlueButton) Button myBlueButton;
    @Bind(R.id.settingGreenButton) Button myGreenButton;
    @Bind(R.id.settingRedButton) Button myRedButton;
    @Bind(R.id.settingsPhoto) ImageView myPhoto;
    @Bind(R.id.settingsUsername) TextView myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setTheme(R.style.RedTheme);
        TheColorUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        myUsername.setText(ParseUser.getCurrentUser().getUsername());
        //   myFacebookPhoto.setVisibility(View.INVISIBLE);
        callbackManager = CallbackManager.Factory.create();

        //Content Sharing facebook image
        faceButton = (LoginButton) findViewById(R.id.login_button);
        faceButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();

                String userId = loginResult.getAccessToken().getUserId();
                String profileImgUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";
                Uri uri = Uri.parse(profileImgUrl);
                mMediaUri = uri;
                Glide.with(SettingsActivity.this).load(profileImgUrl).into(myPhoto);

            }

            @Override
            public void onCancel() {
                //they cancelled
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
                //the attempt failed
            }
        });
        if (ParseUser.getCurrentUser().get("profilePic") != null) {
            ParseFile picFile = (ParseFile) ParseUser.getCurrentUser().get("profilePic");
            picFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        myPhoto.setImageBitmap(bitmap);
                    } else {
                    }
                }

            });
        }
    }
    @OnClick (R.id.settingBlueButton)
    public void changeToBlue(View view) {
        Log.e(this.getClass().getSimpleName(), "Clicked blue button");
        TheColorUtil.changeToTheme(this, TheColorUtil.THEME_BLUE);

    }

    @OnClick (R.id.settingGreenButton)
    public void changeToGreen(View view) {
        Log.e(this.getClass().getSimpleName(), "Clicked green button");
        TheColorUtil.changeToTheme(this, TheColorUtil.THEME_GREEN);
    }

    @OnClick (R.id.settingRedButton)
    public void changeToRed(View view) {
        Log.e(this.getClass().getSimpleName(), "Clicked red button");
        TheColorUtil.changeToTheme(this, TheColorUtil.THEME_RED);
    }
    @OnClick (R.id.settingsPhoto)
    public void choosePhoto(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setItems(R.array.camera_choices, mDialogInterface);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Set the profile image of this account
     * @param theUri the uri for the photo
     */
    private void setImage(Uri theUri) {
        Log.d("ProfileFragment", "Here in setImage with uri: " + theUri);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), theUri);
            Log.d("ProfileFragment", "bitmap is: " + bitmap.toString());
            myPhoto.setImageBitmap(bitmap);

            //send to parse
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] array = stream.toByteArray();
            ParseFile file = new ParseFile("profilePic.jpeg", array);
            file.saveInBackground();
            ParseUser.getCurrentUser().put("profilePic", file);
            ParseUser.getCurrentUser().saveInBackground(); //is this necessary?


        } catch (FileNotFoundException e) {
            Log.e("ProfileFragment", "Error: " + e);
        } catch (IOException e) {
            Log.e("ProfileFragment", "Error: " + e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ProfileFragment", "Here in onActivityResult with requestCode: " + requestCode + " , resultCode: " +
                resultCode  + " , data: " + data);
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_OK) {
            if (requestCode == CHOOSE_PHOTO_REQUEST) {
                if (data == null) {
                    Toast.makeText(this, "Sorry, there was an error", Toast.LENGTH_LONG).show();
                } else {
                    mMediaUri = data.getData();
                    Log.d("ProfileFragment", "Here in onActivityResult's if else.");
                    setImage(mMediaUri);
                }
            } else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                this.sendBroadcast(mediaScanIntent);
                setImage(mMediaUri);
                Log.d("ProfileFragment", "Here in onActivityResult's if else.");
            }
        } else if (resultCode != this.RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error", Toast.LENGTH_LONG).show();

        }
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