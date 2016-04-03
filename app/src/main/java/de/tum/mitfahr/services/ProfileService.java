package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.ChangePasswordEvent;
import de.tum.mitfahr.events.DisableDeviceEvent;
import de.tum.mitfahr.events.ForgotPasswordEvent;
import de.tum.mitfahr.events.GetDepartmentEvent;
import de.tum.mitfahr.events.GetDevicesEvent;
import de.tum.mitfahr.events.GetUserEvent;
import de.tum.mitfahr.events.LoginEvent;
import de.tum.mitfahr.events.RegisterDeviceEvent;
import de.tum.mitfahr.events.RegisterEvent;
import de.tum.mitfahr.events.SessionCheckEvent;
import de.tum.mitfahr.events.UpdateUserEvent;
import de.tum.mitfahr.gcm.DeviceManager;
import de.tum.mitfahr.networking.api.DevicesAPIService;
import de.tum.mitfahr.networking.clients.AvatarRESTClient;
import de.tum.mitfahr.networking.clients.DevicesRESTClient;
import de.tum.mitfahr.networking.clients.ProfileRESTClient;
import de.tum.mitfahr.networking.models.Device;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.networking.models.requests.UpdateUserRequest;
import de.tum.mitfahr.networking.models.response.DepartmentResponse;
import de.tum.mitfahr.networking.models.response.GetUserDevicesResponse;
import de.tum.mitfahr.networking.models.response.GetUserResponse;
import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import de.tum.mitfahr.networking.models.response.SessionResponse;
import de.tum.mitfahr.networking.models.response.UpdateUserResponse;
import retrofit.client.Response;

/**
 * Created by abhijith on 09/05/14.
 */
public class ProfileService {


    private static final String TAG = ProfileService.class.getSimpleName();
    private static String profileImgName = "profile_image.png";

    private SharedPreferences mSharedPreferences;
    private ProfileRESTClient mProfileRESTClient;
    private AvatarRESTClient mAvatarRESTClient;
    private DevicesRESTClient mDevicesRESTClient;
    private Context mContext;
    private Bus mBus;
    private String userAPIKey;
    private int userId;
    private boolean loggingOut = false;
    private User user;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            File path = Environment.getExternalStorageDirectory();
            File dirFile = new File(path, "/" + "tumitfahr");
            File imageFile = new File(dirFile, profileImgName);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    public ProfileService(final Context context) {
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mProfileRESTClient = new ProfileRESTClient(baseBackendURL,context);
        mAvatarRESTClient = new AvatarRESTClient(baseBackendURL);
        mDevicesRESTClient = new DevicesRESTClient(baseBackendURL,context);
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }

    public ProfileService() {

    }

    public void login(String email, String password) {
        mProfileRESTClient.login(email, password);
    }

    public void register(String email, String firstName, String lastName, String department) {
        mProfileRESTClient.registerUserAccount(email, firstName, lastName, department, true);
    }

    public void getDepartments(){
        mProfileRESTClient.getDepartments();
    }

    public boolean isLoggedIn() {
        String apiKey = mSharedPreferences.getString("api_key", null);
        return apiKey != null;
    }

    public  void checkSessionValidity() {
        String apiKey = mSharedPreferences.getString("api_key", null);
        mProfileRESTClient.isSessionValid(apiKey);
    }

    @Subscribe
    public void onSessionCheckResult(SessionCheckEvent result) {
        if (result.getType() == SessionCheckEvent.Type.RESULT) {
            Response response = result.getResponse();
            if((response!=null) && (response.getStatus()==200)) {
                //Log.e(TAG, "session valid!");
                mBus.post(new SessionCheckEvent(SessionCheckEvent.Type.VALID, response));
            }
            else {
                //Log.e(TAG, "session invalid!");
                //Log.e(TAG, "response: " + response);
                mBus.post(new SessionCheckEvent(SessionCheckEvent.Type.INVALID, null));
            }
        }
    }

