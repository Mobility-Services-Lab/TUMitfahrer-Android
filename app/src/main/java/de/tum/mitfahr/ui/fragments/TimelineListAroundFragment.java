package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.LocationUtil;
import de.tum.mitfahr.util.TimelineItem;

/**
 * Authored by abhijith on 21/06/14.
 */
public class TimelineListAroundFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = TimelineListAroundFragment.class.getName();
    private static final int MAX_DISTANCE = 5; //kilometers
    @InjectView(R.id.rides_listview)
    ListView timelineListView;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;
    @InjectView(R.id.button_floating_action)
    FloatingActionButton floatingActionButton;
    private List<TimelineItem> mTimeline = new ArrayList<>();
    private TimelineAdapter mTimelineAdapter;
    private AlphaInAnimationAdapter mAdapter;

    private GoogleApiClient mGoogleAPIClient;
    private static boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    //default: Munich
    private LatLng mCurrentLocation = new LatLng(48.1333, 11.5667);


    private Comparator mLocationComparator = new Comparator<TimelineItem>() {
        @Override
        public int compare(TimelineItem ride1, TimelineItem ride2) {
            Double distance1 = LocationUtil.haversineDistance(mCurrentLocation.latitude,
                    mCurrentLocation.longitude,
                    ride1.getLat(), ride1.getLng());
            Double distance2 = LocationUtil.haversineDistance(mCurrentLocation.latitude,
                    mCurrentLocation.longitude,
                    ride2.getLat(), ride2.getLng());
            return distance1.compareTo(distance2);
        }
    };

    private GetAddressTask mGetAddressTask;
    private Geocoder mGeocoder;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TimelineItem clickedItem = mTimeline.get(position);

            if (!TimelineItem.TimelineItemType.RIDE_SEARCHED.equals(clickedItem.getType())) {
                Ride ride = clickedItem.getRide();
                if (ride != null) {
                    Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                    intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, ride);
                    startActivity(intent);
                }
            }
        }
    };

    public TimelineListAroundFragment() {
    }

    public static TimelineListAroundFragment newInstance() {
        return new TimelineListAroundFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleAPIClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline_list, container, false);
        ButterKnife.inject(this, rootView);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue1,
                R.color.blue2,
                R.color.blue3);

        swipeRefreshLayoutEmptyView.setOnRefreshListener(this);
        swipeRefreshLayoutEmptyView.setColorSchemeResources(R.color.blue1,
                R.color.blue2,
                R.color.blue3);

        timelineListView.setEmptyView(swipeRefreshLayoutEmptyView);
        floatingActionButton.attachToListView(timelineListView);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(3);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTimelineAdapter = new TimelineAdapter(getActivity());
        mAdapter = new AlphaInAnimationAdapter(mTimelineAdapter);
        mAdapter.setAbsListView(timelineListView);
        timelineListView.setAdapter(mAdapter);
        timelineListView.setOnItemClickListener(mItemClickListener);
        setLoading(true);
    }

    public void setTimelineItems(List<TimelineItem> timelineItems) {
        mTimeline = timelineItems;
        mGetAddressTask = new GetAddressTask();
        mGetAddressTask.execute(mTimeline);
    }

    private void setLoading(final boolean loading) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
            }
        });
        swipeRefreshLayoutEmptyView.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayoutEmptyView.setRefreshing(loading);
            }
        });
    }

    private void refreshList() {
        Log.e(TAG, "In refresh list");
        mTimelineAdapter.clear();
        mTimelineAdapter.addAll(mTimeline);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGetAddressTask != null)
            mGetAddressTask.cancel(true);
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleAPIClient.connect();
        }
    }

    @Override
    public void onStop() {
        mGoogleAPIClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        TUMitfahrApplication.getApplication(getActivity()).getActivitiesService().getActivities();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);
        if (location != null) {
            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleAPIClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
        Toast.makeText(getActivity(), "Cannot get location. Make sure the location sharing is enabled", Toast.LENGTH_SHORT).show();
    }

    private class TimelineAdapter extends ArrayAdapter<TimelineItem> {

        private final LayoutInflater mInflater;
        private long now;

        public TimelineAdapter(Context context) {
            super(context, 0);
            mInflater = LayoutInflater.from(context);
            now = System.currentTimeMillis();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup view;

            if (convertView == null) {
                view = (ViewGroup) mInflater.inflate(R.layout.list_item_timeline, parent, false);
            } else {
                view = (ViewGroup) convertView;
            }
            TimelineItem item = getItem(position);
            long time = item.getTime().getTime();
            String timeSpanString = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.FORMAT_ABBREV_TIME).toString();
            if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_CREATED)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.ic_driver);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText(getResources().getString(R.string.created_timeline));

            }
            else if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_JOIN_REQUEST)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.ic_passenger);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText(getResources().getString(R.string.joinReq_timeline));
            }

            ((TextView) view.findViewById(R.id.timeline_location_text)).setText(item.getDestination());
            ((TextView) view.findViewById(R.id.timeline_time_text)).setText(timeSpanString);
            return view;
        }
    }

    protected class GetAddressTask extends AsyncTask<List<TimelineItem>, Void, List<TimelineItem>> {

        public GetAddressTask() {
            super();
        }

        @Override
        protected List<TimelineItem> doInBackground(List<TimelineItem>... params) {
            List<TimelineItem> items = params[0];
            List<TimelineItem> nearbyItems = new ArrayList<>();

            if (!isCancelled()) {
                for (TimelineItem item : items) {
                    String locationName = item.getDeparture();
                    List<Address> addresses = null;
                    try {
                        addresses = mGeocoder.getFromLocationName(locationName, 5);
                    } catch (IOException | IllegalArgumentException exception1) {
                        exception1.printStackTrace();
                    }
                    // If the reverse geocode returned an address
                    if (addresses != null && addresses.size() > 0) {
                        // Get the first address
                        Address address = addresses.get(0);
                        item.setLat(address.getLatitude());
                        item.setLng(address.getLongitude());

                        if (LocationUtil.haversineDistance(
                                mCurrentLocation.latitude,
                                mCurrentLocation.longitude,
                                item.getLat(),
                                item.getLng()) < MAX_DISTANCE) {
                            nearbyItems.add(item);
                        }
                    }
                }
            }
            return nearbyItems;
        }

        @Override
        protected void onPostExecute(List<TimelineItem> result) {
            setLoading(false);
            mTimeline = result;
            Collections.sort(mTimeline, mLocationComparator);
            refreshList();
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public static void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            onDialogDismissed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleAPIClient.isConnecting() &&
                        !mGoogleAPIClient.isConnected()) {
                    mGoogleAPIClient.connect();
                }
            }
        }
    }
}
