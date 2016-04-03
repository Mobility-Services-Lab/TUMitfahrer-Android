package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.AcceptRequestEvent;
import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.DeleteRideRequestEvent;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.events.LeaveRideEvent;
import de.tum.mitfahr.events.RejectRequestEvent;
import de.tum.mitfahr.events.RemovePassengerEvent;
import de.tum.mitfahr.events.RespondToRequestEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.RideRequest;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.networking.panoramio.PanoramioPhoto;
import de.tum.mitfahr.util.RoundTransform;
import de.tum.mitfahr.util.StringHelper;
import de.tum.mitfahr.widget.NotifyingScrollView;
import de.tum.mitfahr.widget.PassengerItemView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;

public class RideDetailsFragment extends Fragment {

    private static final String RIDE = "ride";
    private static final int IMAGE_COLOR_FILTER = 0x5F000000;

    @InjectView(R.id.notifyingScrollView)
    NotifyingScrollView notifyingScrollView;

    @InjectView(R.id.details_driver_image_view)
    CircularImageView ownerImageView;

    @InjectView(R.id.details_from_text)
    TextView fromTextView;

    @InjectView(R.id.details_to_text)
    TextView toTextView;

    @InjectView(R.id.details_info)
    TextView infoTextView;

    @InjectView(R.id.ride_location_image)
    ImageView rideLocationImage;

    @InjectView(R.id.details_car)
    TextView carTextView;

    @InjectView(R.id.details_seats)
    TextView seatsTextView;

    @InjectView(R.id.details_ride_owner_name)
    TextView driverNameTextView;

    @InjectView(R.id.details_car_container)
    View carContainer;

    @InjectView(R.id.details_seats_container)
    View seatsContainer;

    @InjectView(R.id.ride_owner_layout_container)
    View rideOwnerLayoutContainer;

    @InjectView(R.id.passengers_layout_container)
    View passengersLayoutContainer;

    @InjectView(R.id.requests_layout_container)
    View requestsLayoutContainer;

    @InjectView(R.id.passengers_item_container)
    LinearLayout passengersItemContainer;

    @InjectView(R.id.requests_item_container)
    LinearLayout requestsItemContainer;


    @InjectView(R.id.details_action_button)
    CircularProgressButton rideActionButton;

    @InjectView(R.id.details_progress_bar)
    SmoothProgressBar progressBar;

    @InjectView(R.id.details_date_text)
    TextView dateText;

    @InjectView(R.id.details_time_text)
    TextView timeText;

    @InjectView(R.id.campus_activity)
    TextView campusActivityImage;

    private ProgressDialog mProgressDialog;

    private Ride mRide;
    private User mCurrentUser;
    private List<RideRequest> mRideRequests = new ArrayList<>();
    private Map<Integer, User> mRequestUserMap = new HashMap<>();
    private Geocoder mGeocoder;
    private int rideOwnerId;
    private ArrayList<User> actualPassengers = new ArrayList<>();


    private Drawable mActionBarBackgroundDrawable;

    private TUMitfahrApplication mApp;
    private Context appContext;

