package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import de.tum.mitfahr.events.ChangePasswordEvent;
import de.tum.mitfahr.events.ForgotPasswordEvent;
import de.tum.mitfahr.events.GetDepartmentEvent;
import de.tum.mitfahr.events.GetUserEvent;
import de.tum.mitfahr.events.LoginEvent;
import de.tum.mitfahr.events.LogoutEvent;
import de.tum.mitfahr.events.RegisterEvent;
import de.tum.mitfahr.events.SessionCheckEvent;
import de.tum.mitfahr.events.UpdateUserEvent;
import de.tum.mitfahr.networking.BackendUtil;
import de.tum.mitfahr.networking.api.SessionAPIService;
import de.tum.mitfahr.networking.api.UserAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.networking.models.requests.ChangePasswordRequest;
import de.tum.mitfahr.networking.models.requests.ForgotPasswordRequest;
import de.tum.mitfahr.networking.models.requests.RegisterRequest;
import de.tum.mitfahr.networking.models.requests.UpdateUserRequest;
import de.tum.mitfahr.networking.models.response.ChangePasswordResponse;
import de.tum.mitfahr.networking.models.response.DepartmentResponse;
import de.tum.mitfahr.networking.models.response.ForgotPasswordResponse;
import de.tum.mitfahr.networking.models.response.GetUserResponse;
import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.LogoutResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import de.tum.mitfahr.networking.models.response.SessionResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import de.tum.mitfahr.networking.models.response.UpdateUserResponse;
import de.tum.mitfahr.ui.RegisterFragment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by abhijith on 09/05/14.
 */
public class ProfileRESTClient extends AbstractRESTClient {

    private static final String TAG = ProfileRESTClient.class.getSimpleName();
    private UserAPIService userAPIService;

    public ProfileRESTClient(String mBaseBackendURL,Context context) {
        super(mBaseBackendURL,context);
        userAPIService = mRestAdapter.create(UserAPIService.class);
    }

