package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;

/**
 * Authored by abhijith on 22/05/14.
 */
public class MyRidesFragment extends AbstractNavigationFragment {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.pager)
    ViewPager pager;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyRidesFragment newInstance(int sectionNumber) {
        MyRidesFragment fragment = new MyRidesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MyRidesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myrides, container, false);
        ButterKnife.inject(this, rootView);
        MyRidesAdapter adapter = new MyRidesAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        tabs.setViewPager(pager);
        changeActionBarColor(getResources().getColor(R.color.blue4));
        return rootView;
    }

    public class MyRidesAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = {"Created", "Joined", "Past"};
        private MyRidesCreatedFragment myRidesCreatedFragment;
        private MyRidesPastFragment myRidesPastFragment;
        private MyRidesJoinedFragment myRidesJoinedFragment;

        public MyRidesAdapter(FragmentManager fm) {
            super(fm);
            myRidesCreatedFragment = MyRidesCreatedFragment.newInstance();
            myRidesJoinedFragment = MyRidesJoinedFragment.newInstance();
            myRidesPastFragment = MyRidesPastFragment.newInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return myRidesCreatedFragment;
            else if (position == 1)
                return myRidesJoinedFragment;
            else
                return myRidesPastFragment;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
