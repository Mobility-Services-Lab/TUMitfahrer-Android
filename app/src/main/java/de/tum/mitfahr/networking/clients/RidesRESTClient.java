package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

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
import de.tum.mitfahr.events.UpdateRideEvent;
import de.tum.mitfahr.networking.api.RidesAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.requests.AcceptRideRequest;
import de.tum.mitfahr.networking.models.requests.JoinRideReqest;
import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.response.AcceptRideResponse;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import de.tum.mitfahr.networking.models.response.RejectRideResponse;
import de.tum.mitfahr.networking.models.response.RequestsResponse;
import de.tum.mitfahr.networking.models.response.RideResponse;
import de.tum.mitfahr.networking.models.response.RidesResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 18/05/14.
 */
public class RidesRESTClient extends AbstractRESTClient {

    private static final String TAG = RidesRESTClient.class.getSimpleName();
    private RidesAPIService ridesAPIService;

    public RidesRESTClient(String mBaseBackendURL,Context context) {

        super(mBaseBackendURL,context);
        ridesAPIService = mRestAdapter.create(RidesAPIService.class);
    }

    public void offerRide(final String departure,
                          final String destination,
                          final String meetingPoint,
                          final double departureLatitude,
                          final double departureLongitude,
                          final double destinationLatitude,
                          final double destinationLongitude,
                          final int freeSeats,
                          final String dateTime,
                          final String userAPIKey,
                          final int rideType,
                          final int userId,
                          final boolean isDriving,
                          final String car,
                          final List<String> repeatDates) {
        OfferRideRequest requestData = new OfferRideRequest(userId, departure, destination, meetingPoint,departureLatitude, departureLongitude, destinationLatitude, destinationLongitude, freeSeats, dateTime, rideType, isDriving, car,repeatDates);
     //   ridesAPIService.getStatus(statusCallback);
        ridesAPIService.offerRide(userAPIKey, userId, requestData, offerRideCallback);
    }

//    private Callback<StatusResponse> statusCallback = new Callback<StatusResponse>() {
//        @Override
//        public void success(StatusResponse statusResponse, Response response) {
//            Log.e("ActivitiesRESTClient", "URL Active: " + statusResponse.getStatus() + "");
//        }
//
//        @Override
//        public void failure(RetrofitError error) {
//
//            Log.e("ActivitiesRESTClient", "URL not active: "+error.getResponse().getReason()+"");
//
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("oldAPI",true);
//            editor.commit();
//
//        }
//    };
    private Callback<OfferRideResponse> offerRideCallback = new Callback<OfferRideResponse>() {

        @Override
        public void success(OfferRideResponse offerRideResponse, Response response) {
            mBus.post(new OfferRideEvent(OfferRideEvent.Type.RESULT, offerRideResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            if(null == retrofitError.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,retrofitError));
            OfferRideResponse json = (OfferRideResponse) retrofitError.getBodyAs(OfferRideResponse.class);

            String[] errors = json.getErrors();
            mBus.post(new OfferRideEvent(OfferRideEvent.Type.OFFER_RIDE_FAILED, json));
        }
    };

    public void getRide(String userAPIKey, int rideId) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getRide(userAPIKey, rideId, getRideCallback);
    }

