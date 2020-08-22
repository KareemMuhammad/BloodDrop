package com.example.blooddrop.Data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blooddrop.Models.DonationRequests;
import com.example.blooddrop.Models.User;
import com.example.blooddrop.Models.UserLocations;
import com.example.blooddrop.Models.VehicleLocations;
import com.example.blooddrop.Util.Authentication;

import java.util.List;

public class UserViewModel extends ViewModel implements Authentication {

    private UserRepository userRepository = new UserRepository(this);

    private MutableLiveData<List<User>> liveData = new MutableLiveData<>();
    private MutableLiveData<List<DonationRequests>> liveDataDonation = new MutableLiveData<>();
    private MutableLiveData<List<UserLocations>> liveDataLocation = new MutableLiveData<>();
    private MutableLiveData<List<VehicleLocations>> liveDataVehicle = new MutableLiveData<>();

    public UserViewModel (){
        userRepository.getUserList();
        userRepository.getLocationList();
        userRepository.getLiveDataVehicle();
        userRepository.getLiveDataDonation();
    }

    public MutableLiveData<List<User>> getLiveData() {
        return liveData;
    }

    public MutableLiveData<List<UserLocations>> getLiveDataLocation() {
        return liveDataLocation;
    }

    public MutableLiveData<List<VehicleLocations>> getLiveDataVehicle() {
        return liveDataVehicle;
    }

    public MutableLiveData<List<DonationRequests>> getLiveDataDonation() {
        return liveDataDonation;
    }

    @Override
    public void onUserAdded(List<User> userList) {
       liveData.setValue(userList);
    }

    @Override
    public void onVehicleAdded(List<VehicleLocations> vehicleLocationsList) {
        liveDataVehicle.setValue(vehicleLocationsList);
    }

    @Override
    public void onUserLocationAdded(List<UserLocations> userLocationList) {
        liveDataLocation.setValue(userLocationList);
    }

    @Override
    public void onDonationRequestAdded(List<DonationRequests> donationRequests) {
        liveDataDonation.setValue(donationRequests);
    }

    @Override
    public void onError(Exception ex) {

    }
}
