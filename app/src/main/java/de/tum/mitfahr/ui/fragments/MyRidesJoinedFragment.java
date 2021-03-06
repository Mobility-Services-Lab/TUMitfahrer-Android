package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.MyRidesAsPassengerEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.StringHelper;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Authored by abhijith on 22/06/14.
 */
public class MyRidesJoinedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RideAdapterTest mAdapter;
    @InjectView(R.id.list)
    StickyListHeadersListView ridesListView;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;
    private AdapterView.OnItemClickListener mItemClickListener = new android.widget.AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Ride clickedItem = mAdapter.getItem(position);
            if (clickedItem != null) {
                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, clickedItem);
                startActivity(intent);
            }
        }
    };
    private User mCurrentUser;

    public MyRidesJoinedFragment() {
    }

    public static MyRidesJoinedFragment newInstance() {
        return new MyRidesJoinedFragment();
    }

    private void fetchRides() {
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getMyRidesAsPassenger();
    }

    @Subscribe
    public void onGetMyRidesAsPassengerResult(MyRidesAsPassengerEvent result) {
        setLoading(false);
        if (result.getType() == MyRidesAsPassengerEvent.Type.GET_SUCCESSFUL) {
            ArrayList<Ride> ridesArray = result.getResponse().getRides();
            removeCreatedRideRequest(ridesArray);

        } else if (result.getType() == MyRidesAsPassengerEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeCreatedRideRequest(ArrayList<Ride> rides) {
        ArrayList<Ride> actualRides = new ArrayList<>();
        if((rides != null) && (rides.size()>0)) {
            actualRides.clear();
            for(Ride ride : rides) {
                if(ride.getRideOwner().getId() != mCurrentUser.getId()) {
                    actualRides.add(ride);
                }
            }
        }
        if(actualRides.size()>0) updateList(actualRides);
    }

    private void updateList(ArrayList<Ride> rides) {
        mAdapter.clear();
        mAdapter.addAll(rides);
        ridesListView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_rides_list, container, false);
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
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new RideAdapterTest(getActivity());
        ridesListView.setAdapter(mAdapter);
        ridesListView.setOnItemClickListener(mItemClickListener);
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
        setLoading(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        fetchRides();
    }

    @Override
    public void onRefresh() {
        fetchRides();
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
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    class RideAdapterTest extends ArrayAdapter<Ride> implements StickyListHeadersAdapter {

        private final LayoutInflater mInflater;

        public RideAdapterTest(Context context) {
            super(context, 0);
            mInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup view;

            if (convertView == null) {
                view = (ViewGroup) mInflater.inflate(R.layout.list_item_my_rides, parent, false);
            } else {
                view = (ViewGroup) convertView;
            }
            Ride ride = getItem(position);

            String date = StringHelper.parseDate(ride.getDepartureTime());
            String time = StringHelper.parseTime(ride.getDepartureTime());
            ((TextView) view.findViewById(R.id.my_rides_from_text)).setText(ride.getDeparturePlace());
            ((TextView) view.findViewById(R.id.my_rides_to_text)).setText(ride.getDestination());
            ((TextView) view.findViewById(R.id.my_rides_time_text)).setText(time);
            ((TextView) view.findViewById(R.id.my_rides_date_text)).setText(date);

            return view;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup viewGroup) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = mInflater.inflate(R.layout.header_separator, viewGroup, false);
                holder.text = (TextView) convertView.findViewById(R.id.section_name_text);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            holder.text.setText(getResources().getString(R.string.joinedRides));
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return 0;
        }

        class HeaderViewHolder {
            TextView text;
        }
    }
}
