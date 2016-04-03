package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.GetUserDevicesResponse;

/**
 * Created by Duygu on 15/07/2015.
 */
public class GetDevicesEvent extends AbstractEvent {

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }

    private GetUserDevicesResponse response;

    public GetDevicesEvent(Enum type, GetUserDevicesResponse response) {
        super(type);
        this.response = response;
    }

    public GetUserDevicesResponse getResponse() {
        return response;
    }
}
