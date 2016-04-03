package de.tum.mitfahr.networking.models;

import java.io.Serializable;

/**
 * Created by Duygu on 14/07/2015.
 */
public class Device implements Serializable{
    private String token;
    private boolean enabled;
    private String platform;
    private String language;
    private String createdAt;
    private String updatedAt;
    private int id;
    private int userId;


    public Device(String token, boolean enabled, String platform, String language, String createdAt, String updatedAt, int id, int userId) {
        this.token = token;
        this.enabled = enabled;
        this.platform = platform;
        this.language = language;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id = id;
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
