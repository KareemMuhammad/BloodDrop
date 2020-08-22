package com.example.blooddrop.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class VehicleLocations implements Parcelable {
    private String city;
    private String organization;
    private long phone_no;
    private String car_id;
    private String invite_code;
    private GeoPoint geoPoint;
    private @ServerTimestamp Date date;
    private Date departure_date;
    private String critical_blood_type;

    public VehicleLocations(){

    }

    public VehicleLocations(String city, String organization, long phone_no, String car_id, String invite_code, GeoPoint geoPoint, Date date, Date departure_date, String critical_blood_type) {
        this.city = city;
        this.organization = organization;
        this.phone_no = phone_no;
        this.car_id = car_id;
        this.invite_code = invite_code;
        this.geoPoint = geoPoint;
        this.date = date;
        this.departure_date = departure_date;
        this.critical_blood_type = critical_blood_type;
    }

    protected VehicleLocations(Parcel in) {
        city = in.readString();
        organization = in.readString();
        phone_no = in.readLong();
        car_id = in.readString();
        invite_code = in.readString();
        critical_blood_type = in.readString();
    }

    public static final Creator<VehicleLocations> CREATOR = new Creator<VehicleLocations>() {
        @Override
        public VehicleLocations createFromParcel(Parcel in) {
            return new VehicleLocations(in);
        }

        @Override
        public VehicleLocations[] newArray(int size) {
            return new VehicleLocations[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(organization);
        dest.writeLong(phone_no);
        dest.writeString(car_id);
        dest.writeString(invite_code);
        dest.writeString(critical_blood_type);
    }

    public Date getDeparture_date() {
        return departure_date;
    }

    public void setDeparture_date(Date departure_date) {
        this.departure_date = departure_date;
    }

    public String getCritical_blood_type() {
        return critical_blood_type;
    }

    public void setCritical_blood_type(String critical_blood_type) {
        this.critical_blood_type = critical_blood_type;
    }

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public long getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(long phone_no) {
        this.phone_no = phone_no;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }
}
