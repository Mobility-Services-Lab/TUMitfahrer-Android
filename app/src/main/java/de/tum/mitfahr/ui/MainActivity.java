package de.tum.mitfahr.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.SessionCheckEvent;
import de.tum.mitfahr.gcm.DeviceManager;
import de.tum.mitfahr.gcm.PushNotificationInterface;
import de.tum.mitfahr.ui.fragments.AbstractNavigationFragment;
import de.tum.mitfahr.ui.fragments.AllRidesFragment;
import de.tum.mitfahr.ui.fragments.CreateRidesFragment;
import de.tum.mitfahr.ui.fragments.MyRidesFragment;
import de.tum.mitfahr.ui.fragments.ProfileFragment;
import de.tum.mitfahr.ui.fragments.SearchFragment;
import de.tum.mitfahr.ui.fragments.SettingsFragment;
import de.tum.mitfahr.ui.fragments.TimelineFragment;
import de.tum.mitfahr.util.ActionBarColorChangeListener;

public class MainActivity extends ActionBarActivity
        implements ActionBarColorChangeListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks, PushNotificationInterface {
    public static final String EXTRA_OFFER_RIDE = "offer_ride";
    private static final String TAG_TIMELINE_FRAGMENT = "timeline_fragment";
    private static final String TAG_CAMPUS_RIDES_FRAGMENT = "campus_rides_fragment";
    private static final String TAG_CREATE_RIDE_FRAGMENT = "create_ride_fragment";
    private static final String TAG_SEARCH_FRAGMENT = "search_fragment";
    private static final String TAG_MY_RIDES_FRAGMENT = "my_rides_fragment";
    private static final String TAG_SETTINGS_FRAGMENT = "settings_fragment";
    private static final String TAG_PROFILE_FRAGMENT = "profile_fragment";

    private Toolbar toolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private AbstractNavigationFragment mCurrentFragment;

    public static final String TIMELINE_UPDATE = "update_timeline";
    public static final int TIMELINE_UPDATE_CODE = 5;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mCurrentPosition = -1;

    private int mCurrentActionBarColor = 0xFF0F3750;

    private static Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainContext = this;

        //check if the session is valid
        TUMitfahrApplication.getApplication(this).getProfileService().checkSessionValidity();


        if (!TUMitfahrApplication.getApplication(this).getProfileService().isLoggedIn()) {
            Intent intent = new Intent(this, LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("first_run", true)) {
            prefs.edit().putBoolean("first_run", false).commit();
            Intent intent = new Intent(this, AboutActivity.class);
            startActivityForResult(intent, TIMELINE_UPDATE_CODE);
        }

        mTitle = getTitle();

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            onNavigationDrawerItemSelected(1, "Timeline");
        } else {
            mTitle = savedInstanceState.getCharSequence("title", null);
            restoreActionBar();
            findAndAddFragment();
        }

        //manage device registration
        DeviceManager deviceManager = new DeviceManager(this);
        deviceManager.findOrRegisterDevice();


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Subscribe
    public void onSessionCheckResult(SessionCheckEvent result) {
        if (result.getType() == SessionCheckEvent.Type.INVALID) {
            Intent intent = new Intent(this, LoginRegisterActivity.class);
            Toast.makeText(this, "Your session has expired. Please login.", Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
        }
    }

    private void findAndAddFragment() {
        if (getFragmentManager().findFragmentByTag(TAG_TIMELINE_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_TIMELINE_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_CAMPUS_RIDES_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_CAMPUS_RIDES_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_CREATE_RIDE_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_CREATE_RIDE_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_SEARCH_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_SEARCH_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_MY_RIDES_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_MY_RIDES_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_SETTINGS_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_SETTINGS_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_PROFILE_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_PROFILE_FRAGMENT);
        }
    }

    @Override
    public void onBackPressed() {
        if ((mCurrentFragment == getSupportFragmentManager().findFragmentByTag(TAG_TIMELINE_FRAGMENT))
                && (!mNavigationDrawerFragment.isDrawerOpen())) {
            super.onBackPressed();
        } else if (mNavigationDrawerFragment.isDrawerOpen()) {
            this.getNavigationDrawerFragment().closeDrawer();
        } else {
            //return to timeline fragment
            this.getNavigationDrawerFragment().selectItem(1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            boolean resultBool = data.getBooleanExtra(TIMELINE_UPDATE, false);
            if ((resultBool) && ((mCurrentFragment == getSupportFragmentManager().findFragmentByTag(TAG_TIMELINE_FRAGMENT)))) {
                ((TimelineFragment) mCurrentFragment).invokeRefresh();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else if ((requestCode == TIMELINE_UPDATE_CODE) && (resultCode == RESULT_OK)) {
            invokeBack();
        } else {
            super.onActivityResult(requestCode, resultCode, null);
        }
    }

    public static Context getAppContext() {
        return MainActivity.mainContext;
    }

    public static void invokeBack() {
        ((MainActivity) getAppContext()).getNavigationDrawerFragment().selectItem(1);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, String title) {
        // update the main content by replacing fragments
        if (position == mCurrentPosition)
            return;
        mCurrentPosition = position;
        mTitle = title;
        restoreActionBar();
        FragmentManager fragmentManager = getSupportFragmentManager();
        setLollipopStatusBarColor(position);
        switch (position) {
            case 0:
                mCurrentFragment = ProfileFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_PROFILE_FRAGMENT)
                        .commit();
                break;

            case 1:
                mCurrentFragment = TimelineFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_TIMELINE_FRAGMENT)
                        .commit();
                break;
            case 2:
                mCurrentFragment = AllRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_CAMPUS_RIDES_FRAGMENT)
                        .commit();
                break;

            case 3:
                mCurrentFragment = CreateRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_CREATE_RIDE_FRAGMENT)
                        .commit();
                break;

            case 4:
                mCurrentFragment = SearchFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_SEARCH_FRAGMENT)
                        .commit();
                break;

            case 5:
                mCurrentFragment = MyRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_MY_RIDES_FRAGMENT)
                        .commit();
                break;

            case 6:
                mCurrentFragment = SettingsFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_SETTINGS_FRAGMENT)
                        .commit();
                break;
        }
    }

    @TargetApi(21)
    private void setLollipopStatusBarColor(int position) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        switch (position) {
            case 0:
                getWindow().setStatusBarColor(getResources().getColor(R.color.gray));
                break;
            case 1:
                getWindow().setStatusBarColor(getResources().getColor(R.color.blue1_dark));
                break;
            case 2:
                getWindow().setStatusBarColor(getResources().getColor(R.color.blue2_dark));
                break;
            case 3:
                getWindow().setStatusBarColor(getResources().getColor(R.color.blue3_dark));
                break;
            case 4:
                getWindow().setStatusBarColor(getResources().getColor(R.color.blue3_dark));
                break;
            case 5:
                getWindow().setStatusBarColor(getResources().getColor(R.color.blue4_dark));
                break;
            case 6:
                getWindow().setStatusBarColor(getResources().getColor(R.color.gray_dark));
                break;
        }
    }

    public void setTitle(CharSequence title) {
        if (title != null) {
            getActionBar().setTitle(title);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActionBarColorChanged(int newColor) {
        mCurrentActionBarColor = newColor;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("title", mTitle);
        outState.putBoolean("isDrawerOpen",
                mNavigationDrawerFragment.isDrawerOpen());
    }

    public int getCurrentActionBarColor() {
        return mCurrentActionBarColor;
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    public NavigationDrawerFragment getNavigationDrawerFragment() {
        return mNavigationDrawerFragment;
    }

    @Override
    public void onPlayServiceRegistrationComplete(String id) {
        if (id == null) {
            Toast.makeText(this, "There is some problem with registration. Please try again!", Toast.LENGTH_LONG).show();
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

}