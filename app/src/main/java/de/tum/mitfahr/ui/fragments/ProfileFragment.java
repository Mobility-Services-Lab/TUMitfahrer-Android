package de.tum.mitfahr.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.ChangePasswordEvent;
import de.tum.mitfahr.networking.models.Department;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.EditProfileActivity;
import de.tum.mitfahr.util.StringHelper;

/**
 * Authored by abhijith on 22/05/14.
 */
public class ProfileFragment extends AbstractNavigationFragment implements PasswordChangeDialogFragment.PasswordChangeDialogListener {

    private static final int EDIT_PROFILE_INTENT = 1;

    @InjectView(R.id.layoutPhone)
    LinearLayout layoutPhone;

    @InjectView(R.id.layoutCar)
    LinearLayout layoutCar;

    @InjectView(R.id.profile_image)
    CircularImageView profileImage;

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

    private User mCurrentUser;
    private ProgressDialog mProgressDialog;
    private String currentImageUrl = null;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mProgressDialog = new ProgressDialog(getActivity());
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, rootView);
        changeActionBarColor(getResources().getColor(R.color.transparent));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillTheFields();
        loadProfilePicture();

    }

    private void fillTheFields() {
        profileNameText.setText(mCurrentUser.getFirstName() + " " + mCurrentUser.getLastName());
        profileEmailText.setText(mCurrentUser.getEmail());

        //user may want to delete car info, check for null, not blank string
        if (mCurrentUser.getPhoneNumber().isEmpty()) {
            layoutPhone.setVisibility(View.GONE);
        } else {
            layoutPhone.setVisibility(View.VISIBLE);
            profilePhoneText.setText(mCurrentUser.getPhoneNumber());

        }
        if (mCurrentUser.getCar().isEmpty()) {
            layoutCar.setVisibility(View.GONE);
        } else {
            layoutCar.setVisibility(View.VISIBLE);
            profileCarText.setText(mCurrentUser.getCar());
        }


        ArrayList<Department> departmentArray = TUMitfahrApplication.getApplication(getActivity()).getDepartments();
        if (departmentArray.isEmpty()){
            TUMitfahrApplication.getApplication(getActivity()).getDepartmentsFromBackend();
        }

        String departmentName = mCurrentUser.getDepartment();
        profileDepartmentText.setText("");

        for (Department d : departmentArray) {
            if (d.getName().equals(departmentName)) {
                String departmentDisplayValue = d.getFriendly_name();
                profileDepartmentText.setText(departmentDisplayValue);
                break;
            }
        }
    }

    private void loadProfilePicture() {

        currentImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getStoredAvatarURL();
        if (StringHelper.isBlank(currentImageUrl)) {
            currentImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImage();
        }
        showProfilePicture();
    }

    private void forceLoadProfilePicture() {

        String profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImage();
        currentImageUrl = profileImageUrl + "?t=" + System.currentTimeMillis();
        TUMitfahrApplication.getApplication(getActivity()).getProfileService().setStoredAvatarURL(currentImageUrl);
        showProfilePicture();
    }

    private void showProfilePicture() {
        Picasso.with(getActivity())
                .load(currentImageUrl)
                .placeholder(R.drawable.ic_account_dark)
                .error(R.drawable.ic_account_dark)
                .fit().centerCrop()
                .into(profileImage);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_INTENT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
        loadProfilePicture();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_INTENT && resultCode == getActivity().RESULT_OK) {
            mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
            fillTheFields();
            Log.e("ProfileFragment", getActivity().getCacheDir().getAbsolutePath());
            forceLoadProfilePicture();
        }
    }

    @OnClick(R.id.change_password_button)
    public void onChangePasswordClicked() {
        PasswordChangeDialogFragment dialogFragment = PasswordChangeDialogFragment.newInstance(this);
        dialogFragment.show(getFragmentManager(), "password_change");
    }

    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, String passwordOld, String passwordNew) {
        mProgressDialog.show();
        TUMitfahrApplication.getApplication(getActivity()).getProfileService().updatePassword(passwordOld, passwordNew);
    }

    @Subscribe
    public void onUpdatePasswordResult(ChangePasswordEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == ChangePasswordEvent.Type.SUCCESS) {
            Toast.makeText(getActivity(), "Changed Password", Toast.LENGTH_LONG).show();
        } else if (result.getType() == ChangePasswordEvent.Type.FAIL) {
            Toast.makeText(getActivity(), "Failed to Change Password", Toast.LENGTH_LONG).show();
        }
    }
}
