package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.util.RoundTransform;
import de.tum.mitfahr.util.StringHelper;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    @InjectView(R.id.header_drawer_first_name)
    TextView mFirstNameView;
    @InjectView(R.id.header_drawer_last_name)
    TextView mLastNameView;
    @InjectView(R.id.header_drawer_profile_image)
    CircularImageView mProfileImageView;
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private View mProfileHeaderView;
    private DrawerAdapter mAdapter;
    private int mCurrentSelectedPosition = 1;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        selectItem(mCurrentSelectedPosition);

        loadProfilePicture();
    }

    private void loadProfilePicture() {
        String profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getStoredAvatarURL();
        if (StringHelper.isBlank(profileImageUrl))
            profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImage();

        Picasso.with(getActivity())
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_account_dark)
                .fit().centerCrop()
                .transform(new RoundTransform())
                .into(mProfileImageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mProfileHeaderView = (View) inflater.inflate(R.layout.header_item_profile_drawer, null);
        ButterKnife.inject(this, mProfileHeaderView);
        mDrawerListView.addHeaderView(mProfileHeaderView);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        String navTitles[] = getResources().getStringArray(R.array.navigation_drawer_array);
        mAdapter = new DrawerAdapter(getActivity());
        mAdapter.add(new DrawerItem(navTitles[0], R.drawable.ic_timeline2, DrawerType.TYPE1));// Timeline
        mAdapter.add(new DrawerItem(navTitles[1], R.drawable.ic_car_white, DrawerType.TYPE2));// All rides
        //no distinction between campus and activity anymore
        //mAdapter.add(new DrawerItem(navTitles[2], R.drawable.ic_activity, DrawerType.TYPE2));// Activity
        mAdapter.add(new DrawerItem(navTitles[2], R.drawable.ic_add, DrawerType.TYPE3));// Create
        mAdapter.add(new DrawerItem(navTitles[3], R.drawable.ic_search, DrawerType.TYPE3));// Search
        //the "get a car" function is not used at the moment and should be improved before reintegrating into the app
        //mAdapter.add(new DrawerItem(navTitles[5], R.drawable.ic_share_car64, DrawerType.TYPE4));// GET a Car last digit represents dimensions
        mAdapter.add(new DrawerItem(navTitles[4], R.drawable.ic_car_white, DrawerType.TYPE4));// MyRides
        mAdapter.add(new DrawerItem(navTitles[5], R.drawable.ic_settings, DrawerType.TYPE2));// Settings
        mAdapter.add(new DrawerItem(navTitles[6], R.drawable.ic_logout, DrawerType.TYPE2));// logout

        mDrawerListView.setAdapter(mAdapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayUserName();

    }

    private void displayUserName() {
        User currentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
        mFirstNameView.setText(currentUser.getFirstName());
        mLastNameView.setText(currentUser.getLastName());
    }

    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        mAdapter.setSelectedItem(position - 1);
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {

            if (position == 7) {

                AlertDialog dialog = new AlertDialog.Builder(getActivity()).
                        setTitle(R.string.logOutTitle).
                        setMessage(R.string.logOutMessage).
                        setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TUMitfahrApplication.getApplication(getActivity()).getProfileService().logout();
                                Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }).
                        setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();


            }
            else if (position == 0) {
                mCallbacks.onNavigationDrawerItemSelected(position, " ");
            } else {
                mCallbacks.onNavigationDrawerItemSelected(position, mAdapter.getItem(position - 1).mTitle);
            }
        }
    }

    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserName();
        loadProfilePicture();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private enum DrawerType {
        TYPE1,
        TYPE2,
        TYPE3,
        TYPE4
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position, String title);
    }

    private class DrawerAdapter extends ArrayAdapter<DrawerItem> {

        private int selectedItem;

        public DrawerAdapter(Context context) {
            super(context, 0);
        }

        public void setSelectedItem(int selectedItem) {
            this.selectedItem = selectedItem;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DrawerItem item = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_drawer, parent, false);
                holder = new ViewHolder();
                holder.attach(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            switch (item.mType) {
                case TYPE1:
                    convertView.setBackgroundResource(R.drawable.drawer_activated_background_type1);
                    break;
                case TYPE2:
                    convertView.setBackgroundResource(R.drawable.drawer_activated_background_type2);
                    break;
                case TYPE3:
                    convertView.setBackgroundResource(R.drawable.drawer_activated_background_type3);
                    break;
                case TYPE4:
                    convertView.setBackgroundResource(R.drawable.drawer_activated_background_type4);
                    break;
            }
            holder.title.setText(item.mTitle);
            holder.title.setTypeface(null, position == selectedItem ? Typeface.BOLD : Typeface.NORMAL);
            holder.icon.setImageResource(item.mIconResource);
            return convertView;
        }

        private class ViewHolder {
            public TextView title;
            public ImageView icon;

            public void attach(View v) {
                title = (TextView) v.findViewById(R.id.menu_title);
                icon = (ImageView) v.findViewById(R.id.menu_icon);
            }
        }
    }

    private class DrawerItem {
        String mTitle;
        int mIconResource;
        DrawerType mType;

        private DrawerItem(String mTitle, int mIconResource, DrawerType mType) {
            this.mTitle = mTitle;
            this.mIconResource = mIconResource;
            this.mType = mType;
        }
    }
}
