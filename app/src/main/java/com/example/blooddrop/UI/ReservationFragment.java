package com.example.blooddrop.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.blooddrop.Data.UserViewModel;
import com.example.blooddrop.Models.DonationRequests;
import com.example.blooddrop.Models.User;
import com.example.blooddrop.Models.VehicleLocations;
import com.example.blooddrop.R;
import com.example.blooddrop.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ReservationFragment extends DialogFragment implements View.OnClickListener {

    public ReservationFragment(){

    }

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final String VEHICLE_KEY = "vehicle key";
    private UserViewModel viewModel;
    private List<VehicleLocations> mVehicleLocationsList ;
    private List<DonationRequests> donationRequestsList;
    private Button btnToSave;
    private EditText nationalID;
    private CheckBox plasmaCheck, plateletsCheck, bothCheck;
    private FirebaseFirestore mFireDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reservation_fragment,container,false);
        initializeView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getVehicle();
    }

    public void initializeView(View view){
        mFireDb = FirebaseFirestore.getInstance();
        btnToSave = view.findViewById(R.id.order_btn);
        nationalID = view.findViewById(R.id.textNationalId);
        plasmaCheck = view.findViewById(R.id.check_box);
        plateletsCheck = view.findViewById(R.id.check_box2);
        bothCheck = view.findViewById(R.id.check_box3);
        getVehicle();
        view.findViewById(R.id.order_btn).setOnClickListener(this);
    }

    private void getVehicle(){
        donationRequestsList  = new ArrayList<>();
        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        mVehicleLocationsList = new ArrayList<>();
        viewModel.getLiveDataVehicle().observe(getActivity(), new Observer<List<VehicleLocations>>() {
            @Override
            public void onChanged(List<VehicleLocations> vehicleLocationsList) {
                if (vehicleLocationsList != null){
                    mVehicleLocationsList.addAll(vehicleLocationsList);
                }
            }
        });
        viewModel.getLiveDataDonation().observe(getActivity(), new Observer<List<DonationRequests>>() {
            @Override
            public void onChanged(List<DonationRequests> donationRequests) {
                donationRequestsList.addAll(donationRequests);
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.order_btn: {
                String natId = nationalID.getText().toString();
                for (DonationRequests donationRequests : donationRequestsList){
                    if (donationRequests.getVehicleId().equals(natId))
                        nationalID.setError("this national id is already exist!");
                }
                if (natId.isEmpty()) {
                    nationalID.setError("please enter your national id");
                } else {
                    nationalID.setText("");
                    String checkedItem = "";
                    Bundle b = this.getArguments();
                    if (b != null) {
                        String carId = b.getString(VEHICLE_KEY);
                        if (plasmaCheck.isChecked()) {
                            checkedItem = "Plasma";
                        } else if (plateletsCheck.isChecked()) {
                            checkedItem = "Platelets";
                        } else if (bothCheck.isChecked()) {
                            checkedItem = "Both Plasma and Platelets";
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "please check donation type", Toast.LENGTH_LONG).show();
                        }
                        DocumentReference locationRef = mFireDb.collection(getString(R.string.collection_car_users))
                                .document(natId);
                        DonationRequests donationRequests = new DonationRequests();
                        User user = ((UserClient) (getActivity().getApplicationContext())).getUser();
                        donationRequests.setUser(user);
                        donationRequests.setDate(null);
                        donationRequests.setVehicleId(natId);
                        donationRequests.setCheckedItem(checkedItem);
                        for (VehicleLocations vehicleLocations : mVehicleLocationsList) {
                            if (vehicleLocations.getCar_id().equals(carId))
                                donationRequests.setVehicleLocations(vehicleLocations);
                        }
                        locationRef.set(donationRequests).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity().getApplicationContext(), "Request has sent", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }
    }
}