    @Subscribe
    public void onLoginResult(LoginEvent result) {
        if (result.getType() == LoginEvent.Type.LOGIN_RESULT) {
            LoginResponse response = result.getResponse();
            if (null == response) {
                mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_FAILED));
            } else {
                //Log.i(TAG, response.getUser().toString());
                addUserToSharedPreferences(response.getUser());
                mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_SUCCESSFUL));
                userId = mSharedPreferences.getInt("id", 0);
                userAPIKey = mSharedPreferences.getString("api_key", null);
                //Log.i(TAG, "user id: " + userId);
                //Log.i(TAG, "user apiKey: " + userAPIKey);

            }
        }
    }

    @Subscribe
    public void onGetDepartmentResult(GetDepartmentEvent result){
        if(result.getType() == GetDepartmentEvent.Type.RESULT){
            DepartmentResponse response = result.getResponse();
            if(null != response.getStatus() && !response.getStatus().equals("OK")){
                mBus.post(new GetDepartmentEvent(GetDepartmentEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetDepartmentEvent(GetDepartmentEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    @Subscribe
    public void onRegisterResult(RegisterEvent result) {
        if (result.getType() == RegisterEvent.Type.REGISTER_RESULT) {
            RegisterResponse response = result.getResponse();
            if (null != response.getStatus() && response.getStatus().equals("bad_request")) {
                mBus.post(new RegisterEvent(RegisterEvent.Type.REGISTER_FAILED));
            } else {
                mBus.post(new RegisterEvent(RegisterEvent.Type.REGISTER_SUCCESSFUL));
            }
        }
    }

    public User getUserSynchronous(int userId) {
        return mProfileRESTClient.getUserSynchronous(userId, userAPIKey);
    }

    public void getUser(int userId) {
        mProfileRESTClient.getSomeUser(userId, userAPIKey);
    }

    @Subscribe
    public void onGetUserResult(GetUserEvent result) {
        if (result.getType() == GetUserEvent.Type.RESULT) {
            GetUserResponse response = result.getGetUserResponse();
            if (null == response.getUser()) {
                mBus.post(new GetUserEvent(GetUserEvent.Type.GET_FAILED, response, result.getRetrofitResponse()));
            } else {
                mBus.post(new GetUserEvent(GetUserEvent.Type.GET_SUCCESSFUL, response, result.getRetrofitResponse()));
            }
        }
    }

    public void updateUser(User updatedUser) {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(updatedUser);
        mProfileRESTClient.updateUser(userId, updateUserRequest, userAPIKey);
    }

    @Subscribe
    public void onUpdateUserResult(UpdateUserEvent result) {
        if (result.getType() == UpdateUserEvent.Type.RESULT) {
            UpdateUserResponse response = result.getUpdateUserResponse();
            if ((null != response) && (200 == result.getRetrofitResponse().getStatus())) {
                mBus.post(new UpdateUserEvent(UpdateUserEvent.Type.USER_UPDATED, response, result.getRetrofitResponse()));
            } else {
                mBus.post(new UpdateUserEvent(UpdateUserEvent.Type.UPDATE_FAILED, response, result.getRetrofitResponse()));
            }
        }
    }

    public void forgotPassword(String email) {
        mProfileRESTClient.forgotPassword(email);
    }

    @Subscribe
    public  void onForgotPasswordResult(ForgotPasswordEvent result) {
        if (result.getType() == ForgotPasswordEvent.Type.RESULT) {
            if((result.getRetrofitResponse()!=null)&&(result.getRetrofitResponse().getStatus()==200)) {
                mBus.post(new ForgotPasswordEvent(ForgotPasswordEvent.Type.SUCCESS, result.getRetrofitResponse()));
            }
            else if((result.getRetrofitResponse()!=null)&&(result.getRetrofitResponse().getStatus()==503)) {
                //Log.e(TAG, "503 not found : "+ result.getRetrofitResponse().getReason());
                mBus.post(new ForgotPasswordEvent(ForgotPasswordEvent.Type.NOT_USER, result.getRetrofitResponse()));
            }
            else {
                //Log.e(TAG, "other fail : "+ result.getRetrofitResponse().getReason());
                mBus.post(new ForgotPasswordEvent(ForgotPasswordEvent.Type.FAIL, null));
            }
        }
    }

    public void updatePassword(String oldPassword, String newPassword) {
        String email = mSharedPreferences.getString("email", "");
        mProfileRESTClient.changePassword(oldPassword, newPassword, email);
    }

    @Subscribe
    public void onUpdatePasswordResult(ChangePasswordEvent result) {
        if (result.getType() == ChangePasswordEvent.Type.RESULT) {
            Response response = result.getRetrofitResponse();
            if ((null != response) && (response.getStatus() == 200)) {
                mBus.post(new ChangePasswordEvent(ChangePasswordEvent.Type.SUCCESS, response));
            } else {
                mBus.post(new ChangePasswordEvent(ChangePasswordEvent.Type.FAIL, response));
            }
        }
    }

    public String getStoredAvatarURL() {
        String url = mSharedPreferences.getString("avatar_url", "");
        return url;
    }

    public void setStoredAvatarURL(String url) {
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        prefEditor.putString("avatar_url", url);
        prefEditor.commit();
    }

    public User getUserFromPreferences() {
        int id = mSharedPreferences.getInt("id", 0);
        String firstName = mSharedPreferences.getString("first_name", "");
        String lastName = mSharedPreferences.getString("last_name", "");
        String email = mSharedPreferences.getString("email", "");
        String phoneNumber = mSharedPreferences.getString("phone_number", "");
        String department = mSharedPreferences.getString("department", "");
        String car = mSharedPreferences.getString("car", "");
        boolean isStudent = mSharedPreferences.getBoolean("is_student", true);
        String apiKey = mSharedPreferences.getString("api_key", "");
        int ratingAverage = mSharedPreferences.getInt("rating_average", 0);
        String createdAt = mSharedPreferences.getString("created_at", "");
        String updatedAt = mSharedPreferences.getString("updated_at", "");

        User currentUser = new User(
                id, firstName, lastName, email,
                phoneNumber, department, car,
                isStudent, apiKey, ratingAverage,
                createdAt, updatedAt);
        return currentUser;
    }

    public void addUserToSharedPreferences(User user) {
        Log.d("USER", user.toString());
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        prefEditor.putInt("id", user.getId());
        prefEditor.putString("first_name", user.getFirstName());
        prefEditor.putString("last_name", user.getLastName());
        prefEditor.putString("email", user.getEmail());
        prefEditor.putString("phone_number", user.getPhoneNumber());
        prefEditor.putString("car", user.getCar());
        prefEditor.putBoolean("is_student", user.isStudent());
        prefEditor.putString("api_key", user.getApiKey());
        prefEditor.putInt("rating_average", user.getRatingAverage());
        prefEditor.putString("created_at", user.getCreatedAt());
        prefEditor.putString("updated_at", user.getUpdatedAt());
        prefEditor.putString("department", user.getDepartment());
        prefEditor.commit();
    }

    public void logout() {
        int deviceId = getDeviceID();
        if(deviceId != -1) {
            disableDeviceAndLogout(deviceId, true);
        }
        else performLogout();
    }

    private void performLogout() {
        mProfileRESTClient.logout(userAPIKey);

        //Log.e(TAG, "Logout invoked!!");
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        prefEditor.clear();
        prefEditor.commit();
    }

    ///avatar functions

    public String getProfileImage() {
        String url = mAvatarRESTClient.getAvatarUrl(userId);
        //Log.e(TAG, "getProfileImage(): " + url);
        return url;
    }

    public String getProfileImageURL(int uId) {
        String url = mAvatarRESTClient.getAvatarUrl(uId);
        return url;
    }

    public void deleteCachedImage() {
        File path = Environment.getExternalStorageDirectory();
        File dirFile = new File(path, "/" + "tumitfahr");
        dirFile.mkdirs();
        File imageFile = new File(dirFile, profileImgName);
        if (imageFile.exists())
            imageFile.delete();
    }

    public void uploadAvatar(String avatarFilePath) {
        Log.d("uploadImage", "" + avatarFilePath);
        File imageFile = new File(avatarFilePath);
        mAvatarRESTClient.uploadAvatar(userAPIKey, userId, imageFile);

    }


    ///devices controller functions

    public String getGCMKeyStored() {
        return mSharedPreferences.getString("googlePlayRegisterId", null);
    }

    public int getDeviceID() {
        return mSharedPreferences.getInt("device_id", -1);
    }

    public void storeDeviceID(int id) {
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        prefEditor.putInt("device_id", id);
        prefEditor.commit();
    }

    public void getDevices() {
        mDevicesRESTClient.getDevices(userAPIKey, userId);
    }

    @Subscribe
    public void onGetDevicesResult(GetDevicesEvent result) {
        if(result.getType() == GetDevicesEvent.Type.RESULT) {
            GetUserDevicesResponse response = result.getResponse();
            if(null!=response) {
                ArrayList<Device> devices = response.getDevices();
                if(null!=devices) {
                    mBus.post(new GetDevicesEvent(GetDevicesEvent.Type.GET_SUCCESSFUL, response));
                }
                else mBus.post(new GetDevicesEvent(GetDevicesEvent.Type.GET_FAILED, null));
            }
        }
    }

    public void registerDevice(String gcmKey) {
        mDevicesRESTClient.registerDevice(userAPIKey, userId, gcmKey);
    }

    @Subscribe
    public void onRegisterDeviceResult(RegisterDeviceEvent result) {
        if(result.getType() == RegisterDeviceEvent.Type.REGISTER_RESULT) {
            Device device = result.getDevice();
            if(null!=device) {
                mBus.post(new RegisterDeviceEvent(RegisterDeviceEvent.Type.REGISTER_SUCCESSFUL, device));
            }
            else mBus.post(new RegisterDeviceEvent(RegisterDeviceEvent.Type.REGISTER_FAILED, null));
        }
    }

    public void disableDeviceAndLogout(int deviceId, boolean logout) {
        loggingOut=logout;
        mDevicesRESTClient.disableDevice(userAPIKey, userId, deviceId);
    }

    @Subscribe
    public void onDisableDeviceResult(DisableDeviceEvent result) {
        if(result.getType()==DisableDeviceEvent.Type.RESULT) {
            Response response = result.getRetrofitResponse();
            if((null!=response) && (response.getStatus()==200)) {
                mBus.post(new DisableDeviceEvent(DisableDeviceEvent.Type.DISABLE_SUCCESFUL, response));
            }
            else mBus.post(new DisableDeviceEvent(DisableDeviceEvent.Type.DISABLE_FAILED, null));
        }
        if(loggingOut) {
            loggingOut=false;
            performLogout();
        }
    }

}