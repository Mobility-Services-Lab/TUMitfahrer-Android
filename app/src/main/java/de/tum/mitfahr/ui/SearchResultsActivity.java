package de.tum.mitfahr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.fragments.SearchResultsFragment;

/**
 * Created by abhijith on 02/10/14.
 */
public class SearchResultsActivity extends ActionBarActivity {

    public static final String SEARCH_RIDE_RESULT_INTENT_RIDES = "search_ride_results";
    public static final String SEARCH_RIDE_RESULT_INTENT_FROM = "search_ride_from";
    public static final String SEARCH_RIDE_RESULT_INTENT_TO = "search_ride_to";


    private List<Ride> mRides = null;
    private String mTo;
    private String mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(SEARCH_RIDE_RESULT_INTENT_RIDES)) {
            mRides = (List<Ride>) intent.getSerializableExtra(SEARCH_RIDE_RESULT_INTENT_RIDES);
            mTo = intent.getStringExtra(SEARCH_RIDE_RESULT_INTENT_TO);
            mFrom = intent.getStringExtra(SEARCH_RIDE_RESULT_INTENT_FROM);
        } else {
            finish();
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, SearchResultsFragment.newInstance(mRides, mFrom, mTo))
                    .commit();
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
        TUMitfahrApplication.getInstance().trackScreenView("Search results Screen");
    }

}