    private Callback<RideResponse> getRideCallback = new Callback<RideResponse>() {
        @Override
        public void success(RideResponse rideResponse, Response response) {
            mBus.post(new GetRideEvent(GetRideEvent.Type.RESULT, rideResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public Ride getRideSynchronous(String userAPIKey, int rideId) {
      //  ridesAPIService.getStatus(statusCallback);
        RideResponse response = ridesAPIService.getRideSynchronous(userAPIKey, rideId);
        if (null != response && null != response.getRide()) {
            return response.getRide();
        }
        return null;
    }

    public Ride getDeletedRideSynchronous(String userAPIKey, int rideId) {
        //ridesAPIService.getStatus(statusCallback);
        RideResponse response = ridesAPIService.getDeletedRideSynchronous(userAPIKey, rideId);
        if (null != response && null != response.getRide()) {
            return response.getRide();
        }
        return null;
    }

    public void updateRide(String userAPIKey, int userId, Ride updatedRide) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.updateRide(userAPIKey, userId, updatedRide.getId(), updatedRide, updateRideCallback);
    }

    private Callback<RideResponse> updateRideCallback = new Callback<RideResponse>() {
        @Override
        public void success(RideResponse rideResponse, Response response) {
            mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.RESULT, rideResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void getMyRidesAsDriver(final int userId, String userAPIKey) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getMyRidesAsDriver(userAPIKey, userId, true, false, getMyRidesAsDriverCallback);
    }

    private Callback<RidesResponse> getMyRidesAsDriverCallback = new Callback<RidesResponse>() {

        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new MyRidesAsDriverEvent(MyRidesAsDriverEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,retrofitError));
        }
    };

    public void getMyRidesAsRequester(final int userId, String userAPIKey) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getMyRidesAsRequester(userAPIKey, userId, false, false, getMyRidesAsRequesterCallback);
    }

    private Callback<RidesResponse> getMyRidesAsRequesterCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new MyRidesAsRequesterEvent(MyRidesAsRequesterEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void getMyRidesAsPassenger(final int userId, String userAPIKey) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getMyRidesAsPassenger(userAPIKey, userId, getMyRidesAsPassengerCallback);
    }

    private Callback<RidesResponse> getMyRidesAsPassengerCallback = new Callback<RidesResponse>() {

        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new MyRidesAsPassengerEvent(MyRidesAsPassengerEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void getMyRidesPast(final int userId, String userAPIKey) {
       // ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getMyRidesPast(userAPIKey, userId, true, getMyRidesPastCallback);
    }

    private Callback<RidesResponse> getMyRidesPastCallback = new Callback<RidesResponse>() {

        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new MyRidesPastEvent(MyRidesPastEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,retrofitError));
        }
    };

    public void getPage(String userAPIKey, int pageNo) {
       // ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getPage(userAPIKey, pageNo, getPageCallback);
    }

    private Callback<RidesResponse> getPageCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    // Doc not clear
    public void getRides(String userAPIKey, String fromDate, int rideType) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getRides(userAPIKey, fromDate, rideType, getRidesCallback);
    }

    private Callback<RidesResponse> getRidesCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesDateEvent(GetRidesDateEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void getRidesPaged(String userAPIKey, int rideType, int page) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getRidesPaged(userAPIKey, rideType, page, getRidePagedCallback);
    }

    private Callback<RidesResponse> getRidePagedCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void getAllRides(String userAPIKey, int rideType) {
       // ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getRides(userAPIKey, rideType, getAllRideCallback);
    }

    private Callback<RidesResponse> getAllRideCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesEvent(GetRidesEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void addPassenger(String userAPIKey, int userId, int rideId, int addedPassengerId) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.addPassenger(userAPIKey, userId, rideId, addedPassengerId, addPassengerCallback);
    }

