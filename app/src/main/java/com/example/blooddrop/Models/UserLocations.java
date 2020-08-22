package com.example.blooddrop.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class UserLocations implements Parcelable {
    private GeoPoint geoPoint;
    private @ServerTimestamp Date date;
    private User user;

    public UserLocations(GeoPoint geoPoint, Date date, User user) {
        this.geoPoint = geoPoint;
        this.date = date;
        this.user = user;
    }
    public UserLocations(){

    }

    protected UserLocations(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<UserLocations> CREATOR = new Creator<UserLocations>() {
        @Override
        public UserLocations createFromParcel(Parcel in) {
            return new UserLocations(in);
        }

        @Override
        public UserLocations[] newArray(int size) {
            return new UserLocations[size];
        }
    };

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
    }
}
