package com.example.blooddrop.UI;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.blooddrop.Data.UserViewModel;
import com.example.blooddrop.Models.User;
import com.example.blooddrop.Models.UserLocations;
import com.example.blooddrop.R;
import com.example.blooddrop.UserClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 2000;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2001;
    private static final int ERROR_DIALOG_REQUEST = 2002;
    private static final int RC_SIGN_IN = 2003 ;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFuseLocationClient;
    private FirebaseFirestore mFireDb;
    private UserLocations mUserLocations;
    private FirebaseAuth mAuth;
    private Button mLoginBtn;
    private TextView mRegisterTxt;
    private TextView mLogByGoogleTxt;
    private EditText mUserName;
    private EditText mPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mFireDb = FirebaseFirestore.getInstance();
        mFuseLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLoginBtn = findViewById(R.id.btn_login);
        mRegisterTxt = findViewById(R.id.register_text);
        mLogByGoogleTxt = findViewById(R.id.login_google);
        mUserName = findViewById(R.id.name_login);
        mPassword = findViewById(R.id.password_login);
        mRegisterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        googleSignInConfigure();
        hideSoftKeyboard();
    }

    //location permission setup
    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            makeToast("you can't make map request");
        }
        return false;
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
        builder.setMessage(R.string.gps_alert_dialog)
                .setCancelable(false)
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getUserDetails();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getUserDetails();

                } else {
                    getLocationPermission();
                }
            }
            case RC_SIGN_IN: {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        firebaseAuthWithGoogle(account);
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
            }
        }
    }

    //user location setup
    private void getUserDetails(){
        if(mUserLocations == null){
            mUserLocations = new UserLocations();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                DocumentReference userRef = mFireDb.collection(getString(R.string.collection_users))
                        .document(currentUser.getUid());

                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: successfully set the user client.");
                            User user = task.getResult().toObject(User.class);
                            mUserLocations.setUser(user);
                            getLastKnownLocation();
                        }
                    }
                });
            }
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                DocumentReference userRef = mFireDb.collection(getString(R.string.collection_users))
                        .document(acct.getId());

                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: successfully set the user client.");
                            User user = task.getResult().toObject(User.class);
                            mUserLocations.setUser(user);
                            getLastKnownLocation();
                        }
                    }
                });
            }
            }
        else{
            getLastKnownLocation();
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG,"get last known location active");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mFuseLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, " " + geoPoint.getLatitude() + " " + geoPoint.getLongitude());
                        mUserLocations.setGeoPoint(geoPoint);
                        mUserLocations.setDate(null);
                        saveUserLocation();
                    }
                }

            }
        });
    }
    private void saveUserLocation(){
        if (mUserLocations != null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                DocumentReference locationRef = mFireDb.collection(getString(R.string.collection_user_locations))
                        .document(currentUser.getUid());
                locationRef.set(mUserLocations).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "user location inserted to Db " + "\n" + mUserLocations.getGeoPoint().getLatitude()
                                    + " " + mUserLocations.getGeoPoint().getLongitude());
                        }
                    }
                });
            }
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                DocumentReference locationRef = mFireDb.collection(getString(R.string.collection_user_locations))
                        .document(acct.getId());
                locationRef.set(mUserLocations).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "user location inserted to Db " + "\n" + mUserLocations.getGeoPoint().getLatitude()
                                    + " " + mUserLocations.getGeoPoint().getLongitude());
                        }
                    }
                });
            }
        }
    }

    //Firebase Setup
    private void getUserDocument(){
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userArrayList) {
                for (User u : userArrayList) {
                    if (u.getUser_id().equals(mAuth.getCurrentUser().getUid()))
                       ((UserClient)(getApplicationContext())).setUser(u);
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void googleSignIn(View view){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleSignInConfigure(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

    }

    public void userSignIn(View view){
                final String userName = mUserName.getText().toString();
                final String password = mPassword.getText().toString();
                if (!userName.equals("") && !password.equals("")) {
                    mAuth.signInWithEmailAndPassword(userName, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null) {
                                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                            .setTimestampsInSnapshotsEnabled(true)
                                            .build();
                                    mFireDb.setFirestoreSettings(settings);
                                   getUserDocument();
                                }else {
                                    makeToast(getString(R.string.null_user));
                                }
                            }else {
                                makeToast(getString(R.string.failed_sign_in));
                            }
                        }
                    });
                }else{
                    makeToast(getString(R.string.incomplete_info));
                }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                        .setTimestampsInSnapshotsEnabled(true)
                                        .build();
                                mFireDb.setFirestoreSettings(settings);
                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                makeToast(getString(R.string.null_user));
                            }
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            makeToast(getString(R.string.failed_sign_in));
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkMapServices()){
            if(mLocationPermissionGranted){
                getUserDetails();
            }
            else{
                getLocationPermission();
            }
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                getUserDocument();
                makeToast(getString(R.string.successful_sign_in));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void makeToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
