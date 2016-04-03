package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.RegisterResponse;

/**
 * Created by amr on 25/05/14.
 */
public class RegisterEvent extends AbstractEvent {

    public enum Type
    {
        REGISTER_FAILED,
        REGISTER_SUCCESSFUL,
        REGISTER_RESULT
    }

    RegisterResponse response;

    public RegisterEvent(Type type)
    {
        super(type);
    }

    public RegisterEvent(Type type, RegisterResponse response) {

        super(type);
        this.response = response;
    }

    public RegisterResponse getResponse() {
        return response;
    }

}
