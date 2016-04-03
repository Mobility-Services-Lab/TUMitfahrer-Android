package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.UploadAvatarResponse;
import retrofit.client.Response;

/**
 * Created by Duygu on 18.6.2015.
 */
public class UploadAvatarEvent extends AbstractEvent{

    public enum Type
    {
        UPLOAD_SUCCESSFUL,
        UPLOAD_FAILED,
        RESULT
    }

    private Response response;

    public UploadAvatarEvent(Type type, Response response) {
        super(type);
        this.response = response;
    }

    public Response getResponse() { return this.response; }
}
