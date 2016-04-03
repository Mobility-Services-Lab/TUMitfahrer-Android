package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.networking.panoramio.PanoramioPhoto;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.ui.SearchResultsActivity;

/**
 * Authored by abhijith on 20/06/14.
 */
public class SearchResultsFragment extends Fragment {

    private List<Ride> mRides;
    private static final int LIST_ITEM_COLOR_FILTER = 0x5F000000;

    @InjectView(R.id.search_results_listview)
    ListView searchResultsList;

    View mBlankHeader;

    private RideAdapter mAdapter;
    private android.location.Geocoder mGeocoder;

    public static SearchResultsFragment newInstance(List<Ride> rides, String from, String to) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_FROM, from);
        args.putString(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_TO, to);
        args.putSerializable(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_RIDES, (java.io.Serializable) rides);
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchResultsFragment newInstance() {
        return new SearchResultsFragment();
    }

    public SearchResultsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() == null) {
            getActivity().finish();
        }
        mRides = (ArrayList<Ride>) getArguments().getSerializable(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_RIDES);
        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.inject(this, rootView);
        mBlankHeader = inflater.inflate(R.layout.blank_header, null, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new RideAdapter(getActivity());
        mAdapter.addAll(mRides);
        searchResultsList.setAdapter(mAdapter);
        searchResultsList.setOnItemClickListener(mItemClickListener);
        new PanoramioTask(mAdapter).execute(mRides);
    }

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

            String[] dateTime = ride.getDepartureTime().split(" ");
            view.findViewById(R.id.ride_location_image).setBackgroundResource(R.drawable.list_image_placeholder);
            ((TextView) view.findViewById(R.id.rides_from_text)).setText(ride.getDeparturePlace());
            ((TextView) view.findViewById(R.id.rides_to_text)).setText(ride.getDestination());
            ((TextView) view.findViewById(R.id.rides_date_text)).setText(dateTime[0]);
            ((TextView) view.findViewById(R.id.rides_time_text)).setText(dateTime[1].substring(0, 5));
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
            } else {
                return freeSeats + " " + getResources().getString(R.string.severalSeatsLeft);
            }
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
            if (!isCancelled()) {
                for (Ride ride : rides) {
                    String locationName = ride.getDestination();
                    List<Address> addresses = null;
                    // check if it meets the app logic
                    if (ride.getDestinationLatitude() != 0 && ride.getDestinationLongitude() != 0)
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
                        ride.setDestinationLatitude(address.getLatitude());
                        ride.setDestinationLongitude(address.getLongitude());

                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();

                        try {
                            PanoramioPhoto photo = TUMitfahrApplication.getApplication(getActivity()).getPanoramioService().getPhoto(longitude, latitude);
                            if (photo != null) {
                                ride.setRideImageUrl(photo.getPhotoFileUrl());
                                publishProgress();
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }
                }
            }
            return rides;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }


        @Override
        protected void onPostExecute(List<Ride> result) {

            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

}