    private Callback addPassengerCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            Log.i(TAG, "addPassengerCallback "+response.getStatus());
            Log.i(TAG, "addPassengerCallback "+response.getReason());
            mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
            mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.RESULT, null));
        }
    };

    public void removePassenger(String userAPIKey, int userId, int rideId, int removedPassengerId) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.removePassenger(userAPIKey, removedPassengerId, rideId, removePassengerCallback);
    }

    private Callback removePassengerCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new RemovePassengerEvent(RemovePassengerEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
            mBus.post(new RemovePassengerEvent(RemovePassengerEvent.Type.FAILED, null));
        }
    };


    public void deleteRide(final String userAPIKey, final int userId, final int rideId) {
        //ridesAPIService.deleteRide(userAPIKey, userId, rideId, deleteRideCallback);
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.deleteRide(userAPIKey, rideId, deleteRideCallback);
    }

    private Callback<DeleteRideResponse> deleteRideCallback = new Callback<DeleteRideResponse>() {

        @Override
        public void success(DeleteRideResponse deleteRideResponse, Response response) {
            mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.RESULT, deleteRideResponse, response));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            if(null==retrofitError.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,retrofitError));
            mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.DELETE_FAILED, null, null));

        }
    };

    public void joinRequest(int rideId, int passengerId, String userAPIKey) {
        JoinRideReqest joinRideReqest = new JoinRideReqest(passengerId);
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.joinRequest(userAPIKey, rideId, joinRideReqest, joinRequestCallback);
    }

    private Callback<JoinRequestResponse> joinRequestCallback = new Callback<JoinRequestResponse>() {
        @Override
        public void success(JoinRequestResponse joinRequestResponse, Response response) {
            mBus.post(new JoinRequestEvent(JoinRequestEvent.Type.RESULT, joinRequestResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
           JoinRequestEvent joinRequestEvent = new JoinRequestEvent(JoinRequestEvent.Type.REQUEST_FAILED, null, null);
           mBus.post(joinRequestEvent);
        }
    };

    public void acceptRequest(int rideId, int passengerId, int requestId, String userAPIKey) {
        AcceptRideRequest request = new AcceptRideRequest(passengerId, true);
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.acceptRideRequest(userAPIKey, rideId, request, acceptRequestCallback);
        //ridesAPIService.acceptRideRequest(userAPIKey, rideId, requestId, passengerId, true, acceptRequestCallback);
    }

    public void rejectRequest(int rideId, int passengerId, int requestId, String userAPIKey) {
        AcceptRideRequest request = new AcceptRideRequest(passengerId, false);
        //ridesAPIService.getStatus(statusCallback);
        ridesAPIService.rejectRideRequest(userAPIKey, rideId, request, rejectRequestCallback);
    }

    private Callback<RejectRideResponse> rejectRequestCallback = new Callback<RejectRideResponse>() {
        @Override
        public void success(RejectRideResponse o, Response response) {
            Log.e("RidesClient", "RejectCallback");
            Log.e("RidesService", "RejectCode:" + response.getStatus());

            if(response.getStatus() == 200) {
                mBus.post(new RejectRequestEvent(RejectRequestEvent.Type.REJECT_SENT, response));
            }else{
                mBus.post(new RejectRequestEvent(RejectRequestEvent.Type.REJECT_FAILED, response));
            }

            //mBus.post(new RejectRequestEvent(RejectRequestEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
            mBus.post(new RejectRequestEvent(RejectRequestEvent.Type.REJECT_FAILED, error.getResponse()));
        }
    };

    private Callback<AcceptRideResponse> acceptRequestCallback = new Callback<AcceptRideResponse>() {
        @Override
        public void success(AcceptRideResponse o, Response response) {
            Log.e("RidesClient", "AcceptCallback");
            Log.e("RidesService", "AcceptCode:" + response.getStatus());
            /*
            if(response.getStatus() == 200) {
                mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_SENT, response));
            }else{
                mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_FAILED, response));
            }
            */
            mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
            else mBus.post(new AcceptRequestEvent(AcceptRequestEvent.Type.ACCEPT_FAILED, error.getResponse()));
        }
    };

    public void getRideRequests(String userAPIKey, int rideId) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getRideRequests(userAPIKey, rideId, getRideRequestsCallback);
    }

    private Callback<RequestsResponse> getRideRequestsCallback = new Callback<RequestsResponse>() {
        @Override
        public void success(RequestsResponse requestsResponse, Response response) {
            mBus.post(new GetRideRequestsEvent(GetRideRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void getUserRequests(String userAPIKey, int userId) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.getUserRequests(userAPIKey, userId, getUserRequestsCallback);
    }

    private Callback<RequestsResponse> getUserRequestsCallback = new Callback<RequestsResponse>() {
        @Override
        public void success(RequestsResponse requestsResponse, Response response) {
                mBus.post(new GetUserRequestsEvent(GetUserRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void deleteRideRequest(String userAPIKey, int rideId, int requestId) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.deleteRideRequest(userAPIKey, rideId, requestId, deleteRideRequestCallback);
    }

    private Callback deleteRideRequestCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new DeleteRideRequestEvent(DeleteRideRequestEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
            else {
                mBus.post(new DeleteRideRequestEvent(DeleteRideRequestEvent.Type.RESULT, error.getResponse()));
            }
        }
    };

    public void leaveRide(String userAPIKey, int userId, int rideId) {
      //  ridesAPIService.getStatus(statusCallback);
        ridesAPIService.leaveRide(userAPIKey, userId, rideId, leaveRideCallback);
    }

    private Callback leaveRideCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new LeaveRideEvent(LeaveRideEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
            else {
                mBus.post(new LeaveRideEvent(LeaveRideEvent.Type.RESULT, error.getResponse()));
            }
        }
    };
}
