package com.example.blooddrop.UI;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.blooddrop.Data.UserViewModel;
import com.example.blooddrop.Models.ClusterMarkers;
import com.example.blooddrop.Models.UserLocations;
import com.example.blooddrop.Models.VehicleLocations;
import com.example.blooddrop.R;
import com.example.blooddrop.Util.ClusterMarkersRenderer;
import com.example.blooddrop.Util.ViewWeightAnimationWrapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback,View.OnClickListener {

    public MapFragment() {

    }

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final String MAP_VIEW_KEY = "MapViewBundleKey";
    private static final String VEHICLE_KEY = "vehicle key";
    private int mapState = 0;
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private RelativeLayout mMapContainer;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBounds;
    private UserLocations mUserPosition ;
    private List<UserLocations> mUserLocationsList ;
    private List<VehicleLocations> mVehicleLocationsList ;
    private FirebaseAuth mAuth;
    private ArrayList<ClusterMarkers> mClMarkList;
    private ClusterManager mClMarkManager;
    private ClusterMarkersRenderer mClMarkRender;
    private ImageButton extendbtn;
    private LinearLayoutCompat mButtonsContainer;
    private TextView cityTxt;
    private TextView phoneTxt;
    private TextView criticalTxt;
    private TextView departureTxt;
    private UserViewModel viewModel;
    private String id;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.donationMap);
        mAuth = FirebaseAuth.getInstance();
        extendbtn = view.findViewById(R.id.extendBtn);
        view.findViewById(R.id.extendBtn).setOnClickListener(this);
        mMapContainer = view.findViewById(R.id.map_container);
        mButtonsContainer = view.findViewById(R.id.buttonsContainer);

        cityTxt = view.findViewById(R.id.cityMap);
        departureTxt = view.findViewById(R.id.departureMap);
        phoneTxt = view.findViewById(R.id.phoneMap);
        criticalTxt = view.findViewById(R.id.criticalMap);
        view.findViewById(R.id.reserve_btn).setOnClickListener(this);
        view.findViewById(R.id.call_btn).setOnClickListener(this);
        initiateGoogleMap(savedInstanceState);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mClMarkList = new ArrayList<>();
        mUserLocationsList = new ArrayList<>();
        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        viewModel.getLiveDataLocation().observe(getActivity(), new Observer<List<UserLocations>>() {
            @Override
            public void onChanged(List<UserLocations> userLocations) {
                if (userLocations != null) {
                    mUserLocationsList.addAll(userLocations);
                }
            }
        });
        mVehicleLocationsList = new ArrayList<>();
        viewModel.getLiveDataVehicle().observe(getActivity(), new Observer<List<VehicleLocations>>() {
            @Override
            public void onChanged(List<VehicleLocations> vehicleLocationsList) {
                if (vehicleLocationsList != null){
                    mVehicleLocationsList.addAll(vehicleLocationsList);
                }
            }
        });
    }



    private void setMapView(){
        //map view window , area = 0.2 * 0.2 = 0.4
        setUserPosition();
        double bottomBounds = mUserPosition.getGeoPoint().getLatitude() - 0.1;
        double leftBounds = mUserPosition.getGeoPoint().getLongitude() - 0.1;
        double topBounds = mUserPosition.getGeoPoint().getLatitude() + 0.1;
        double rightBounds = mUserPosition.getGeoPoint().getLongitude() + 0.1;
        mMapBounds = new LatLngBounds(new LatLng(bottomBounds,leftBounds),
                new LatLng(topBounds,rightBounds));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBounds,0));
    }

    private void addMapMarkers(){
        if (mGoogleMap != null){
            if(mClMarkManager == null){
                mClMarkManager = new ClusterManager<ClusterMarkers>(requireActivity().getApplicationContext(), mGoogleMap);
            }
            if(mClMarkRender == null){
                mClMarkRender = new ClusterMarkersRenderer(requireActivity(), mGoogleMap, mClMarkManager);
                mClMarkManager.setRenderer(mClMarkRender);
            }
            for (VehicleLocations userLocations : mVehicleLocationsList){
                try{
                String snippet = "Blood Vehicle";
                 int icon = R.drawable.blood_transfusion;
                ClusterMarkers newClusterMarker = new ClusterMarkers(
                        new LatLng(userLocations.getGeoPoint().getLatitude(), userLocations.getGeoPoint().getLongitude()),
                        userLocations.getOrganization(),
                        snippet,
                        icon,
                        userLocations
                );
                mClMarkManager.addItem(newClusterMarker);
                mClMarkList.add(newClusterMarker);
            }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }
            }
            mClMarkManager.cluster();
        }
        setMapView();
    }


    private void setUserPosition(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireContext());

        for (UserLocations userLocations : mUserLocationsList) {
            if (userLocations.getUser().getUser_id().equals(mAuth.getCurrentUser().getUid()) || userLocations.getUser().getUser_id().equals(acct.getId()) ) {
                mUserPosition = userLocations;
            }
        }
    }

    private void initiateGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            mGoogleMap = map;
            mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    addMapMarkers();
                }
            });
            mClMarkManager = new ClusterManager<ClusterMarkers>(requireContext(), mGoogleMap);
            mClMarkManager.setRenderer(new ClusterMarkersRenderer(requireContext(), mGoogleMap, mClMarkManager));

            mGoogleMap.setOnMarkerClickListener(mClMarkManager);

            mClMarkManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterMarkers>() {
                @Override
                public boolean onClusterClick(Cluster<ClusterMarkers> cluster) {
                    Log.d(TAG, "Cluster which consumes whole list of ClusterItems");
                    return false;
                }
            });

            mClMarkManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterMarkers>() {
                @Override
                public boolean onClusterItemClick(ClusterMarkers item) {
                    Log.d(TAG, "Cluster Item");
                   contractMapAnimation();
                   id = item.getUser().getCar_id();
                   String city = item.getUser().getCity(), critical = item.getUser().getCritical_blood_type();
                   long phone = item.getUser().getPhone_no() ;
                   String time = item.getUser().getDeparture_date().getHours()
                           + ":" +item.getUser().getDeparture_date().getMinutes();
                   cityTxt.setText(city);
                   criticalTxt.setText(critical);
                   phoneTxt.setText(String.valueOf(phone));
                   departureTxt.setText(time);
                        return false;
                }
            });
        }
    }

    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);


        ViewWeightAnimationWrapper buttonsAnimationWrapper = new ViewWeightAnimationWrapper(mButtonsContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(buttonsAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper buttonsAnimationWrapper = new ViewWeightAnimationWrapper(mButtonsContainer);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(buttonsAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.extendBtn:
            if (mapState == MAP_LAYOUT_STATE_CONTRACTED) {
                mapState = MAP_LAYOUT_STATE_EXPANDED;
                Log.d(TAG, "extend");
                expandMapAnimation();
            } else if (mapState == MAP_LAYOUT_STATE_EXPANDED) {
                mapState = MAP_LAYOUT_STATE_CONTRACTED;
                Log.d(TAG, "contract");
                contractMapAnimation();
            }
            break;
            case R.id.reserve_btn: {
                Log.d(TAG,"reserve btn");
                Bundle bundle = new Bundle();
                bundle.putString(VEHICLE_KEY,id);
                ReservationFragment reservationFragment = new ReservationFragment();
                reservationFragment.setArguments(bundle);
                reservationFragment.show(getActivity().getSupportFragmentManager(),"DialogFragment");
            }
            break;
            case R.id.call_btn:{
                String phoneNumber = String.format("tel: %s",
                        phoneTxt.getText().toString());
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse(phoneNumber));
                if (dialIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(dialIntent);
                } else {
                    Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
                }
            }
            break;
        }
    }
}