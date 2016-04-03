package de.tum.mitfahr.networking.models.requests;

import de.tum.mitfahr.networking.models.User;

/**
 * Created by amr on 22/06/14.
 */
public class UpdateUserRequest {

    private UpdateUserRequester user;

    public UpdateUserRequest(User user) {
        this.user = new UpdateUserRequester(user);
    }

    private class UpdateUserRequester {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String department;
        private String car;
        public UpdateUserRequester(User user) {
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.phoneNumber = user.getPhoneNumber();
            this.department = user.getDepartment();
            this.car = user.getCar();
        }
    }
}
