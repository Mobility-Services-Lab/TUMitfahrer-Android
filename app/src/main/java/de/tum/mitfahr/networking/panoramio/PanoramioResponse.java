package de.tum.mitfahr.networking.panoramio;

/**
 * Created by abhijith on 09/10/14.
 */
public class PanoramioResponse {

    long count;
    PanoramioPhoto[] photos;

    public PanoramioResponse(long count, PanoramioPhoto[] photos) {
        this.count = count;
        this.photos = photos;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public PanoramioPhoto[] getPhotos() {
        return photos;
    }

    public void setPhotos(PanoramioPhoto[] photos) {
        this.photos = photos;
    }
}
