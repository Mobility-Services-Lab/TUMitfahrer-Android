package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Device;

/**
 * Created by Duygu on 14/07/2015.
 */
public class GetUserDevicesResponse {
    private String status;
    private ArrayList<Device> devices;

    public GetUserDevicesResponse(String status, ArrayList<Device> devices) {
        this.status = status;
        this.devices = devices;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
}
