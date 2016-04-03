package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.DepartmentResponse;

/**
 * Created by Monika Ullrich on 24.11.2015.
 */
public class GetDepartmentEvent extends AbstractEvent{
    private DepartmentResponse departmentResponse;

    public GetDepartmentEvent(Type type, DepartmentResponse departmentResponse) {
        super(type);
        this.departmentResponse = departmentResponse;
    }

    public GetDepartmentEvent(Type type) {
        super(type);
    }

    public enum Type
    {
        GET_SUCCESSFUL,
        GET_FAILED,
        RESULT
    }
    public DepartmentResponse getResponse() {
        return departmentResponse;
    }
}
