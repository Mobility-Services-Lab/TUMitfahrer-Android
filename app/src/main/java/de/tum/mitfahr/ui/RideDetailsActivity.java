package de.tum.mitfahr.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by abhijith on 02/10/14.
 */
public class RideDetailsActivity extends ActionBarActivity {

    public static final String RIDE_INTENT_EXTRA = "selected_ride";
    public static final String DIRECTED_FROM_CREATE = "from_create";
    public static final int DIRECTED_FROM_TIMELINE = 3;
    public static boolean needsTimelineRefresh = false;

    private Ride mRide = null;
    private Handler mHandler = new Handler();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        needsTimelineRefresh = false;
        setContentView(R.layout.activity_ride_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        changeActionBarColor(android.R.color.transparent);

        Intent intent = getIntent();
        if (intent.hasExtra(RIDE_INTENT_EXTRA)) {
            mRide = (Ride) intent.getSerializableExtra(RIDE_INTENT_EXTRA);
        } else {
            finish();
        }

        if(intent.hasExtra(DIRECTED_FROM_CREATE)) {
            needsTimelineRefresh=true;
            MainActivity.invokeBack();

        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, RideDetailsFragment.newInstance(mRide))
                    .commit();
        }
    }

    public void changeActionBarColor(int newColor) {
        Drawable newDrawable = new ColorDrawable((newColor));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newDrawable.setCallback(drawableCallback);
        } else {
            getSupportActionBar().setBackgroundDrawable(newDrawable);
        }
    }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(needsTimelineRefresh) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MainActivity.TIMELINE_UPDATE, needsTimelineRefresh);
                    setResult(RESULT_OK, resultIntent);
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(needsTimelineRefresh) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(MainActivity.TIMELINE_UPDATE, needsTimelineRefresh);
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }

    public void setNeedsTimelineRefresh(boolean flag) {
        needsTimelineRefresh = flag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        TUMitfahrApplication.getInstance().trackScreenView("Ride details Screen");
    }
}
