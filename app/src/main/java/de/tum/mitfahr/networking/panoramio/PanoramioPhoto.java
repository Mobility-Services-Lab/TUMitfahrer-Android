package de.tum.mitfahr.networking.panoramio;

/**
 * Created by abhijith on 09/10/14.
 */
public class PanoramioPhoto {

    String photoId;
    String photoTitle;
    String photoUrl;
    String photoFileUrl;
    double longitude;
    double latitude;
    String uploadDate;
    int ownerId;
    String ownerName;
    String ownerUrl;

    public PanoramioPhoto(String photoId, String photoTitle, String photoUrl, String photoFileUrl, double longitude, double latitude, String uploadDate, int ownerId, String ownerName, String ownerUrl) {
        this.photoId = photoId;
        this.photoTitle = photoTitle;
        this.photoUrl = photoUrl;
        this.photoFileUrl = photoFileUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.uploadDate = uploadDate;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerUrl = ownerUrl;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoFileUrl() {
        return photoFileUrl;
    }

    public void setPhotoFileUrl(String photoFileUrl) {
        this.photoFileUrl = photoFileUrl;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerUrl() {
        return ownerUrl;
    }

    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }
}
