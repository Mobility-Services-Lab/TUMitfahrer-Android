package de.tum.mitfahr.networking.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abhijith on 16/05/14.
 */
public class User implements Serializable{

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String department;
    private String car;
    private boolean isStudent;
    private String apiKey;
    //private int ratingAverage;
    private int  rating;
    private String createdAt;
    private String updatedAt;
    private Integer avatarId;
    private List<Ride> rides;

    /*
    public class UserDTO {

 private int id;
 private String firstName;
 private String lastName;
 private String email;
 private String phoneNumber;
 private String department;
 private Date createdAt;
 private Date updatedAt;
 private boolean admin;
 private boolean isStudent;
 private Double rating;
 private Integer avatarId;
 private boolean enabled;
 private List<RideDTO> rides;
     */

    public User(int id, String firstName, String lastName, String email, String phoneNumber, String department,
                String car, boolean isStudent, String apiKey, int ratingAverage, String createdAt, String updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.car = car;
        this.isStudent = isStudent;
        this.apiKey = apiKey;
        this.rating = ratingAverage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        //this.avatar = avatar;
    }
// needed an empty constructor
    public  User(){

    }

    public Integer getAvatar() {return avatarId;}
    public void setAvatarId(Integer ai) {avatarId=ai;}

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getCar() {
        return car;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getRatingAverage() {
        return rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", department='" + department + '\'' +
                ", car='" + car + '\'' +
                ", isStudent=" + isStudent +
                ", apiKey='" + apiKey + '\'' +
                ", ratingAverage=" + rating +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public void setStudent(boolean isStudent) {
        this.isStudent = isStudent;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setRatingAverage(int ratingAverage) {
        this.rating = ratingAverage;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