    private Handler mActionButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            rideActionButton.setProgress(0);
            rideActionButton.setClickable(true);
            TUMitfahrApplication.getApplication(appContext).getRidesService().getRide(mRide.getId());
        }
    };
    private int mPendingRequestId;
    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            ((ActionBarActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };
    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = rideLocationImage.getHeight() - ((ActionBarActivity) getActivity()).getSupportActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };


    public RideDetailsFragment() {
        // Required empty public constructor
    }

    public static RideDetailsFragment newInstance(Ride ride) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(RIDE, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRide = (Ride) getArguments().getSerializable(RIDE);
        }
        mApp = TUMitfahrApplication.getApplication(getActivity());
        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        setRetainInstance(true);
        appContext = getActivity();
        rideOwnerId = mRide.getRideOwner().getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_detail, container, false);
        ButterKnife.inject(this, view);
        mActionBarBackgroundDrawable = getResources().getDrawable(R.color.blue2);
        mActionBarBackgroundDrawable.setAlpha(0);
        rideLocationImage.setColorFilter(IMAGE_COLOR_FILTER);

        ((ActionBarActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        notifyingScrollView.setOnScrollChangedListener(mOnScrollChangedListener);

        rideActionButton.setIndeterminateProgressMode(true);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getRide(mRide.getId());
        progressBar.progressiveStart();
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
        removeRideOwnerFromPassengers();
        showData();
    }

    private void removeRideOwnerFromPassengers() {
        actualPassengers.clear();
        for (final User passenger : mRide.getPassengers()) {
            if (rideOwnerId != passenger.getId()) {
                actualPassengers.add(passenger);
            }
        }
    }

    private void removeActionForPastRide() {
        if ((rideOwnerId != mCurrentUser.getId()) && isPastRide()) {
            rideActionButton.setVisibility(View.GONE);
        } else rideActionButton.setVisibility(View.VISIBLE);
    }

    private boolean isPastRide() {
        Date currentDate = new Date();
        SimpleDateFormat outputFormat = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
        Date date1;
        try {
            date1 = outputFormat.parse(mRide.getDepartureTime());

            return date1.before(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Subscribe
    public void onGetRide(GetRideEvent result) {
        progressBar.progressiveStop();
        if (result.getType() == GetRideEvent.Type.GET_SUCCESSFUL) {
            mRide = result.getResponse().getRide();
            removeRideOwnerFromPassengers();
            showData();
        } else if (result.getType() == GetRideEvent.Type.GET_FAILED) {
            getActivity().finish();
        }
    }

    @Subscribe
    public void onDeleteResult(DeleteRideEvent result) {
        if (result.getType() == DeleteRideEvent.Type.DELETE_SUCCESSFUL) {
            rideActionButton.setProgress(100);
            rideActionButton.setText(getString(R.string.rideDetails_deleteSuccess));
            ((RideDetailsActivity) getActivity()).setNeedsTimelineRefresh(true);
        } else if (result.getType() == DeleteRideEvent.Type.DELETE_FAILED) {
            rideActionButton.setProgress(-1);
            rideActionButton.setText(getString(R.string.rideDetails_deleteFailed));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    getActivity().finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Subscribe
    public void onRideRequestResult(JoinRequestEvent result) {
        if (result.getType() == JoinRequestEvent.Type.REQUEST_SENT) {
            ((RideDetailsActivity) getActivity()).setNeedsTimelineRefresh(true);
            rideActionButton.setProgress(100);
            Toast.makeText(getActivity(), getString(R.string.rideRequest_sentSuccess), Toast.LENGTH_SHORT).show();

        } else if (result.getType() == JoinRequestEvent.Type.REQUEST_FAILED) {
            rideActionButton.setProgress(-1);
            Toast.makeText(getActivity(), getString(R.string.rideRequest_sentFail), Toast.LENGTH_SHORT).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onRemovePassengerResult(RemovePassengerEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == RemovePassengerEvent.Type.SUCCESSFUL) {
            ((RideDetailsActivity) getActivity()).setNeedsTimelineRefresh(true);
            Toast.makeText(getActivity(), getString(R.string.removePassenger_successful), Toast.LENGTH_SHORT).show();
        } else if (result.getType() == RemovePassengerEvent.Type.FAILED) {
            Toast.makeText(getActivity(), getString(R.string.removePassenger_notSuccessful), Toast.LENGTH_SHORT).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onLeaveRideResult(LeaveRideEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == LeaveRideEvent.Type.SUCCESSFUL) {
            ((RideDetailsActivity) getActivity()).setNeedsTimelineRefresh(true);
            Toast.makeText(getActivity(), getString(R.string.leaveRide_successful), Toast.LENGTH_SHORT).show();
        } else if (result.getType() == LeaveRideEvent.Type.FAILED) {
            Toast.makeText(getActivity(), getString(R.string.leaveRide_NotSuccessful), Toast.LENGTH_SHORT).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onRespondToRequestResult(RespondToRequestEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == RespondToRequestEvent.Type.RESPOND_SENT) {
            ((RideDetailsActivity) getActivity()).setNeedsTimelineRefresh(true);
            Toast.makeText(getActivity(), getString(R.string.sendResponse_successful), Toast.LENGTH_SHORT).show();

        } else if (result.getType() == RespondToRequestEvent.Type.RESPOND_FAILED) {
            Toast.makeText(getActivity(), getString(R.string.sendResponse_notSuccessful), Toast.LENGTH_SHORT).show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onAcceptRequestResult(AcceptRequestEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == AcceptRequestEvent.Type.ACCEPT_SENT) {
            ((RideDetailsActivity) getActivity()).setNeedsTimelineRefresh(true);
            Toast.makeText(getActivity(), getString(R.string.sendResponse_successful), Toast.LENGTH_SHORT).show();

        } else if (result.getType() == AcceptRequestEvent.Type.ACCEPT_FAILED) {
            Toast.makeText(getActivity(), getString(R.string.sendResponse_notSuccessful), Toast.LENGTH_SHORT).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onRejectRequestResult(RejectRequestEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == RejectRequestEvent.Type.REJECT_SENT) {
            ((RideDetailsActivity) getActivity()).setNeedsTimelineRefresh(true);
            Toast.makeText(getActivity(), getString(R.string.sendResponse_successful), Toast.LENGTH_SHORT).show();

        } else if (result.getType() == RejectRequestEvent.Type.REJECT_FAILED) {
            Toast.makeText(getActivity(), getString(R.string.sendResponse_notSuccessful), Toast.LENGTH_SHORT).show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onDeleteRideRequest(DeleteRideRequestEvent result) {
        if (result.getType() == DeleteRideRequestEvent.Type.RESULT) {
            ((RideDetailsActivity) getActivity()).setNeedsTimelineRefresh(true);
            rideActionButton.setProgress(100);
        } else {
            rideActionButton.setProgress(-1);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showData() {
        fromTextView.setText(mRide.getDeparturePlace());
        toTextView.setText(mRide.getDestination());
        infoTextView.setText(mRide.getMeetingPoint());
        String date = StringHelper.parseDate(mRide.getDepartureTime());
        String time = StringHelper.parseTime(mRide.getDepartureTime());
        dateText.setText(date);
        timeText.setText(time);
        if (mRide.getRideType() == 0) {
            campusActivityImage.setText(getResources().getString(R.string.ridetype_Campus));
            campusActivityImage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_school_white_24dp, 0, 0, 0);
        } else {
            campusActivityImage.setText(getResources().getString(R.string.ridetype_Activity));
            campusActivityImage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_activity, 0, 0, 0);
        }

        if (!StringHelper.isBlank(mRide.getCar())) {
            carTextView.setText(mRide.getCar());
        } else {
            carContainer.setVisibility(View.GONE);
        }
        if (mRide.isRideRequest()) {
            seatsContainer.setVisibility(View.GONE);
        } else {
            seatsTextView.setText(Integer.toString(mRide.getFreeSeats()));
        }

        if (mRide.getRideImageUrl() != null) {
            Picasso.with(getActivity())
                    .load(mRide.getRideImageUrl())
                    .placeholder(R.drawable.list_image_placeholder)
                    .error(R.drawable.list_image_placeholder)
                    .into(rideLocationImage);
        } else {
            new PanoramioTask().execute(mRide);
        }

        if ((null != mRide.getRideOwner())) {
            String profileUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImageURL(mRide.getRideOwner().getId());
            Picasso.with(getActivity())
                    .load(profileUrl)
                    .skipMemoryCache()
                    .placeholder(R.drawable.list_image_placeholder)
                    .error(R.drawable.list_image_placeholder)
                    .fit().centerCrop()
                    .transform(new RoundTransform())
                    .into(ownerImageView);
            setActionButtonState(ActionButtonState.LEAVE_RIDE);

            rideOwnerLayoutContainer.setVisibility(View.VISIBLE);
            requestsLayoutContainer.setVisibility(View.GONE);
            requestsItemContainer.removeAllViews();

            passengersLayoutContainer.setVisibility(View.GONE);
            passengersItemContainer.removeAllViews();

            driverNameTextView.setText(mRide.getRideOwner()
                    .getFirstName() + " " + mRide.getRideOwner().getLastName());


            ownerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                    intent.putExtra(UserDetailsActivity.USER_INTENT_EXTRA, mRide.getRideOwner());
                    startActivity(intent);

                }
            });


            if (mCurrentUser.getId() == mRide.getRideOwner().getId() && !mRide.isRideRequest())
                showDataAsOwnerDriver();
            else if (mCurrentUser.getId() == mRide.getRideOwner().getId())
                showDataAsOwnerRequest();
            else if (isUserPassenger())
                showDataAsPassengerAccepted();
            else if (isUserRequestPending())
                showDataAsPassengerPending();
            else if (mRide.isRideRequest())
                showDataAsRideRequest();
            else
                showDataAsPassenger();
        }

        removeActionForPastRide();

    }

    private void addAllPassengersIfAny(ArrayList<User> passengers, final boolean isRideOwner) {

        if (passengers.size() > 0) {

            passengersLayoutContainer.setVisibility(View.VISIBLE);
            passengersItemContainer.removeAllViews();
            for (final User passenger : passengers) {
                PassengerItemView passengerItem = new PassengerItemView(getActivity());
                passengerItem.setPassenger(passenger);
                if (isRideOwner) passengerItem.setItemType(PassengerItemView.TYPE_PASSENGER);
                else passengerItem.setItemType(PassengerItemView.TYPE_NONE);
                passengerItem.setListener(new PassengerItemView.PassengerItemClickListener() {
                    @Override
                    public void onRemoveClicked(User passenger) {
                        if (isRideOwner) {
                            mApp.getRidesService().removePassenger(mRide.getId(), passenger.getId());
                            mProgressDialog.show();
                        }
                    }

                    @Override
                    public void onActionClicked(User passenger) {
                        //no op
                    }

                    @Override
                    public void onUserClicked(User passenger) {
                        //show the user page
                        Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                        intent.putExtra(UserDetailsActivity.USER_INTENT_EXTRA, passenger);
                        startActivity(intent);
                    }
                });

                passengersItemContainer.addView(passengerItem);
            }
        }

    }

    private boolean isUserRequestPending() {
        if (mRide.getRequests().length == 0)
            return false;
        mRideRequests = Arrays.asList(mRide.getRequests());
        for (RideRequest request : mRideRequests) {
            if (request.getPassengerId() == mCurrentUser.getId()) {
                mPendingRequestId = request.getId();
                return true;
            }
        }
        return false;
    }

    private boolean isUserPassenger() {
        if (actualPassengers.size() > 0) {
            for (User user : actualPassengers) {
                if (user.getId() == mCurrentUser.getId())
                    return true;
            }
            return false;
        }
        return false;
    }

    private void showDataAsPassenger() {
        setActionButtonState(ActionButtonState.REQUEST_RIDE);
        showPassengers(false);
        showRequests(false);

    }

    private void showDataAsRideRequest() {
        setActionButtonState(ActionButtonState.OFFER_RIDE);
    }

    private void showDataAsPassengerPending() {
        setActionButtonState(ActionButtonState.PENDING_RIDE);

        showPassengers(false);
        showRequests(false);
    }

    private void showDataAsPassengerAccepted() {
        showPassengers(false);
        showRequests(false);
    }

    private void showDataAsOwnerRequest() {
        String profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getStoredAvatarURL();
        if (StringHelper.isBlank(profileImageUrl))
            profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImage();

        Picasso.with(getActivity())
                .load(profileImageUrl)
                .placeholder(R.drawable.list_image_placeholder)
                .error(R.drawable.list_image_placeholder)
                .fit().centerCrop()
                .transform(new RoundTransform())
                .into(ownerImageView);
        setActionButtonState(ActionButtonState.DELETE_RIDE);
    }

    private void showDataAsOwnerDriver() {
        String profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getStoredAvatarURL();
        if (StringHelper.isBlank(profileImageUrl))
            profileImageUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImage();

        Picasso.with(getActivity())
                .load(profileImageUrl)
                .placeholder(R.drawable.list_image_placeholder)
                .error(R.drawable.list_image_placeholder)
                .fit().centerCrop()
                .transform(new RoundTransform())
                .into(ownerImageView);
        setActionButtonState(ActionButtonState.DELETE_RIDE);

        showPassengers(true);
        showRequests(true);
    }

    private void showRequests(boolean isUserTheOwner) {
        if (mRide.getRequests() != null && mRide.getRequests().length > 0) {
            mRideRequests = Arrays.asList(mRide.getRequests());
            new GetUserFromRequestsTask(getActivity(), isUserTheOwner).execute(mRideRequests);
        }
    }

    private void showPassengers(boolean isUserTheOwner) {
        if (mRide.getPassengers() != null && mRide.getPassengers().length > 0) {
            new GetUserTask(getActivity(), isUserTheOwner).execute(actualPassengers);
        }
    }

    public void setActionButtonState(ActionButtonState state) {
        switch (state) {
            case REQUEST_RIDE:
                rideActionButton.setText(getString(R.string.requestJoinRide));
                rideActionButton.setIdleText(getString(R.string.requestJoinRide));
                rideActionButton.setErrorText(getString(R.string.requestJoinRide_notSuccessful));
                rideActionButton.setCompleteText(getString(R.string.requestJoinRide_successfull));
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rideActionButton.setProgress(50);
                        rideActionButton.setClickable(false);
                        mApp.getRidesService().joinRequest(mRide.getId());
                    }
                });
                break;
            case LEAVE_RIDE:
                rideActionButton.setText(getString(R.string.leaveRide));
                rideActionButton.setIdleText(getString(R.string.leaveRide));
                rideActionButton.setErrorText(getString(R.string.leaveRide_NotSuccessful));
                rideActionButton.setCompleteText(getString(R.string.leaveRide_successful));
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rideActionButton.setProgress(50);
                        rideActionButton.setClickable(false);
                        mApp.getRidesService().leaveRide(mRide.getId());
                    }
                });
                break;
            case PENDING_RIDE:
                rideActionButton.setText(getString(R.string.cancelJoinRequest));
                rideActionButton.setIdleText(getString(R.string.cancelJoinRequest));
                rideActionButton.setErrorText(getString(R.string.cancelJoinRequest_notSuccessful));
                rideActionButton.setCompleteText(getString(R.string.cancelJoinRequest_successful));
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(getString(R.string.cancelJoinRequest_question))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mPendingRequestId != 0)
                                            mApp.getRidesService().deleteRideRequest(mRide.getId(), mPendingRequestId);
                                    }
                                })
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        // Create the AlertDialog object and return it
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
                break;
            case DELETE_RIDE:
                rideActionButton.setText(getString(R.string.deleteRide));
                rideActionButton.setIdleText(getString(R.string.deleteRide));
                rideActionButton.setErrorText(getString(R.string.deleteRide_notSuccessful));
                rideActionButton.setCompleteText(getString(R.string.deleteRide_successful));
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rideActionButton.setProgress(50);
                        rideActionButton.setClickable(false);
                        mApp.getRidesService().deleteRide(mRide.getId());
                    }
                });
                break;
            case OFFER_RIDE:
                rideActionButton.setText(getString(R.string.offer_ride));
                rideActionButton.setIdleText(getString(R.string.offer_ride));
                rideActionButton.setErrorText(getString(R.string.offerRide_notSuccessful));
                rideActionButton.setCompleteText(getString(R.string.offerRide_successful));
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra(MainActivity.EXTRA_OFFER_RIDE, mRide);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                });
                break;

        }
        rideActionButton.setVisibility(View.VISIBLE);
        rideActionButton.setProgress(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private enum ActionButtonState {
        REQUEST_RIDE,
        LEAVE_RIDE,
        PENDING_RIDE,
        DELETE_RIDE,
        OFFER_RIDE
    }

    private class GetUserTask extends AsyncTask<List<User>, Void, Boolean> {

        Context localContext;
        boolean isOwnerCurrentUser;
        ArrayList<User> passengers;

        public GetUserTask(Context context, boolean ownerIsTheCurrentUser) {
            this.localContext = context;
            this.isOwnerCurrentUser = ownerIsTheCurrentUser;
            passengers = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.progressiveStart();
        }

        @Override
        protected Boolean doInBackground(List<User>... params) {
            List<User> users = params[0];
            passengers.clear();
            for (User user : users) {
                try {
                    User passenger = TUMitfahrApplication.getApplication(localContext)
                            .getProfileService().getUserSynchronous(user.getId());
                    if (null != passenger) {
                        passengers.add(passenger);
                    }
                } catch (RetrofitError e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            addAllPassengersIfAny(passengers, isOwnerCurrentUser);
        }
    }

    private class GetUserFromRequestsTask extends AsyncTask<List<RideRequest>, Void, Boolean> {

        Context localContext;
        boolean isOwnerCurrentUser;

        public GetUserFromRequestsTask(Context context, boolean ownerIsTheCurrentUser) {
            this.localContext = context;
            this.isOwnerCurrentUser = ownerIsTheCurrentUser;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.progressiveStart();
        }

        @Override
        protected Boolean doInBackground(List<RideRequest>... params) {
            List<RideRequest> rideRequests = params[0];
            for (RideRequest rideRequest : rideRequests) {
                try {
                    User passenger = TUMitfahrApplication.getApplication(localContext)
                            .getProfileService().getUserSynchronous(rideRequest.getPassengerId());
                    if (null != passenger) {
                        mRequestUserMap.put(rideRequest.getId(), passenger);
                    }
                } catch (RetrofitError e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            progressBar.progressiveStop();
            final TUMitfahrApplication app = TUMitfahrApplication.getApplication(localContext);


            Map<Integer, User> localRequestMap = mRequestUserMap;
            Iterator it = mRequestUserMap.entrySet().iterator();
            if (mRequestUserMap.size() > 0) {
                requestsLayoutContainer.setVisibility(View.VISIBLE);
                ArrayList<User> mPassengers = actualPassengers;
                requestsItemContainer.removeAllViews();
                while (it.hasNext()) {
                    final Map.Entry pairs = (Map.Entry) it.next();
                    PassengerItemView passengerItem = new PassengerItemView(localContext);
                    passengerItem.setPassenger((User) pairs.getValue());
                    final int requestId = (Integer) pairs.getKey();
                    if (isOwnerCurrentUser)
                        passengerItem.setItemType(PassengerItemView.TYPE_REQUEST);
                    else passengerItem.setItemType(PassengerItemView.TYPE_NONE);
                    passengerItem.setListener(new PassengerItemView.PassengerItemClickListener() {
                        @Override
                        public void onRemoveClicked(User passenger) {
                            //remove the user
                            app.getRidesService().rejectRequest(mRide.getId(), requestId, passenger.getId());
                            mProgressDialog.show();
                        }

                        @Override
                        public void onActionClicked(User passenger) {
                            //do the action... conversation or accept
                            app.getRidesService().acceptRequest(mRide.getId(), requestId, passenger.getId());
                            mProgressDialog.show();
                        }

                        @Override
                        public void onUserClicked(User passenger) {
                            //show the userpage
                            Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                            intent.putExtra(UserDetailsActivity.USER_INTENT_EXTRA, passenger);
                            startActivity(intent);

                        }
                    });
                    User pUser = passengerItem.getPassenger();
                    Iterator passIterator = mPassengers.iterator();
                    if (mPassengers.size() == 0) {
                        requestsItemContainer.addView(passengerItem);
                    } else {
                        while (passIterator.hasNext()) {
                            User user = (User) passIterator.next();
                            if (pUser.getId() != user.getId()) {
                                requestsItemContainer.addView(passengerItem);
                            }
                        }
                    }
                    Iterator reqIterator = localRequestMap.entrySet().iterator();

                    Integer count = 0;
                    while (reqIterator.hasNext()) {
                        final Map.Entry reqPairs = (Map.Entry) reqIterator.next();
                        User mUser = (User) reqPairs.getValue();
                        Iterator passengerIterator = mPassengers.iterator();
                        while (passengerIterator.hasNext()) {
                            User nUser = (User) passengerIterator.next();
                            if (mUser.getId() == (nUser.getId())) {
                                count++;
                            }
                        }
                    }
                    if (count.equals(mRequestUserMap.size())) {
                        requestsLayoutContainer.setVisibility(View.GONE);
                    }
                    it.remove();// avoids a ConcurrentModificationException
                }
            }
        }
    }

    private class PanoramioTask extends AsyncTask<Ride, Void, Ride> {

        public PanoramioTask() {
            super();
        }

        @Override
        protected Ride doInBackground(Ride... params) {

            Ride ride = params[0];
            if (!isCancelled()) {
                String locationName = ride.getDestination();
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
                    ride.setLatitude(address.getLatitude());
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
            return ride;
        }

        @Override
        protected void onPostExecute(Ride result) {
            Picasso.with(getActivity())
                    .load(mRide.getRideImageUrl())
                    .placeholder(R.drawable.list_image_placeholder)
                    .error(R.drawable.list_image_placeholder)
                    .into(rideLocationImage);
        }
    }
}