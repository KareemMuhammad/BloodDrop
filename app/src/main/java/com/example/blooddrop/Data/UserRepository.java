package com.example.blooddrop.Data;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.blooddrop.Models.DonationRequests;
import com.example.blooddrop.Models.User;
import com.example.blooddrop.Models.UserLocations;
import com.example.blooddrop.Models.VehicleLocations;
import com.example.blooddrop.Util.Authentication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserRepository {
    private static final String USER_COLLECTION = "Users";
    private static final String USER_LOCATION_COLLECTION = "User Locations";
    private static final String VEHICLE_LOCATION_COLLECTION = "Vehicle Locations";
    private static final String DONATION_REQUESTS_COLLECTION = "Donation Requests";
    private List<User> userList = new ArrayList<>();
    private List<UserLocations> userListLocation = new ArrayList<>();
    private List<VehicleLocations> vehicleList = new ArrayList<>();
    private List<DonationRequests> donationList = new ArrayList<>();
    private FirebaseFirestore fDb = FirebaseFirestore.getInstance();
    private Authentication authentication;
    private MutableLiveData<List<User>> liveData = new MutableLiveData<>();
    private MutableLiveData<List<UserLocations>> liveDataLocation = new MutableLiveData<>();
    private MutableLiveData<List<VehicleLocations>> liveDataVehicle = new MutableLiveData<>();
    private MutableLiveData<List<DonationRequests>> liveDataDonation = new MutableLiveData<>();

    public  UserRepository (Authentication authentication){
        this.authentication = authentication;
    }

    public MutableLiveData<List<UserLocations>> getLocationList() {
        getAllUsersLocations();
        liveDataLocation.setValue(userListLocation);
        return liveDataLocation;
    }

    public MutableLiveData<List<User>> getUserList() {
        getAllUsersDocuments();
        liveData.setValue(userList);
        return liveData;
    }

    public MutableLiveData<List<VehicleLocations>> getLiveDataVehicle() {
        getAllVehicleLocations();
        liveDataVehicle.setValue(vehicleList);
        return liveDataVehicle;
    }
    public MutableLiveData<List<DonationRequests>> getLiveDataDonation() {
        getAllDonationRequests();
        liveDataDonation.setValue(donationList);
        return liveDataDonation;
    }

    private void getAllUsersDocuments(){
        fDb.collection(USER_COLLECTION)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult() != null){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            User user = doc.toObject(User.class);
                            userList.add(user);
                            authentication.onUserAdded(userList);
                        }
                    }else {
                        authentication.onError(task.getException());
                    }
                }else {
                    authentication.onError(task.getException());
                }
            }
        });
    }
    private void getAllUsersLocations(){
        fDb.collection(USER_LOCATION_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    UserLocations userLocations = doc.toObject(UserLocations.class);
                                    userListLocation.add(userLocations);
                                    authentication.onUserLocationAdded(userListLocation);
                                }
                            }else {
                                authentication.onError(task.getException());
                            }
                        }else {
                            authentication.onError(task.getException());
                        }
                    }
                });
    }
    private void getAllVehicleLocations(){
        fDb.collection(VEHICLE_LOCATION_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    VehicleLocations vehicleLocations = doc.toObject(VehicleLocations.class);
                                    vehicleList.add(vehicleLocations);
                                    authentication.onVehicleAdded(vehicleList);
                                }
                            }else {
                                authentication.onError(task.getException());
                            }
                        }else {
                            authentication.onError(task.getException());
                        }
                    }
                });
    }
    private void getAllDonationRequests(){
        fDb.collection(DONATION_REQUESTS_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    DonationRequests donationRequests = doc.toObject(DonationRequests.class);
                                    donationList.add(donationRequests);
                                    authentication.onDonationRequestAdded(donationList);
                                }
                            }else {
                                authentication.onError(task.getException());
                            }
                        }else {
                            authentication.onError(task.getException());
                        }
                    }
                });
    }
}
