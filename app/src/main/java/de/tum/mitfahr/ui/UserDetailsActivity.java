package de.tum.mitfahr.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetUserEvent;
import de.tum.mitfahr.networking.models.Department;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.util.RoundTransform;

/**
 * Created by abhijith on 02/10/14.
 */
public class UserDetailsActivity extends ActionBarActivity {

    public static final String USER_INTENT_EXTRA = "selected_user";

    @InjectView(R.id.profile_image)
    CircularImageView profileImage;

    @InjectView(R.id.phoneLayout)
    LinearLayout phoneLayout;

    @InjectView(R.id.carLayout)
    LinearLayout carLayout;

    @InjectView(R.id.profile_name_text)
    TextView profileNameText;

    @InjectView(R.id.profile_email)
    TextView profileEmailText;

    @InjectView(R.id.profile_car)
    TextView profileCarText;

    @InjectView(R.id.profile_phone)
    TextView profilePhoneText;

    @InjectView(R.id.profile_department)
    TextView profileDepartmentText;

    private ProgressDialog mProgressDialog;

    private User mCurrentUser;
    private Handler mHandler = new Handler();
    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            mHandler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            mHandler.removeCallbacks(what);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.inject(this);
        changeActionBarColor(android.R.color.transparent);

        Bus mBus = BusProvider.getInstance();
        mBus.register(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);



        Intent intent = getIntent();
        if (intent.hasExtra(USER_INTENT_EXTRA)) {
            mCurrentUser = (User) intent.getSerializableExtra(USER_INTENT_EXTRA);
        } else {
            finish();
        }
        Log.e("UserDetailsActivity", mCurrentUser.toString());
        if(mCurrentUser.getEmail()==null) {
            mProgressDialog.show();
            TUMitfahrApplication.getApplication(this).getProfileService().getUser(mCurrentUser.getId());
        }
        else  showData(mCurrentUser);

    }

    @Subscribe
    public void onGetUserEvent(GetUserEvent result) {
        if (result.getType() == GetUserEvent.Type.GET_SUCCESSFUL) {
            mProgressDialog.dismiss();
            showData(result.getGetUserResponse().getUser());
        }
    }

    private void showData(User user) {

        profileNameText.setText(user.getFirstName() + " " + user.getLastName());
        profileEmailText.setText(user.getEmail());

        if(!(user.getPhoneNumber() != null && !(user.getPhoneNumber().isEmpty()))) {
            phoneLayout.setVisibility(View.GONE);
        }
        else {
            phoneLayout.setVisibility(View.VISIBLE);
            profilePhoneText.setText(user.getPhoneNumber());
        }

        if(!(user.getCar() != null && !(user.getCar().isEmpty()))) {
            carLayout.setVisibility(View.GONE);
        }
        else {
            carLayout.setVisibility(View.VISIBLE);
            profileCarText.setText(user.getCar());
        }

        ArrayList<Department> departmentArray = TUMitfahrApplication.getApplication(this).getDepartments();
        if (departmentArray.isEmpty()){
            TUMitfahrApplication.getApplication(this).getDepartmentsFromBackend();
        }

        String departmentName = user.getDepartment();
        profileDepartmentText.setText("");

        for(Department d : departmentArray){
            if(d.getName().equals(departmentName)){
                String departmentDisplayValue = d.getFriendly_name();
                profileDepartmentText.setText(departmentDisplayValue);
                break;
            }
        }

        String profileImageUrl = TUMitfahrApplication.getApplication(this).getProfileService().getProfileImageURL(user.getId());
        Picasso.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_account_dark)
                .error(R.drawable.ic_account_dark)
                .fit().centerCrop()
                .transform(new RoundTransform())
                .into(profileImage);
    }

    public void changeActionBarColor(int newColor) {
        Drawable newDrawable = new ColorDrawable((newColor));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newDrawable.setCallback(drawableCallback);
        } else {
            getSupportActionBar().setBackgroundDrawable(newDrawable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        TUMitfahrApplication.getInstance().trackScreenView("User details Screen");
    }
}
