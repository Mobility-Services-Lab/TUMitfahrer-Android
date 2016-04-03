package de.tum.mitfahr.services;

import android.content.Context;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.AcceptRequestEvent;
import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.DeleteRideRequestEvent;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.events.GetRideRequestsEvent;
import de.tum.mitfahr.events.GetRidesDateEvent;
import de.tum.mitfahr.events.GetRidesEvent;
import de.tum.mitfahr.events.GetRidesPageEvent;
import de.tum.mitfahr.events.GetUserRequestsEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.events.LeaveRideEvent;
import de.tum.mitfahr.events.MyRidesAsDriverEvent;
import de.tum.mitfahr.events.MyRidesAsPassengerEvent;
import de.tum.mitfahr.events.MyRidesAsRequesterEvent;
import de.tum.mitfahr.events.MyRidesPastEvent;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.events.RejectRequestEvent;
import de.tum.mitfahr.events.RemovePassengerEvent;
import de.tum.mitfahr.events.RespondToRequestEvent;
import de.tum.mitfahr.events.UpdateRideEvent;
import de.tum.mitfahr.networking.clients.RidesRESTClient;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import de.tum.mitfahr.networking.models.response.RequestsResponse;
import de.tum.mitfahr.networking.models.response.RideResponse;
import de.tum.mitfahr.networking.models.response.RidesResponse;
import de.tum.mitfahr.util.LocationUtil;
import retrofit.client.Response;

/**
 * Created by amr on 18/05/14.
 */
public class RidesService extends AbstractService {

    private static final String TAG = RidesService.class.getSimpleName();
    private RidesRESTClient mRidesRESTClient;
    private Bus mBus;
    private String userAPIKey;
    private int userId;

