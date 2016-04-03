package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Department;

/**
 * Created by Monika Ullrich on 24.11.2015.
 */
public class DepartmentResponse {

    private String status;
    private ArrayList<Department> departments;

    public DepartmentResponse(String status, ArrayList<Department> departments){
        this.status = status;
        this.departments = departments;
    }

    public String getStatus(){
        return status;
    }

    public ArrayList<Department> getDepartments(){
        return departments;
    }
}
