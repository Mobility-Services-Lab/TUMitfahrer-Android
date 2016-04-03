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
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetRidesEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.networking.panoramio.PanoramioPhoto;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.LocationUtil;
import de.tum.mitfahr.util.StringHelper;

/**
 * Authored by abhijith on 21/06/14.
 */
public class RidesAroundListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int MAX_DISTANCE = 5; //kilometers
    private static final int LIST_ITEM_COLOR_FILTER = 0x5F000000;

    @InjectView(R.id.rides_listview)
    ListView ridesListView;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;
    @InjectView(R.id.button_floating_action)
    FloatingActionButton floatingActionButton;

    private List<Ride> mRides = new ArrayList<>();
    private AlphaInAnimationAdapter mAdapter;
    private RideAdapter mRidesAdapter;

    private GoogleApiClient mGoogleAPIClient;
    private static boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    //default: Munich
    private LatLng mCurrentLocation = new LatLng(48.1333, 11.5667);

    private GetAddressTask mGetAddressTask;
    private Geocoder mGeocoder;
    private AdapterView.OnItemClickListener mItemClickListener = new android.widget.AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Ride clickedItem = mRides.get(position);
            if (clickedItem != null) {
                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, clickedItem);
                startActivity(intent);
            }
        }
    };
    private ArrayList<Ride> currentRides = new ArrayList<>();

    public RidesAroundListFragment() {
    }

    public static RidesAroundListFragment newInstance(int rideType) {
        RidesAroundListFragment fragment = new RidesAroundListFragment();
        Bundle args = new Bundle();
        args.putInt("ride_type", rideType);
        fragment.setArguments(args);
        return fragment;
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
        TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ride_list, container, false);
        ButterKnife.inject(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue1,
                R.color.blue2,
                R.color.blue3);

        swipeRefreshLayoutEmptyView.setOnRefreshListener(this);
        swipeRefreshLayoutEmptyView.setColorSchemeResources(R.color.blue1,
                R.color.blue2,
                R.color.blue3);

        ridesListView.setEmptyView(swipeRefreshLayoutEmptyView);

        floatingActionButton.attachToListView(ridesListView);
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
        mRidesAdapter = new RideAdapter(getActivity());
        mAdapter = new AlphaInAnimationAdapter(mRidesAdapter);
        mAdapter.setAbsListView(ridesListView);
        ridesListView.setAdapter(mAdapter);
        ridesListView.setOnItemClickListener(mItemClickListener);
        getRides();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    public void setRides(List<Ride> rides) {
        setLoading(false);
        synchronized (currentRides) {
            removeOldRides(rides);
        }
        synchronized (mRides) {
            mRides.clear();
            mRides.addAll(currentRides);
        }
        refreshList();

    }

    private List<Ride> removeOldRides(List<Ride> rides) {
        Date currentDate = new Date();
        SimpleDateFormat outputFormat = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
        for (Ride ride : rides) {
            Date date1;
            try {
                date1 = outputFormat.parse(ride.getDepartureTime());

                if (!(date1.before(currentDate))) {
                    if (!currentRides.contains(ride))
                        currentRides.add(ride);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return currentRides;
    }

    private void refreshList() {
        mRidesAdapter.clear();
        if (mRides != null) {
            mRidesAdapter.addAll(mRides);
        }
        mAdapter.notifyDataSetChanged();
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

    @Override
    public void onRefresh() {
        if (mGetAddressTask != null && mGetAddressTask.getStatus() == AsyncTask.Status.RUNNING) {
            mGetAddressTask.cancel(true);
            mGetAddressTask = null;
        }
        getRides();
    }

    private void getRides() {
        setLoading(true);
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getAllRides(0);
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getAllRides(1);
    }

    @Subscribe
    public void onGetRidesResult(GetRidesEvent result) {
        if (result.getType() == GetRidesEvent.Type.GET_SUCCESSFUL) {
            new GetAddressTask().execute(result.getResponse().getRides());
        } else if (result.getType() == GetRidesEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Fetching Rides Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);
        if (location != null)
            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
        Toast.makeText(getActivity(), "Cannot get location.Make sure the location sharing is enabled", Toast.LENGTH_SHORT).show();
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
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    protected class GetAddressTask extends AsyncTask<List<Ride>, Void, List<Ride>> {

        public GetAddressTask() {
            super();
        }

        @Override
        protected List<Ride> doInBackground(List<Ride>... params) {
            List<Ride> rides = params[0];
            List<Ride> nearbyRides = new ArrayList<Ride>();

            if (!isCancelled()) {
                synchronized (rides) {
                    nearbyRides.clear();
                    for (Ride ride : rides) {

                        Ride cloneRide = ride;
                        String locationName = cloneRide.getDeparturePlace();
                        List<Address> addresses = null;
                        try {
                            addresses = mGeocoder.getFromLocationName(locationName, 5);
                        } catch (IOException | IllegalArgumentException exception1) {
                            exception1.printStackTrace();
                        }
                        // If the reverse geocode returned an address
                        if (addresses != null && addresses.size() > 0) {
                            // Get the first address
                            Address address1 = addresses.get(0);
                            cloneRide.setLatitude(address1.getLatitude());
                            cloneRide.setDepartureLongitude(address1.getLongitude());

                            if (LocationUtil.haversineDistance(
                                    mCurrentLocation.latitude,
                                    mCurrentLocation.longitude,
                                    cloneRide.getLatitude(),
                                    cloneRide.getDepartureLongitude()) < MAX_DISTANCE) {

                                //get the panoramio picture of the destination place
                                String destination = cloneRide.getDestination();
                                List<Address> destAddresses = null;
                                try {
                                    destAddresses = mGeocoder.getFromLocationName(destination, 5);
                                } catch (IOException | IllegalArgumentException exception1) {
                                    exception1.printStackTrace();
                                }
                                // If the reverse geocode returned an address
                                if (destAddresses != null && destAddresses.size() > 0) {
                                    // Get the first address
                                    Address address2 = destAddresses.get(0);

                                    double latitude = address2.getLatitude();
                                    double longitude = address2.getLongitude();

                                    try {
                                        PanoramioPhoto photo = TUMitfahrApplication.getApplication(getActivity()).getPanoramioService().getPhoto(longitude, latitude);

                                        if (photo != null) {
                                            cloneRide.setRideImageUrl(photo.getPhotoFileUrl());
                                        }
                                    } catch (Exception e) {
                                        return null;
                                    }
                                }
                                nearbyRides.add(cloneRide);
                            }
                        }
                    }
                }
            }
            return nearbyRides;
        }

        @Override
        protected void onPostExecute(List<Ride> result) {
            if (null == result) result = new ArrayList<>();
            setRides(result);
        }
    }

    class RideAdapter extends ArrayAdapter<Ride> {
        private final LayoutInflater mInflater;

        public RideAdapter(Context context) {
            super(context, 0);
            mInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup view;
            if (convertView == null) {
                view = (ViewGroup) mInflater.inflate(R.layout.list_item_rides, parent, false);
            } else {
                view = (ViewGroup) convertView;
            }
            Ride ride = getItem(position);

            String date = StringHelper.parseDate(ride.getDepartureTime());
            String time = StringHelper.parseTime(ride.getDepartureTime());

            view.findViewById(R.id.ride_location_image).setBackgroundResource(R.drawable.list_image_placeholder);
            ((TextView) view.findViewById(R.id.rides_from_text)).setText(ride.getDeparturePlace().split(",")[0]);
            ((TextView) view.findViewById(R.id.rides_to_text)).setText(ride.getDestination().split(",")[0]);
            ((TextView) view.findViewById(R.id.rides_date_text)).setText(date);
            ((TextView) view.findViewById(R.id.rides_time_text)).setText(time);

            if (ride.getRideType() == 0) {
                ((TextView) view.findViewById(R.id.campus_activity)).setText(getResources().getString(R.string.ridetype_Campus));
                ((TextView) view.findViewById(R.id.campus_activity)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_school_white_24dp, 0, 0, 0);
            } else {
                ((TextView) view.findViewById(R.id.campus_activity)).setText(getResources().getString(R.string.ridetype_Activity));
                ((TextView) view.findViewById(R.id.campus_activity)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_activity, 0, 0, 0);
            }

            view.findViewById(R.id.ride_seats_text).setVisibility(View.VISIBLE);
            String description = getFreeSeatText(ride);
            if(description.equals(getResources().getString(R.string.noSeatsLeft))){
                view.findViewById(R.id.ride_seats_text).setBackgroundColor(getResources().getColor(R.color.float_label_inactive));
            } else {
                view.findViewById(R.id.ride_seats_text).setBackgroundColor(getResources().getColor(R.color.holo_orange_dark));
            }
            ((TextView) view.findViewById(R.id.ride_seats_text)).setText(description);


            ImageView locationImage = ((ImageView) view.findViewById(R.id.ride_location_image));
            locationImage.setColorFilter(LIST_ITEM_COLOR_FILTER);
            Picasso.with(getActivity())
                    .load(ride.getRideImageUrl())
                    .placeholder(R.drawable.list_image_placeholder)
                    .error(R.drawable.list_image_placeholder)
                    .into(locationImage);

            return view;
        }

        private String getFreeSeatText(Ride mRide) {
            ArrayList<User> actualPassengers = new ArrayList<>();
            actualPassengers.clear();
            for (final User passenger : mRide.getPassengers()) {
                if (mRide.getRideOwner().getId() != passenger.getId()) {
                    actualPassengers.add(passenger);
                }
            }
            int freeSeats = mRide.getFreeSeats() - actualPassengers.size();
            if (freeSeats == 1) {
                return getResources().getString(R.string.oneSeatLeft);
            } else if ((freeSeats > 0) && (freeSeats < mRide.getFreeSeats())) {
                return freeSeats + " " + getResources().getString(R.string.severalSeatsLeft);
            } else if (freeSeats <= 0) {
                return getResources().getString(R.string.noSeatsLeft);
            }
            return mRide.getFreeSeats() + " " + getResources().getString(R.string.severalSeatsLeft);
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
        public ErrorDialogFragment() {
        }

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
