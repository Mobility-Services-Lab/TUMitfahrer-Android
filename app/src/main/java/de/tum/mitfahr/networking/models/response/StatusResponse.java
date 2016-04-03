package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Department;

/**
 * Created by Kiran on 1/7/2016.
 */

public class StatusResponse {

    public String status;

    public StatusResponse(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
