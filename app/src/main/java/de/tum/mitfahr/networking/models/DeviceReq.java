package de.tum.mitfahr.networking.models;

import java.io.Serializable;

/**
 * Created by Duygu on 16/07/2015.
 */
public class DeviceReq implements Serializable {

    private String token;
    private boolean enabled;
    private String platform;
    private String language;

    public DeviceReq(String token) {
        this.token = token;
        this.enabled = true;
        this.platform = "android";
        this.language = "en";
    }
}
