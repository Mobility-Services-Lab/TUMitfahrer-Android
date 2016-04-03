package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetActivitiesEvent;
import de.tum.mitfahr.networking.models.Activities;
import de.tum.mitfahr.networking.models.ActivitiesRideRequest;
import de.tum.mitfahr.networking.models.ActivitiesRideSearch;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.TimelineItem;
import retrofit.RetrofitError;

/**
 * Authored by abhijith on 21/06/14.
 */
public class TimelineListAllFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = TimelineListAllFragment.class.getName();
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
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TimelineItem clickedItem = mTimeline.get(position);

            if (!TimelineItem.TimelineItemType.RIDE_SEARCHED.equals(clickedItem.getType())) {
                Ride ride = clickedItem.getRide();
                if (ride != null) {
                    Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                    intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, ride);
                    startActivityForResult(intent, RideDetailsActivity.DIRECTED_FROM_TIMELINE);
                }
            }
        }
    };

    public TimelineListAllFragment() {
    }

    public static TimelineListAllFragment newInstance() {
        return new TimelineListAllFragment();
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

        floatingActionButton.attachToListView(timelineListView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(3);
            }
        });

        timelineListView.setEmptyView(swipeRefreshLayoutEmptyView);

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
        setLoading(false);
        mTimeline = timelineItems;
        Collections.sort(mTimeline);
        refreshList();
    }

    private void refreshList() {
        Log.e(TAG, "In refresh list");
        mTimelineAdapter.clear();
        mTimelineAdapter.addAll(mTimeline);
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
        refreshInvoke();
    }

    public void refreshInvoke() {
        setLoading(true);
        TUMitfahrApplication.getApplication(getActivity()).getActivitiesService().getActivities();
    }




    @Subscribe
    public void onGetActivities(GetActivitiesEvent result) {
        if (result.getType() == GetActivitiesEvent.Type.GET_SUCCESSFUL) {
            new CreateTimelineItemsTask(getActivity()).execute(result.getResponse().getActivities());
        } else if (result.getType() == GetActivitiesEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), getString(R.string.timeline_activityFail), Toast.LENGTH_SHORT).show();
        }
    }

    private class CreateTimelineItemsTask extends AsyncTask<Activities, Void, List<TimelineItem>> {

        Context localContext;

        public CreateTimelineItemsTask(Context context) {
            this.localContext = context;
        }

        @Override
        protected List<TimelineItem> doInBackground(Activities... params) {
            Activities activities = params[0];
            List<TimelineItem> items = new ArrayList<>();

            for (Ride ride : activities.getRides()) {
                if(ride.getUpdatedAt() != null) {
                    if(!ride.isRideRequest()) {
                        items.add(new TimelineItem(ride, TimelineItem.TimelineItemType.RIDE_CREATED));
                    }
                    else {
                        items.add(new TimelineItem(ride, TimelineItem.TimelineItemType.RIDE_REQUEST));
                    }
                }
            }

            for (ActivitiesRideSearch rideSearch : activities.getRideSearches()) {
                if((rideSearch.getCreatedAt() != null) && (rideSearch.getDestination()!=null)) {
                    items.add(new TimelineItem(TimelineItem.TimelineItemType.RIDE_SEARCHED,
                            rideSearch.getId(),
                            rideSearch.getDeparturePlace(),
                            rideSearch.getDestination(),
                            rideSearch.getCreatedAt()));
                }
            }
            for (ActivitiesRideRequest rideRequest : activities.getRequests()) {
                try {
                    Ride ride = TUMitfahrApplication.getApplication(localContext).getRidesService().getRideSynchronous(rideRequest.getRideId());
                    if (null != ride) {
                        items.add(new TimelineItem(TimelineItem.TimelineItemType.RIDE_JOIN_REQUEST,
                                ride.getId(),
                                ride.getDeparturePlace(),
                                ride.getDestination(),
                                rideRequest.getUpdatedAt()));
                        TUMitfahrApplication.getApplication(localContext).getRidesService().getRide(rideRequest.getRideId());
                    }
                } catch (RetrofitError e) {
                    Log.d(TAG, e.toString());
                }
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<TimelineItem> timelineItems) {
            setTimelineItems(timelineItems);
        }
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
            now = System.currentTimeMillis();
            String timeSpanString = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.FORMAT_ABBREV_TIME).toString();
            if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_CREATED)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.ic_driver);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText(getResources().getString(R.string.created_timeline));
            } else if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_JOIN_REQUEST)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.ic_passenger);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText(getResources().getString(R.string.joinReq_timeline));
            }

            ((TextView) view.findViewById(R.id.timeline_location_text)).setText(item.getDestination());
            ((TextView) view.findViewById(R.id.timeline_time_text)).setText(timeSpanString);
            return view;
        }
    }

}