    public void registerUserAccount(final String email,
                                    final String firstName,
                                    final String lastName,
                                    final String department,
                                    final boolean isStudent) {
        RegisterRequest requestData = new RegisterRequest(email, firstName, lastName, department, isStudent);
        UserAPIService userAPIService = mRestAdapter.create(UserAPIService.class);
       // userAPIService.getStatus(statusCallback);
        userAPIService.registerUser(requestData, registerCallback);
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

    private Callback<RegisterResponse> registerCallback = new Callback<RegisterResponse>() {
        @Override
        public void success(RegisterResponse registerResponse, Response response) {
            Log.i("Register User","in Success Method");
            // Post an event based on success on the BUS! :)
            mBus.post(new RegisterEvent(RegisterEvent.Type.REGISTER_RESULT, registerResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Log.i("Register User","in Failure Method");
            if(null==retrofitError.getResponse()){
                mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR, retrofitError));
            }
            else if(retrofitError.getResponse().getStatus() == 404){
                mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.ERROR_404, retrofitError));
            }
            else if(retrofitError.getResponse().getStatus() == 401) {
                mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.ERROR_401, retrofitError));
            }
            RegisterResponse json = (RegisterResponse) retrofitError.getBodyAs(RegisterResponse.class);

           String[] errors = json.getErrors();
            mBus.post(new RegisterEvent(RegisterEvent.Type.REGISTER_FAILED, json));
        }
    };


    public void logout(String userApiKey) {
        SessionAPIService sessionAPIService = mRestAdapter.create(SessionAPIService.class);
       // sessionAPIService.getStatus(statusCallback);
        sessionAPIService.logoutUser(userApiKey, logoutCallback);
    }

    private Callback<LogoutResponse> logoutCallback = new Callback<LogoutResponse>() {
        @Override
        public void success(LogoutResponse logoutResponse, Response response) {
            mBus.post(new LogoutEvent(LogoutEvent.Type.LOGOUT_RESULT, logoutResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new LogoutEvent(LogoutEvent.Type.LOGOUT_RESULT, null));
        }
    };


    public void login(final String email, final String password) {
        SessionAPIService sessionAPIService = mRestAdapter.create(SessionAPIService.class);
      //  sessionAPIService.getStatus(statusCallback);
        sessionAPIService.loginUser(BackendUtil.getLoginHeader(email, password), loginCallback);
    }

    private Callback<LoginResponse> loginCallback = new Callback<LoginResponse>() {

        @Override
        public void success(LoginResponse loginResponse, Response response) {
            Log.i(TAG, loginResponse.toString());
            Log.i(TAG, response.toString());
            Log.i(TAG, response.getReason().toString());
            Log.i(TAG, response.getStatus()+"");
            Log.i(TAG, response.getUrl());
            Log.i(TAG, new String(((TypedByteArray) response.getBody()).getBytes()));
            mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_RESULT, loginResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            if(null==retrofitError.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR, retrofitError));
            else if(retrofitError.getResponse().getStatus() == 404) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.ERROR_404, retrofitError));
           mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_FAILED, null));

        }
    };

    public void isSessionValid(String apiKey) {
        SessionAPIService sessionAPIService = mRestAdapter.create(SessionAPIService.class);
       // sessionAPIService.getStatus(statusCallback);
        sessionAPIService.checkSessionValidity(apiKey, sessionValidityCallback);
    }
    private Callback<SessionResponse> sessionValidityCallback = new Callback<SessionResponse>() {
        @Override
        public void success(SessionResponse sessionResponse, Response response) {
            mBus.post(new SessionCheckEvent(SessionCheckEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new SessionCheckEvent(SessionCheckEvent.Type.RESULT, null));
        }
    };

    public User getUserSynchronous(int someUserId, String userAPIKey) {
        GetUserResponse response = userAPIService.getUserSynchronous(userAPIKey, someUserId);
        if (null != response && null != response.getUser()) {
            return response.getUser();
        }
        return null;
    }

    public void getSomeUser(int someUserId, String userAPIKey) {
      //  userAPIService.getStatus(statusCallback);
        userAPIService.getSomeUser(userAPIKey, someUserId, getUserCallback);
    }

    private Callback<GetUserResponse> getUserCallback = new Callback<GetUserResponse>() {
        @Override
        public void success(GetUserResponse getUserResponse, Response response) {
            mBus.post(new GetUserEvent(GetUserEvent.Type.RESULT, getUserResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
        }
    };

    public void updateUser(int userId, UpdateUserRequest updateUserRequest,String userAPIKey) {

      //  userAPIService.getStatus(statusCallback);
        userAPIService.updateUser(userAPIKey, userId, updateUserRequest, updateUserCallback);

    }

    private Callback<UpdateUserResponse> updateUserCallback = new Callback<UpdateUserResponse>() {
        @Override
        public void success(UpdateUserResponse updateUserResponse, Response response) {
            mBus.post(new UpdateUserEvent(UpdateUserEvent.Type.RESULT, updateUserResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
            mBus.post(new UpdateUserEvent(UpdateUserEvent.Type.UPDATE_FAILED, null, null));
        }
    };

    public void changePassword(String oldPassword, String newPassword, String email) {
        String auth = BackendUtil.getLoginHeader(email, oldPassword);
      //  userAPIService.getStatus(statusCallback);
        userAPIService.changePassword(auth, new ChangePasswordRequest(newPassword), changePasswordCallback);
    }

    private Callback<ChangePasswordResponse> changePasswordCallback = new Callback<ChangePasswordResponse>() {
        @Override
        public void success(ChangePasswordResponse changePasswordResponse, Response response) {
            mBus.post(new ChangePasswordEvent(ChangePasswordEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if(null==error.getResponse()) mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));
            mBus.post(new ChangePasswordEvent(ChangePasswordEvent.Type.FAIL, null));
        }
    };

    public void forgotPassword(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        //userAPIService.forgotPassword(request, forgotPasswordCallback);
      //  userAPIService.getStatus(statusCallback);
        userAPIService.forgotPassword(email, forgotPasswordCallback);
    }

    private Callback<ForgotPasswordResponse> forgotPasswordCallback = new Callback<ForgotPasswordResponse>() {
        @Override
        public void success(ForgotPasswordResponse o, Response response) {
            mBus.post(new ForgotPasswordEvent(ForgotPasswordEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            if((error.getResponse()!=null)&&(error.getResponse().getStatus()==503)) {
                Log.e(TAG, "password error: "+error.getResponse().getStatus());
                mBus.post(new ForgotPasswordEvent(ForgotPasswordEvent.Type.NOT_USER, null));
            }
            else if(error.getResponse()!=null) {
                mBus.post(new ForgotPasswordEvent(ForgotPasswordEvent.Type.FAIL, null));
            }
            else mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR,error));

        }
    };
    public  void getDepartments(){
      //  userAPIService.getStatus(statusCallback);
        userAPIService.getDepartments(departmentCallback);
    }

    private Callback<DepartmentResponse> departmentCallback = new Callback<DepartmentResponse>() {
        @Override
        public void success(DepartmentResponse departmentResponse, Response response) {
            mBus.post(new GetDepartmentEvent(GetDepartmentEvent.Type.RESULT, departmentResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            if(error.getResponse() != null){
                mBus.post(new GetDepartmentEvent(GetDepartmentEvent.Type.GET_FAILED, null));
            } else {
                mBus.post(new RequestFailedEvent(RequestFailedEvent.Type.CONNECTION_ERROR, error));
            }
        }
    };

}
