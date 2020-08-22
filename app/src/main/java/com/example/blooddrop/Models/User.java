package com.example.blooddrop.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Parcelable {
    private String full_name;
    private String password;
    private String address;
    private String email;
    private long phone_no;
    private String user_id;
    private String blood_type;
    private float points ;

    public User (){

    }

    public User(String full_name, String password, String address, String email, long phone_no, String user_id, String blood_type,float points) {
        this.full_name = full_name;
        this.password = password;
        this.address = address;
        this.email = email;
        this.phone_no = phone_no;
        this.user_id = user_id;
        this.blood_type = blood_type;
        this.points = points;
    }

    protected User(Parcel in) {
        full_name = in.readString();
        password = in.readString();
        address = in.readString();
        email = in.readString();
        phone_no = in.readLong();
        user_id = in.readString();
        blood_type = in.readString();
        points = in.readFloat();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(String blood_type) {
        this.blood_type = blood_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(long phone_no) {
        this.phone_no = phone_no;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(full_name);
        dest.writeString(password);
        dest.writeString(address);
        dest.writeString(email);
        dest.writeLong(phone_no);
        dest.writeString(user_id);
        dest.writeString(blood_type);
        dest.writeFloat(points);
    }
}
