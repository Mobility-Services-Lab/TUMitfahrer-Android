package de.tum.mitfahr.networking.models.requests;

import android.util.Log;

import de.tum.mitfahr.networking.models.User;

/**
 * Created by abhijith on 09/05/14.
 */
public class RegisterRequest {

    User user= new User();

    public RegisterRequest(String email, String firstName, String lastName, String department, boolean isStudent) {
        Log.i("email", email+"");
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDepartment(department);
        user.setCar("car");

    }
}
