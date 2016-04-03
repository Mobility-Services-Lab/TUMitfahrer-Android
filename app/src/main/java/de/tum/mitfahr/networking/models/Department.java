package de.tum.mitfahr.networking.models;

import java.io.Serializable;

/**
 * Created by Monika Ullrich on 24.11.2015.
 */
public class Department implements Serializable {
    private String name;
    private String friendly_name;

    public Department(String name, String friendly_name){
        this.name = name;
        this.friendly_name = friendly_name;
    }

    public String getName(){
        return name;
    }
    public String getFriendly_name(){
        return friendly_name;
    }
}
