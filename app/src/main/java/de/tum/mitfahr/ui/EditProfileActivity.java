package de.tum.mitfahr.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.UpdateUserEvent;
import de.tum.mitfahr.events.UploadAvatarEvent;
import de.tum.mitfahr.networking.models.Department;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.fragments.PasswordPromptDialogFragment;
import de.tum.mitfahr.util.BitmapUtils;
import de.tum.mitfahr.util.RoundTransform;
import de.tum.mitfahr.util.StringHelper;

public class EditProfileActivity extends ActionBarActivity implements PasswordPromptDialogFragment.PasswordPromptDialogListener {

    public static final int PICK_IMAGE_INTENT = 1;

    @InjectView(R.id.edit_profile_first_name)
    EditText firstNameEditText;

    @InjectView(R.id.edit_profile_last_name)
    EditText lastNameEditText;

    @InjectView(R.id.edit_profile_phone_number)
    EditText phoneNumberEditText;

    @InjectView(R.id.edit_profile_car)
    EditText carEditText;

    @InjectView(R.id.edit_profile_image)
    CircularImageView userImageView;

    @InjectView(R.id.departmentSpinner)
    Spinner departmentSpinner;

    private boolean detailsChanged = false;
    private ArrayList<Department> departments = new ArrayList<>();
    private String selectedDepartment;
    AdapterView.OnItemSelectedListener departmentListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedDepartment = departments.get(position).getName();
            if(!selectedDepartment.trim().equalsIgnoreCase(mCurrentUser.getDepartment().trim())) {
                detailsChanged = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            detailsChanged = true;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private boolean imageChanged;
    private String changedImageUri;
    private User mCurrentUser;
    private ProgressDialog mProgressDialog;
    private String tempImgName = "profile_image_temp.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.inject(this);
        mCurrentUser = TUMitfahrApplication.getApplication(this).getProfileService().getUserFromPreferences();
        populateTheFields();
        firstNameEditText.addTextChangedListener(textWatcher);
        lastNameEditText.addTextChangedListener(textWatcher);
        phoneNumberEditText.addTextChangedListener(textWatcher);
        carEditText.addTextChangedListener(textWatcher);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Updating Profile");

        selectedDepartment = mCurrentUser.getDepartment();
        departments = TUMitfahrApplication.getApplication(this).getDepartments();
        if (departments.isEmpty()){
            TUMitfahrApplication.getApplication(this).getDepartmentsFromBackend();
        }
        List<String> department_displayNames = new LinkedList<>();
        for (Department d : departments){
            department_displayNames.add(d.getFriendly_name());
        }

        ArrayAdapter<String> mDepartmentAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_black, department_displayNames);
        departmentSpinner.setAdapter(mDepartmentAdapter);
        for (int i = 0; i < departments.size(); i++){
            if(departments.get(i).getName().equals(selectedDepartment)){
                departmentSpinner.setSelection(i);
            }
        }

        mDepartmentAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        departmentSpinner.setOnItemSelectedListener(departmentListener);
    }

    private void populateTheFields() {
        firstNameEditText.setText(mCurrentUser.getFirstName());
        lastNameEditText.setText(mCurrentUser.getLastName());
        carEditText.setText(mCurrentUser.getCar());
        phoneNumberEditText.setText(mCurrentUser.getPhoneNumber());

        String profileImageUrl = TUMitfahrApplication.getApplication(this).getProfileService().getStoredAvatarURL();
        if( StringHelper.isBlank(profileImageUrl) ) profileImageUrl = TUMitfahrApplication.getApplication(this).getProfileService().getProfileImage();
        Picasso.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_account_dark)
                .error(R.drawable.ic_account_dark)
                .fit().centerCrop()
                .transform(new RoundTransform())
                .into(userImageView);
    }

    @OnClick(R.id.edit_profile_image)
    public void onProfileImageClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_INTENT);
    }

    @OnClick(R.id.edit_profile_button)
    public void onEditProfileButtonClicked() {
        if (imageChanged) {
            if (!StringHelper.isBlank(changedImageUri)) {
                //new UploadImageTask(this).execute(changedImageUri);
                mProgressDialog.show();
                TUMitfahrApplication.getApplication(this).getProfileService().uploadAvatar(changedImageUri);
            }
        }
        if (detailsChanged) {
            mProgressDialog.show();
            if (detailsChanged) {
                mCurrentUser.setFirstName(firstNameEditText.getText().toString());
                mCurrentUser.setLastName(lastNameEditText.getText().toString());
                mCurrentUser.setPhoneNumber(phoneNumberEditText.getText().toString());
                mCurrentUser.setCar(carEditText.getText().toString());
                mCurrentUser.setDepartment(selectedDepartment);
                TUMitfahrApplication.getApplication(this).getProfileService().updateUser(mCurrentUser);
            }
        }
    }

    @Subscribe
    public void onUploadAvatarResult(UploadAvatarEvent result) {
        if(result.getType() == UploadAvatarEvent.Type.UPLOAD_SUCCESSFUL) {
            File path = Environment.getExternalStorageDirectory();
            File dirFile = new File(path, "/" + "tumitfahr");
            File tempFile = new File(dirFile, tempImgName);
            tempFile.delete();
            File originalFile = new File(dirFile, "profile_image.png");
            originalFile.delete();
            mProgressDialog.dismiss();
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else if(result.getType() == UploadAvatarEvent.Type.UPLOAD_FAILED) {
            mProgressDialog.dismiss();
            Toast.makeText(this, "Can't upload the avatar!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_INTENT && resultCode == RESULT_OK
                && null != data) {

            Target mTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    userImageView.setImageBitmap(BitmapUtils.cropBitmap(bitmap));
                    File path = Environment.getExternalStorageDirectory();
                    File dirFile = new File(path, "/" + "tumitfahr");
                    File imageFile = new File(dirFile, tempImgName);
                    Log.e("EditProfileActivity", bitmap.getWidth() + " x " + bitmap.getHeight());
                    BitmapUtils.save(bitmap, Uri.fromFile(imageFile));
                    imageChanged = true;
                    changedImageUri = imageFile.getAbsolutePath();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e(this.getClass().getSimpleName(), errorDrawable.toString());
                    Toast.makeText(EditProfileActivity.this, getString(R.string.profile_imageError), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Uri selectedImage = data.getData();
            Log.d("EditProfileActivity", "" + selectedImage);
            Picasso.with(this)
                    .load(selectedImage.toString())
                    .placeholder(R.drawable.ic_account_dark)
                    .resize(300,300)
                    .error(R.drawable.placeholder)
                    .into(mTarget);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (detailsChanged || imageChanged) {
                    AlertDialog dialog = new AlertDialog.Builder(this).
                            setTitle("Discard Changes?").
                            setMessage("Are you sure you want to discard changes?").
                            setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).
                            setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String password) {
        //Start the edit process


    }

    @Subscribe
    public void onUpdateUserResult(UpdateUserEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == UpdateUserEvent.Type.USER_UPDATED) {
            Toast.makeText(this, getString(R.string.profile_updateSuccess), Toast.LENGTH_LONG).show();
            mCurrentUser = result.getUpdateUserResponse().getUser();
            TUMitfahrApplication.getApplication(this).getProfileService().addUserToSharedPreferences(mCurrentUser);
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        } else if (result.getType() == UpdateUserEvent.Type.UPDATE_FAILED) {
            Toast.makeText(this, getString(R.string.profile_updateFailed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        TUMitfahrApplication.getInstance().trackScreenView("Edit Profile Screen");
    }
}
