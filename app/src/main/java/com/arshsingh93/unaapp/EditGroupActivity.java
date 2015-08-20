package com.arshsingh93.unaapp;

import android.app.AlertDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditGroupActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 4;

    @Bind(R.id.edtGroupNameText) TextView myName;
    @Bind(R.id.editGroupOneText) TextView myOneWord;
    @Bind(R.id.editGroupPrivateButton) CheckBox myPrivateCheck;
    @Bind(R.id.editGroupLengthyText) EditText myDescription;
    @Bind(R.id.editGroupBlogCheck) CheckBox myBlogCheck;
    @Bind(R.id.editGroupCalendarCheck) CheckBox myCalendarCheck;
    @Bind(R.id.editGroupPhoto) ImageView myPhoto;

    @Bind(R.id.editGroupProgressBar) ProgressBar myProgressBar;
    @Bind(R.id.editGroupSaveButton) Button mySaveButton;
    @Bind(R.id.editGroupMemberbutton) Button myEditMembersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setTheme(R.style.RedTheme);
        setContentView(R.layout.activity_edit_group);
        ButterKnife.bind(this);

        myProgressBar.setVisibility(View.INVISIBLE);


        mySaveButton.setBackgroundColor(TheColorUtil.getDarkProperColor());
        myEditMembersButton.setBackgroundColor(TheColorUtil.getDarkProperColor());
        myName.setText(TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_NAME));
        myOneWord.setText(TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_ONE_WORD));
        myDescription.setText(TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_LENGTHY_DESCRIPTION));
        setCheckBoxes();

        setPhoto();
    }

    /**
     * Sets the photo of the group.
     */
    private void setPhoto() {
        if (TheGroupUtil.getCurrentGroup().get(TheGroupUtil.GROUP_PHOTO) != null) {
            ParseFile picFile = (ParseFile) TheGroupUtil.getCurrentGroup().get(TheGroupUtil.GROUP_PHOTO);
            picFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        myPhoto.setImageBitmap(bitmap);
                        myPhoto.setBackgroundColor(0xFFffffff);
                    } else {
                        //unable to load image. //TODO
                    }
                }

            });
        }
    }

    /**
     * Sets the check boxes depending on what the founder/moderator specified previously.
     */
    private void setCheckBoxes() {
        if(TheGroupUtil.getCurrentGroup().getString(TheGroupUtil.GROUP_TYPE).equals(TheGroupUtil.GROUP_PRIVATE)) {
            myPrivateCheck.setChecked(true);
        } else {
            myPrivateCheck.setChecked(false);
        }
        if(TheGroupUtil.getCurrentGroup().getBoolean(TheGroupUtil.GROUP_BLOG_EXIST) == true) {
            myBlogCheck.setChecked(true);
        } else {
            myBlogCheck.setChecked(false);
        }
        if(TheGroupUtil.getCurrentGroup().getBoolean(TheGroupUtil.GROUP_CALENDAR_EXIST) == true) {
            myCalendarCheck.setChecked(true);
        } else {
            myCalendarCheck.setChecked(false);
        }
    }


    @OnClick(R.id.editGroupSaveButton)
    public void editGroup() {
        myProgressBar.setVisibility(View.VISIBLE);

        final ParseObject groupObject = TheGroupUtil.getCurrentGroup();

        if (editValid()) {
            groupObject.put(TheGroupUtil.GROUP_NAME, myName.getText().toString());
            groupObject.put(TheGroupUtil.GROUP_TYPE, getGroupType());
            groupObject.put(TheGroupUtil.GROUP_ONE_WORD, myOneWord.getText().toString());
            groupObject.put(TheGroupUtil.GROUP_LENGTHY_DESCRIPTION, myDescription.getText().toString());
            groupObject.put(TheGroupUtil.GROUP_BLOG_EXIST, getBlogCheck());
            groupObject.put(TheGroupUtil.GROUP_CALENDAR_EXIST, getCalendarCheck());

            if (TheNetUtil.isNetworkAvailable(this)) {
                groupObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        myProgressBar.setVisibility(View.INVISIBLE);
                        if (e == null) {
                            //send user to the group looker page.
                            Intent intent = new Intent(EditGroupActivity.this, ViewGroupActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            } else {
                myProgressBar.setVisibility(View.INVISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Network is currently unavailable")
                        .setMessage("This group will be updated and shared with the world automatically once network is connected!");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groupObject.saveEventually();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.show();

            }
        } else {
            myProgressBar.setVisibility(View.INVISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cannot create group").setMessage("Please make sure all of the information is filled out");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }

    }
    /**
     * Checks to see if the group should be allowed to be created.
     * @return true if it should be, false otherwise.
     */

    private boolean editValid() {
        if (myName.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Unable to Create Group").setMessage("Please give this group a name")
                    .setPositiveButton(android.R.string.ok, null).show();
            return false;
        }
        //maybe include more checks here in the future.

        return true; //if no problem encountered.
    }
    private String getGroupType() {
        if (myPrivateCheck.isChecked()) {
            return TheGroupUtil.GROUP_PRIVATE;
        } else if (!myPrivateCheck.isChecked()) {
            return TheGroupUtil.GROUP_PUBLIC;
        }
        //default
        return TheGroupUtil.GROUP_PUBLIC;
    }


    /**
     * Checks to see if the blog button is marked.
     * @return true if it is.
     */
    private boolean getBlogCheck() {
        return myBlogCheck.isChecked();
    }

    @OnClick (R.id.editGroupPhoto)
    public void editPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogInterface);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Checks to see if the calendar button is checked.
     * @return true if it is.
     */
    private boolean getCalendarCheck() {
        return myCalendarCheck.isChecked();
    }
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
            TheGroupUtil.getCurrentGroup().put(TheGroupUtil.GROUP_PHOTO, file);
            TheGroupUtil.getCurrentGroup().saveInBackground();

        } catch (FileNotFoundException e) {
            Log.e("ProfileFragment", "Error: " + e);
        } catch (IOException e) {
            Log.e("ProfileFragment", "Error: " + e);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ProfileFragment", "Here in onActivityResult with requestCode: " + requestCode + " , resultCode: " +
                resultCode + " , data: " + data);
        super.onActivityResult(requestCode, resultCode, data);
        //    ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_OK) {
            if (requestCode == CHOOSE_PHOTO_REQUEST) {
                if (data == null) {
                    Toast.makeText(this, "Sorry, there was an error", Toast.LENGTH_LONG).show();
                } else {
                    mMediaUri = data.getData();
                    Log.d("ProfileFragment", "Here in onActivityResult's if if else.");
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