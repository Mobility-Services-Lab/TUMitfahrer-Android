package de.tum.mitfahr.networking.models.requests;

import de.tum.mitfahr.networking.models.Device;
import de.tum.mitfahr.networking.models.DeviceReq;

/**
 * Created by Duygu on 14/07/2015.
 */
public class RegisterDeviceRequest {
    private DeviceReq device;

    public RegisterDeviceRequest(DeviceReq device) {
        this.device = device;
    }

    public DeviceReq getDevice() {
        return device;
    }

    public void setDevice(DeviceReq device) {
        this.device = device;
    }
}
