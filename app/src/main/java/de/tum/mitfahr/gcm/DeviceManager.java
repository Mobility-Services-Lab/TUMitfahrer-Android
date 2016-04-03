package de.tum.mitfahr.gcm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.DisableDeviceEvent;
import de.tum.mitfahr.events.GetDevicesEvent;
import de.tum.mitfahr.events.RegisterDeviceEvent;
import de.tum.mitfahr.networking.models.Device;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.services.ProfileService;

/**
 * Created by Duygu on 15/07/2015.
 */
public class DeviceManager {
    private final static String TAG = DeviceManager.class.getSimpleName();
    private Context localContext;
    private ProfileService profileService;
    private String gcmKey; //check if null
    private int deviceId;  //check if -1
    private User currentUser;

    public DeviceManager(Context context) {
        super();
        this.localContext = context;
        profileService = TUMitfahrApplication.getApplication(localContext).getProfileService();
        currentUser = TUMitfahrApplication.getApplication(localContext).getProfileService().getUserFromPreferences();
        BusProvider.getInstance().register(this);
    }

    private String getGCMKey() {
        String key = profileService.getGCMKeyStored();
        if(null==key) {
            Log.e(TAG, "Getting the token from gcm server..");
            PushNotificationHelper helper = new PushNotificationHelper();
            try {
                helper.getRegistrationID(localContext);
                key = profileService.getGCMKeyStored();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return key;
    }

    public void registerDevice() {
        gcmKey = getGCMKey();
        profileService.registerDevice(gcmKey);
    }

    @Subscribe
    public void onRegisterDeviceResult(RegisterDeviceEvent result) {
        if(result.getType()==RegisterDeviceEvent.Type.REGISTER_SUCCESSFUL) {
            Device device = result.getDevice();
            int deviceID = device.getId();
            Log.e(TAG, "device id: "+deviceID);
            profileService.storeDeviceID(deviceID);
        }
        else if(result.getType()==RegisterDeviceEvent.Type.REGISTER_FAILED) {
            Toast.makeText(localContext, "Device registration failed!", Toast.LENGTH_LONG).show();
            unregisterFromBus();
        }
    }

    public void disableDevice() {
        deviceId = profileService.getDeviceID();
        if(deviceId != -1) {
            profileService.disableDeviceAndLogout(deviceId, false);
        }
        else {
            Log.e(TAG, "There is no stored device id!");
            unregisterFromBus();
        }
    }

    @Subscribe
    public void onDisableDeviceResult(DisableDeviceEvent result) {
        if(result.getType() == DisableDeviceEvent.Type.DISABLE_SUCCESFUL) {
            Log.e(TAG, "Device disabled for notifications");
        } else if(result.getType() == DisableDeviceEvent.Type.DISABLE_FAILED) {
            Log.e(TAG, "Failed to disable device for notifications!");
        }
        unregisterFromBus();
    }

    public void findOrRegisterDevice() {
        deviceId = profileService.getDeviceID();
        if(deviceId == -1) {
            gcmKey = getGCMKey();
            if(null==gcmKey) unregisterFromBus();
            else {
                profileService.getDevices();
            }
        }
        else {
            Log.e(TAG, "There is already a stored device id: " + deviceId);
            unregisterFromBus();
        }
    }

    @Subscribe
    public void onGetDevicesResult(GetDevicesEvent result) {

        if(result.getType() == GetDevicesEvent.Type.GET_SUCCESSFUL) {
            ArrayList<Device> devices = result.getResponse().getDevices();
            gcmKey = getGCMKey();
            if(null!=gcmKey) {
                for (Device device : devices) {
                    String token = device.getToken().trim();
                    Log.e(TAG, "gcmKey:" + gcmKey + "...END");
                    Log.e(TAG, "token :" + token + "...END");
                    Log.e(TAG, "dUid:" + device.getUserId());
                    Log.e(TAG, "devId:" + device.getId());
                    Log.e(TAG, "enabled:" + device.isEnabled());
                    if (gcmKey.equals(token)) {
                        if (device.isEnabled()) {
                            deviceId = device.getId();
                            profileService.storeDeviceID(deviceId);
                            break;
                        }
                    }
                }
                if (deviceId == -1) {
                    registerDevice();
                }
                else unregisterFromBus();
            }
            else {
                Log.e(TAG, "Failed to get token from gcm server!");
                unregisterFromBus();
            }
        }
        else if(result.getType() == GetDevicesEvent.Type.GET_FAILED) {
            Log.e(TAG, "Failed to get devices from server!");
            unregisterFromBus();
        }
    }

    private void unregisterFromBus() {
        BusProvider.getInstance().unregister(this);
    }
}
