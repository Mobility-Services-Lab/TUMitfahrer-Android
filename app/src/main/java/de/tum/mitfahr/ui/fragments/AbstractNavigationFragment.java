package de.tum.mitfahr.ui.fragments;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.util.ActionBarColorChangeListener;

/**
 * Authored by abhijith on 22/05/14.
 */
public abstract class AbstractNavigationFragment extends Fragment {

    protected static final String ARG_SECTION_NUMBER = "section_number";
    protected ActionBarColorChangeListener mListener;
    private Handler mHandler = new Handler();
    private Drawable mActionBarDrawable = null;
    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            ((MainActivity) getActivity()).getToolbar().setBackgroundDrawable(who);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ActionBarColorChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ActionBarColorChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void changeActionBarColor(int newColor) {
        Drawable newColorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(getResources().getColor(R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{newColorDrawable, bottomDrawable});

        if (mActionBarDrawable == null) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ld.setCallback(drawableCallback);
            } else {
                ((MainActivity) getActivity()).getToolbar().setBackground(ld);
                ((MainActivity) getActivity()).getSupportActionBar().invalidateOptionsMenu();

            }
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{mActionBarDrawable, ld});
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                td.setCallback(drawableCallback);
            } else {
                ((MainActivity) getActivity()).getToolbar().setBackground(td);
                ((MainActivity) getActivity()).getSupportActionBar().invalidateOptionsMenu();

            }
            td.startTransition(200);

        }
        mActionBarDrawable = ld;

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

}
