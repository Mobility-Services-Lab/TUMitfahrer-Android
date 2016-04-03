package de.tum.mitfahr.util;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Authored by abhijith on 20/06/14.
 * Reference: http://cyrilmottier.com/2013/01/09/back-to-top-android-vs-ios/
 * https://github.com/flavienlaurent/poppyview
 */
public class QuickReturnViewHelper {

    public enum ViewPosition {
        TOP, BOTTOM
    }

    private static final int SCROLL_TO_TOP = -1;

    private static final int SCROLL_TO_BOTTOM = 1;

    private static final int SCROLL_DIRECTION_CHANGE_THRESHOLD = 5;

    private Activity mActivity;

    private LayoutInflater mLayoutInflater;

    private View mQuickReturnView;

    private int mScrollDirection = 0;

    private int mQuickReturnViewHeight = -1;

    private ViewPosition mViewPosition;

    public QuickReturnViewHelper(Activity activity, ViewPosition position, LayoutInflater inflater) {
        mActivity = activity;
        mLayoutInflater = inflater;
        mViewPosition = position;
    }

    public QuickReturnViewHelper(Activity activity, ViewPosition position) {
        mActivity = activity;
        mLayoutInflater = LayoutInflater.from(activity);
        mViewPosition = position;
    }

    public QuickReturnViewHelper(Activity activity) {
        this(activity, ViewPosition.BOTTOM);
    }

    public View createQuickReturnViewOnListView(
            ListView listView,
            int quickReturnViewResId,
            OnScrollListener onScrollListener) {

        if (listView.getHeaderViewsCount() != 0) {
            throw new IllegalArgumentException("use createQuickReturnViewOnListView with headerResId parameter");
        }
        if (listView.getFooterViewsCount() != 0) {
            throw new IllegalArgumentException("Doesn't support ListView with footer");
        }
        mQuickReturnView = mLayoutInflater.inflate(quickReturnViewResId, null);
        initQuickReturnViewOnListView(listView, onScrollListener);
        return mQuickReturnView;
    }

    public View createQuickReturnViewOnListView(ListView listView, int quickReturnViewResId) {
        return createQuickReturnViewOnListView(listView, quickReturnViewResId, null);
    }


    private void initQuickReturnViewOnListView(ListView listView, final OnScrollListener onScrollListener) {
        setPoppyViewOnView(listView);
        listView.setOnScrollListener(new OnScrollListener() {
            int mScrollPosition;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (onScrollListener != null) {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
                View topChild = view.getChildAt(0);

                int newScrollPosition = 0;
                if (topChild == null) {
                    newScrollPosition = 0;
                } else {
                    newScrollPosition = -topChild.getTop() + view.getFirstVisiblePosition() * topChild.getHeight();
                }

                if (Math.abs(newScrollPosition - mScrollPosition) >= SCROLL_DIRECTION_CHANGE_THRESHOLD) {
                    onScrollPositionChanged(mScrollPosition, newScrollPosition);
                }

                mScrollPosition = newScrollPosition;
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(view, scrollState);
                }
            }
        });
    }

    private void setPoppyViewOnView(View view) {
        LayoutParams lp = view.getLayoutParams();
        ViewParent parent = view.getParent();
        ViewGroup group = (ViewGroup) parent;
        int index = group.indexOfChild(view);
        final FrameLayout newContainer = new FrameLayout(mActivity);
        group.removeView(view);
        group.addView(newContainer, index, lp);
        newContainer.addView(view);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = mViewPosition == ViewPosition.BOTTOM ? Gravity.BOTTOM : Gravity.TOP;
        newContainer.addView(mQuickReturnView, layoutParams);
        group.invalidate();
    }

    private void onScrollPositionChanged(int oldScrollPosition, int newScrollPosition) {
        int newScrollDirection;
        //System.out.println(oldScrollPosition + " ->" + newScrollPosition);
        if(newScrollPosition < 2){
            newScrollDirection = SCROLL_TO_TOP;
        }else if (newScrollPosition < oldScrollPosition) {
            newScrollDirection = SCROLL_TO_TOP;
        } else {
            newScrollDirection = SCROLL_TO_BOTTOM;
        }
        if (newScrollDirection != mScrollDirection) {
            mScrollDirection = newScrollDirection;
            translateYPoppyView();
        }
    }

    private void translateYPoppyView() {
        mQuickReturnView.post(new Runnable() {

            @Override
            public void run() {
                if (mQuickReturnViewHeight <= 0) {
                    mQuickReturnViewHeight = mQuickReturnView.getHeight();
                }

                int translationY = 0;
                switch (mViewPosition) {
                    case BOTTOM:
                        translationY = mScrollDirection == SCROLL_TO_TOP ? 0 : mQuickReturnViewHeight;
                        break;
                    case TOP:
                        translationY = mScrollDirection == SCROLL_TO_BOTTOM ? -mQuickReturnViewHeight : 0;
                        break;
                }
                ViewPropertyAnimator.animate(mQuickReturnView).setDuration(200).translationY(translationY);
            }
        });
    }
}
