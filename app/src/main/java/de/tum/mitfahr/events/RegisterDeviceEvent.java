package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.Device;

/**
 * Created by Duygu on 15/07/2015.
 */
public class RegisterDeviceEvent extends AbstractEvent{

    public enum Type
    {
        REGISTER_FAILED,
        REGISTER_SUCCESSFUL,
        REGISTER_RESULT
    }

    Device device;

    public RegisterDeviceEvent(Type type, Device device) {

        super(type);
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }
}
