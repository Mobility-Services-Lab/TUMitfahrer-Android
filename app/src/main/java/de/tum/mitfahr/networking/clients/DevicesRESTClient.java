package de.tum.mitfahr.networking.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import de.tum.mitfahr.events.DisableDeviceEvent;
import de.tum.mitfahr.events.GetDevicesEvent;
import de.tum.mitfahr.events.RegisterDeviceEvent;
import de.tum.mitfahr.networking.api.DevicesAPIService;
import de.tum.mitfahr.networking.models.Device;
import de.tum.mitfahr.networking.models.DeviceReq;
import de.tum.mitfahr.networking.models.requests.RegisterDeviceRequest;
import de.tum.mitfahr.networking.models.response.GetUserDevicesResponse;
import de.tum.mitfahr.networking.models.response.StatusResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Duygu on 15/07/2015.
 */
public class DevicesRESTClient extends AbstractRESTClient {
    private DevicesAPIService devicesAPIService;


    public DevicesRESTClient(String mBaseBackendURL,Context context) {
        super(mBaseBackendURL,context);
        this.devicesAPIService = mRestAdapter.create(DevicesAPIService.class);
    }

    public void getDevices(String userAPIKey, int userId) {
       // devicesAPIService.getStatus(statusCallback);
        devicesAPIService.getUserDevices(userAPIKey, userId, getUserDevicesResponseCallback);
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

    private Callback<GetUserDevicesResponse> getUserDevicesResponseCallback = new Callback<GetUserDevicesResponse>() {
        @Override
        public void success(GetUserDevicesResponse getUserDevicesResponse, Response response) {
            mBus.post(new GetDevicesEvent(GetDevicesEvent.Type.RESULT, getUserDevicesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            //handleErrorResponse(error);
            mBus.post(new GetDevicesEvent(GetDevicesEvent.Type.RESULT, null));
            ///TODO: log the errors
        }
    };

    public void registerDevice(String userAPIKey, int userId, String gcmKey) {
        DeviceReq device = new DeviceReq(gcmKey);
        RegisterDeviceRequest request = new RegisterDeviceRequest(device);
      //  devicesAPIService.getStatus(statusCallback);
        devicesAPIService.registerUserDevice(userAPIKey, userId, request, registerDeviceResponseCallback);
    }

    private Callback<Device> registerDeviceResponseCallback = new Callback<Device>() {
        @Override
        public void success(Device device, Response response) {
            mBus.post(new RegisterDeviceEvent(RegisterDeviceEvent.Type.REGISTER_RESULT, device));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RegisterDeviceEvent(RegisterDeviceEvent.Type.REGISTER_RESULT, null));
            ///TODO: log the errors
        }
    };

    public void disableDevice(String userAPIKey, int userId, int deviceId) {
       // devicesAPIService.getStatus(statusCallback);
        devicesAPIService.disableUserDevice(userAPIKey, userId, deviceId, disableDeviceResponseCallback);
    }

    private Callback<Response> disableDeviceResponseCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            mBus.post(new DisableDeviceEvent(DisableDeviceEvent.Type.RESULT, response2));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new DisableDeviceEvent(DisableDeviceEvent.Type.RESULT, null));
            ///TODO: log the errors
        }
    };
}