    public RidesService(final Context context) {
        super(context);
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mRidesRESTClient = new RidesRESTClient(baseBackendURL,context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }

    public void offerRide(String departure, String destination, double departureLat, double departureLong, double destinationLat, double destinationLong, String meetingPoint, int freeSeats, String dateTime, int rideType, boolean isDriving, String car, List<String> repeatDates) {
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.offerRide(departure, destination, meetingPoint,
                departureLat, departureLong, destinationLat, destinationLong,
                freeSeats, dateTime, userAPIKey, rideType, userId, isDriving, car, repeatDates);
        TUMitfahrApplication.getInstance().trackEvent("Ride", "Ride Offered", "CreateRide");
    }

    @Subscribe
    public void onOfferRidesResult(OfferRideEvent result) {
        if (result.getType() == OfferRideEvent.Type.RESULT) {
            OfferRideResponse response = result.getResponse();
            if (null == response.getRides() && null == response.getRide()) {
                mBus.post(new OfferRideEvent(OfferRideEvent.Type.OFFER_RIDE_FAILED, response));
            } else {
                mBus.post(new OfferRideEvent(OfferRideEvent.Type.RIDE_ADDED, response.getRide()));
            }

      }
    }



    public void getRide(int rideId) {
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.getRide(userAPIKey, rideId);
    }

    @Subscribe
    public void onGetRideResult(GetRideEvent result) {
        if (result.getType() == GetRideEvent.Type.RESULT) {
            RideResponse response = result.getResponse();
            if (null == response.getRide()) {
                mBus.post(new GetRideEvent(GetRideEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRideEvent(GetRideEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public Ride getRideSynchronous(int rideId) {
        userAPIKey = mSharedPreferences.getString("api_key", null);
        return mRidesRESTClient.getRideSynchronous(userAPIKey, rideId);
    }

    public Ride getDeletedRideSynchronous(int rideId) {
        userAPIKey = mSharedPreferences.getString("api_key", null);
        return mRidesRESTClient.getDeletedRideSynchronous(userAPIKey, rideId);
    }


    public void updateRide(Ride updatedRide) {
        mRidesRESTClient.updateRide(userAPIKey, userId, updatedRide);
    }

    @Subscribe
    public void onUpdateRideResult(UpdateRideEvent result) {
        if (result.getType() == UpdateRideEvent.Type.RESULT) {
            RideResponse response = result.getResponse();
            if (null == response.getRide()) {
                mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.UPDATE_FAILED, response));
            } else {
                mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.RIDE_UPDATED, response));
            }
        }
    }

    public void getMyRidesAsDriver() {
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.getMyRidesAsDriver(userId, userAPIKey);
    }

    @Subscribe
    public void onGetMyRidesAsDriverResult(MyRidesAsDriverEvent result) {
        if (result.getType() == MyRidesAsDriverEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new MyRidesAsDriverEvent(MyRidesAsDriverEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new MyRidesAsDriverEvent(MyRidesAsDriverEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getMyRidesAsRequester() {
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.getMyRidesAsRequester(userId, userAPIKey);
    }

    @Subscribe
    public void onGetMyRidesAsRequesterResult(MyRidesAsRequesterEvent result) {
        if (result.getType() == MyRidesAsRequesterEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new MyRidesAsRequesterEvent(MyRidesAsRequesterEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new MyRidesAsRequesterEvent(MyRidesAsRequesterEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getMyRidesAsPassenger() {
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.getMyRidesAsPassenger(userId, userAPIKey);
    }

    @Subscribe
    public void onGetMyRidesAsPassengerResult(MyRidesAsPassengerEvent result) {
        if (result.getType() == MyRidesAsPassengerEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new MyRidesAsPassengerEvent(MyRidesAsPassengerEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new MyRidesAsPassengerEvent(MyRidesAsPassengerEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getMyRidesPast() {
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.getMyRidesPast(userId, userAPIKey);
    }

    @Subscribe
    public void onGetMyRidesPastResult(MyRidesPastEvent result) {
        if (result.getType() == MyRidesPastEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new MyRidesPastEvent(MyRidesPastEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new MyRidesPastEvent(MyRidesPastEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getPage(int pageNo) {
        mRidesRESTClient.getPage(userAPIKey, pageNo);
    }

    @Subscribe
    public void onGetPageResult(GetRidesPageEvent result) {
        if (result.getType() == GetRidesPageEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getRides(String fromDate, int rideType) {
        mRidesRESTClient.getRides(userAPIKey, fromDate, rideType);
    }

    @Subscribe
    public void onGetDateResult(GetRidesDateEvent result) {
        if (result.getType() == GetRidesDateEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new GetRidesDateEvent(GetRidesDateEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRidesDateEvent(GetRidesDateEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getRidesPaged(int rideType, int page) {
        mRidesRESTClient.getRidesPaged(userAPIKey, rideType, page);
    }

    @Subscribe
    public void onGetRidesPagedResult(GetRidesPageEvent result) {
        if (result.getType() == GetRidesPageEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getAllRides(int rideType) {
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.getAllRides(userAPIKey, rideType);
    }

    @Subscribe
    public void onGetRidesResult(GetRidesEvent result) {
        if (result.getType() == GetRidesEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new GetRidesEvent(GetRidesEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRidesEvent(GetRidesEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void addPassenger(int rideId, int addedPassengerId) {
        userId = mSharedPreferences.getInt("id", 0);
        //if(userId==0) Log.e(TAG, "accept, userID: "+userId);
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.addPassenger(userAPIKey, userId, rideId, addedPassengerId);
    }

    @Subscribe
    public void onAddPassengerResult(AcceptRequestEvent result) {
        if (result.getType() == AcceptRequestEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_SENT, retrofitResponse));
            } else {
                mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_FAILED, retrofitResponse));
            }
        }
    }

    public void leaveRide(int rideId) {
        userId = mSharedPreferences.getInt("id", 0);
        //if(userId==0) Log.e(TAG, "accept, userID: "+userId);
        userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.leaveRide(userAPIKey, userId, rideId);
    }


    @Subscribe
    public void onLeaveRideResult(LeaveRideEvent result) {
        if (result.getType() == LeaveRideEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new LeaveRideEvent(LeaveRideEvent.Type.SUCCESSFUL, retrofitResponse));
            } else {
                mBus.post(new LeaveRideEvent(LeaveRideEvent.Type.FAILED, retrofitResponse));
            }
        }
    }

    public void removePassenger(int rideId, int removedPassengerId) {
        mRidesRESTClient.removePassenger(userAPIKey, userId, rideId, removedPassengerId);
    }

    @Subscribe
    public void onRemovePassengerResult(RemovePassengerEvent result) {
        if (result.getType() == RemovePassengerEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new RemovePassengerEvent(RemovePassengerEvent.Type.SUCCESSFUL, retrofitResponse));
            } else {
                mBus.post(new RemovePassengerEvent(RemovePassengerEvent.Type.FAILED, retrofitResponse));
            }
        }
    }

    public void deleteRide(int rideId) {
        mRidesRESTClient.deleteRide(userAPIKey, userId, rideId);
    }

    @Subscribe
    public void onDeleteResult(DeleteRideEvent result) {
        if (result.getType() == DeleteRideEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            //Log.i(TAG, retrofitResponse.toString());
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.DELETE_SUCCESSFUL, result.getResponse(), retrofitResponse));
            } else {
                mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.DELETE_FAILED, result.getResponse(), retrofitResponse));
            }
        }
    }

    public void joinRequest(int rideId) {
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
        //if(userId==0) Log.e(TAG, "join, userID: "+userId);
        mRidesRESTClient.joinRequest(rideId, userId, userAPIKey);
    }

    @Subscribe
    public void onRideRequestResult(JoinRequestEvent result) {
        if (result.getType() == JoinRequestEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();

            if (201 == retrofitResponse.getStatus() || 200 == retrofitResponse.getStatus()) {
                mBus.post(new JoinRequestEvent(JoinRequestEvent.Type.REQUEST_SENT,
                        result.getJoinRequestResponse(), result.getRetrofitResponse()));
            } else {
                mBus.post(new JoinRequestEvent(JoinRequestEvent.Type.REQUEST_FAILED,
                        result.getJoinRequestResponse(), result.getRetrofitResponse()));
            }
        }

    }

    public void acceptRequest(int rideId, int requestId, int passengerId) {

        userId = mSharedPreferences.getInt("id", 0);
        //if(userId==0) Log.e(TAG, "accept, userID: "+userId);
        mRidesRESTClient.acceptRequest(rideId, passengerId, requestId, userAPIKey);

    }

    @Subscribe
    public void onAcceptRequestResult(AcceptRequestEvent result) {
        //Log.e("RidesService", "AcceptRequest");
        if (result.getType() == AcceptRequestEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            //Log.e("RidesService", "AcceptRequestCode:" + retrofitResponse.getStatus());
            if (retrofitResponse.getStatus() == 200) {
                //Log.e("RidesService", "AcceptRequest:SENT:" + retrofitResponse.getStatus());
                mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_SENT, retrofitResponse));
            } else {
                //Log.e("RidesService", "AcceptRequest:FALIED:" + retrofitResponse.getStatus());
                mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_FAILED, retrofitResponse));
            }
        }
    }


    public void rejectRequest(int rideId, int requestId, int passengerId) {
        mRidesRESTClient.rejectRequest(rideId, passengerId, requestId, userAPIKey);
    }

    @Subscribe
    public void onRejectRequestResult(RejectRequestEvent result) {
        //Log.e("RidesService", "RejectRequest");
        if (result.getType() == RejectRequestEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            //Log.e("RidesService", "RejectRequestEventCode:" + retrofitResponse.getStatus());
            if (retrofitResponse.getStatus() == 200) {
                //Log.e("RidesService", "RejectRequestEvent:SENT:" + retrofitResponse.getStatus());
                mBus.post(new RejectRequestEvent(RejectRequestEvent.Type.REJECT_SENT, retrofitResponse));
            } else {
                //Log.e("RidesService", "RejectRequestEvent:FALIED:" + retrofitResponse.getStatus());
                mBus.post(new RejectRequestEvent(RejectRequestEvent.Type.REJECT_FAILED, retrofitResponse));
            }
        }
    }

    @Subscribe
    public void onRespondToRequestResult(RespondToRequestEvent result) {
        //Log.e("RidesService", "ResponseToRequst");
        if (result.getType() == RespondToRequestEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            //Log.e("RidesService", "Code:" + retrofitResponse.getStatus());
            if (retrofitResponse.getStatus() == 200) {
                //Log.e("RidesService", "SENT:" + retrofitResponse.getStatus());
                mBus.post(new RespondToRequestEvent(RespondToRequestEvent.Type.RESPOND_SENT, retrofitResponse));
            } else {
                //Log.e("RidesService", "FALIED:" + retrofitResponse.getStatus());
                mBus.post(new RespondToRequestEvent(RespondToRequestEvent.Type.RESPOND_FAILED, retrofitResponse));
            }
        }
    }

    public void getRideRequests(int rideId) {
        mRidesRESTClient.getRideRequests(userAPIKey, rideId);
    }

    @Subscribe
    public void onGetRideRequestsResult(GetRideRequestsEvent result) {
        if (result.getType() == GetRideRequestsEvent.Type.RESULT) {
            RequestsResponse requestsResponse = result.getResponse();
            if (null == requestsResponse.getRequests()) {
                mBus.post(new GetRideRequestsEvent(GetRideRequestsEvent.Type.GET_FAILED, requestsResponse));
            } else {
                mBus.post(new GetRideRequestsEvent(GetRideRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
            }
        }
    }

    public void getUserRequests() {
        mRidesRESTClient.getUserRequests(userAPIKey, userId);
    }

    @Subscribe
    public void onGetUserRequestsResult(GetUserRequestsEvent result) {
        if (result.getType() == GetUserRequestsEvent.Type.RESULT) {
            RequestsResponse requestsResponse = result.getResponse();
            if (null == requestsResponse.getRequests()) {
                mBus.post(new GetUserRequestsEvent(GetUserRequestsEvent.Type.GET_FAILED, requestsResponse));
            } else {
                mBus.post(new GetUserRequestsEvent(GetUserRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
            }
        }
    }

    public void deleteRideRequest(int rideId, int requestId) {
        mRidesRESTClient.deleteRideRequest(userAPIKey, rideId, requestId);
    }

    @Subscribe
    public void onDeleteRideRequest(DeleteRideRequestEvent result) {
        if (result.getType() == DeleteRideRequestEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new DeleteRideRequestEvent(DeleteRideRequestEvent.Type.SUCCESSFUL, retrofitResponse));
            } else {
                mBus.post(new DeleteRideRequestEvent(DeleteRideRequestEvent.Type.FAILED, retrofitResponse));
            }
        }
    }
}
