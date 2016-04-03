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

import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.TimelineItem;

/**
 * Authored by abhijith on 21/06/14.
 */
public class TimelineListLastMinuteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = TimelineListLastMinuteFragment.class.getName();
    private static final int MAX_TIME = 600; //seconds
    @InjectView(R.id.rides_listview)
    ListView timelineList;
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
                    startActivity(intent);
                }
            }
        }
    };

    public TimelineListLastMinuteFragment() {
    }

    public static TimelineListLastMinuteFragment newInstance() {
        return new TimelineListLastMinuteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        timelineList.setEmptyView(swipeRefreshLayoutEmptyView);

        floatingActionButton.attachToListView(timelineList);
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

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        outputFormat.setTimeZone(TimeZone.getDefault());

        mTimelineAdapter = new TimelineAdapter(getActivity());
        mAdapter = new AlphaInAnimationAdapter(mTimelineAdapter);
        mAdapter.setAbsListView(timelineList);
        timelineList.setAdapter(mAdapter);
        timelineList.setOnItemClickListener(mItemClickListener);

        TUMitfahrApplication.getApplication(getActivity()).getActivitiesService().getActivities();
        setLoading(true);
    }

    public void setTimelineItems(List<TimelineItem> timelineItems) {
        setLoading(false);
        FilterLastMinuteTask mLastMinuteTask = new FilterLastMinuteTask();
        mLastMinuteTask.execute(timelineItems);
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
        setLoading(true);
        TUMitfahrApplication.getApplication(getActivity()).getActivitiesService().getActivities();
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

    protected class FilterLastMinuteTask extends AsyncTask<List<TimelineItem>, Void, List<TimelineItem>> {

        public FilterLastMinuteTask() {
            super();
        }

        @Override
        protected List<TimelineItem> doInBackground(List<TimelineItem>... params) {
            List<TimelineItem> items = params[0];
            List<TimelineItem> nearbyItems = new ArrayList<>();
            Date now = new Date();

            if (!isCancelled()) {
                for (TimelineItem item : items) {
                    Date date = item.getTime();
                    long seconds = (now.getTime() - date.getTime()) / 1000;

                    if (seconds > 0 && seconds < MAX_TIME) {
                        nearbyItems.add(item);
                    }
                }
            }
            return nearbyItems;
        }

        @Override
        protected void onPostExecute(List<TimelineItem> result) {
            setLoading(false);
            mTimeline = result;
            refreshList();
        }
    }

}
