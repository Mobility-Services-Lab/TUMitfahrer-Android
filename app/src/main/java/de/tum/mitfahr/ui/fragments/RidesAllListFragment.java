package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import de.tum.mitfahr.util.StringHelper;

/**
 * Authored by abhijith on 21/06/14.
 */
public class RidesAllListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

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
    private Geocoder mGeocoder;
    private PanoramioTask mPanoramioTask;

    ArrayList<Ride> currentRides = new ArrayList<>();

    private Comparator mTimeComparator = new Comparator<Ride>() {
        @Override
        public int compare(Ride ride1, Ride ride2) {
            SimpleDateFormat outputFormat = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = outputFormat.parse(ride1.getDepartureTime());
                date2 = outputFormat.parse(ride2.getDepartureTime());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date2.compareTo(date1);
        }
    };
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

    public RidesAllListFragment() {
    }

    public static RidesAllListFragment newInstance(int rideType) {
        RidesAllListFragment fragment = new RidesAllListFragment();
        Bundle args = new Bundle();
        args.putInt("ride_type", rideType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
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
        mRides.clear();
        currentRides.clear();
        getRides();
    }

    @Override
    public void onRefresh() {
        if (mPanoramioTask != null && mPanoramioTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPanoramioTask.cancel(true);
            mPanoramioTask = null;
        }
        getRides();
    }

    /**
     * Gets all campus and activity rides from the backend.
     */
    private void getRides() {

        setLoading(true);
        currentRides.clear();
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getAllRides(0);
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getAllRides(1);
    }

    @Subscribe
    public void onGetRide(GetRidesEvent result) {
        if (result.getType() == GetRidesEvent.Type.GET_SUCCESSFUL) {
            setRides(result.getResponse().getRides());
        } else if (result.getType() == GetRidesEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), getResources().getString(R.string.rides_error), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Adds all rides to the current rides.
     *
     * @param rides the rides to be added
     */
    public void setRides(List<Ride> rides) {

        synchronized (currentRides) {
            removeOldRides(rides);
        }
        synchronized (mRides) {
            mRides.clear();
            mRides.addAll(currentRides);
        }
        refreshList();

        new PanoramioTask(mRidesAdapter).execute(mRides);
    }

    /**
     * Removes rides in the past and adds only rides that are not already in the currentRides list to that list.
     *
     * @param rides the rides to be checked for past rides and rides in the currentRides list
     * @return the current rides
     */
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

    /**
     * Sorts the rides according to the departure date and refreshes the list.
     */
    private void refreshList() {

        synchronized (mRides) {
            Collections.sort(mRides, mTimeComparator);
        }
        setLoading(false);
        mRidesAdapter.clear();
        mRidesAdapter.addAll(mRides);
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
    public void onStop() {
        super.onStop();
        if (mPanoramioTask != null && mPanoramioTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPanoramioTask.cancel(true);
            mPanoramioTask = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
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
            ((TextView) view.findViewById(R.id.rides_from_text)).setText(ride.getDeparturePlace());
            ((TextView) view.findViewById(R.id.rides_to_text)).setText(ride.getDestination());

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
            if (description.equals(getResources().getString(R.string.noSeatsLeft))) {
                view.findViewById(R.id.ride_seats_text).setBackgroundColor(getResources().getColor(R.color.transparent_black));
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

        /**
         * Calculates the free seats for a ride and gets the text for the free seats textview.
         *
         * @param mRide the ride for which the free seats should be calculated
         * @return the text for the free seats textview
         */
        private String getFreeSeatText(Ride mRide) {
            ArrayList<User> actualPassengers = new ArrayList<>();
            actualPassengers.clear();
            for (final User passenger : mRide.getPassengers()) {
                if (mRide.getRideOwner().getId() != passenger.getId()) {
                    actualPassengers.add(passenger);
                }
            }
            int freeSeats = mRide.getFreeSeats() - actualPassengers.size();
            if (freeSeats == 1) return getResources().getString(R.string.oneSeatLeft);
            else if ((freeSeats > 0) && (freeSeats < mRide.getFreeSeats()))
                return freeSeats + " " + getResources().getString(R.string.severalSeatsLeft);
            else if (freeSeats <= 0) return getResources().getString(R.string.noSeatsLeft);
            return mRide.getFreeSeats() + " " + getResources().getString(R.string.severalSeatsLeft);
        }
    }

    private class PanoramioTask extends AsyncTask<List<Ride>, Void, List<Ride>> {

        RideAdapter adapter;

        public PanoramioTask(RideAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected List<Ride> doInBackground(List<Ride>... params) {
            List<Ride> rides = params[0];
            List<Ride> resultRides = new ArrayList<Ride>();
            if (!isCancelled()) {
                synchronized (rides) {
                    resultRides.clear();
                    for (Ride ride : rides) {
                        Ride cloneRide = ride;
                        String locationName = cloneRide.getDestination();
                        List<Address> addresses = null;
                        if (cloneRide.getLatitude() != 0 && cloneRide.getDestinationLongitude() != 0)
                            continue;
                        try {
                            addresses = mGeocoder.getFromLocationName(locationName, 5);
                        } catch (IOException | IllegalArgumentException exception1) {
                            exception1.printStackTrace();
                        }
                        // If the reverse geocode returned an address
                        if (addresses != null && addresses.size() > 0) {
                            // Get the first address
                            Address address = addresses.get(0);
                            cloneRide.setLatitude(address.getLatitude());
                            cloneRide.setDestinationLongitude(address.getLongitude());

                            double latitude = address.getLatitude();
                            double longitude = address.getLongitude();

                            try {
                                PanoramioPhoto photo = TUMitfahrApplication.getApplication(getActivity()).getPanoramioService().getPhoto(longitude, latitude);
                                if (photo != null) {
                                    cloneRide.setRideImageUrl(photo.getPhotoFileUrl());
                                    publishProgress();
                                }
                            } catch (Exception e) {
                                return null;
                            }

                        }
                        resultRides.add(cloneRide);
                    }
                }
            }
            return resultRides;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(List<Ride> result) {

            adapter.notifyDataSetChanged();
        }
    }

}