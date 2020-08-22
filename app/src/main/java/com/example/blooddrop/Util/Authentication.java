package com.example.blooddrop.Util;

import com.example.blooddrop.Models.DonationRequests;
import com.example.blooddrop.Models.User;
import com.example.blooddrop.Models.UserLocations;
import com.example.blooddrop.Models.VehicleLocations;

import java.util.List;

public interface Authentication {
void onUserAdded(List<User> userList);
void onVehicleAdded(List<VehicleLocations> vehicleLocationsList);
void onUserLocationAdded(List<UserLocations> userLocationList);
void onDonationRequestAdded(List<DonationRequests> donationRequests);
void onError(Exception ex);
}
