package com.example.blooddrop.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class DonationRequests implements Parcelable {
    private VehicleLocations vehicleLocations;
    private String vehicleId;
    private User user;
    private String checkedItem;
    private @ServerTimestamp Date date;

    public DonationRequests(){

    }

    public DonationRequests(VehicleLocations vehicleLocations, String vehicleId, User user, String checkedItem, Date date) {
        this.vehicleLocations = vehicleLocations;
        this.vehicleId = vehicleId;
        this.user = user;
        this.checkedItem = checkedItem;
        this.date = date;
    }

    protected DonationRequests(Parcel in) {
        vehicleLocations = in.readParcelable(VehicleLocations.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
        checkedItem = in.readString();
        vehicleId = in.readString();
    }

    public static final Creator<DonationRequests> CREATOR = new Creator<DonationRequests>() {
        @Override
        public DonationRequests createFromParcel(Parcel in) {
            return new DonationRequests(in);
        }

        @Override
        public DonationRequests[] newArray(int size) {
            return new DonationRequests[size];
        }
    };

    public VehicleLocations getVehicleLocations() {
        return vehicleLocations;
    }

    public void setVehicleLocations(VehicleLocations vehicleLocations) {
        this.vehicleLocations = vehicleLocations;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCheckedItem() {
        return checkedItem;
    }

    public void setCheckedItem(String checkedItem) {
        this.checkedItem = checkedItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(vehicleLocations, i);
        parcel.writeParcelable(user, i);
        parcel.writeString(checkedItem);
        parcel.writeString(vehicleId);
    }
}
