package com.example.blooddrop.UI;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.blooddrop.Data.UserViewModel;
import com.example.blooddrop.Models.User;
import com.example.blooddrop.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener{
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private CircleImageView circleImageView;
    private static final int PICK_IMAGE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private TextView emailTxt;
    private TextView nameTxt;
    private TextView phoneTxt;
    private TextView addressTxt;
    private TextView bloodTxt;
    private TextView updateDonationBtn;
    private TextView dateView;
    private int day , month , year;
    private TextView lastDonationTxt;
    private TextView profileNameTxt;
    private TextView pointTxt;
    private StorageReference storageReference;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFDb;
    private ImageButton imageButton;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImagesStore").child("ImageFolder");
        circleImageView = view.findViewById(R.id.circle_image);
        imageButton = view.findViewById(R.id.editProfile);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInFragment googleSignInFragment = new GoogleSignInFragment();
                googleSignInFragment.show(getActivity().getSupportFragmentManager(),"google fragment");
            }
        });
         updateDonationBtn = view.findViewById(R.id.edit_update);
         profileNameTxt = view.findViewById(R.id.name_nav_view);
         dateView = view.findViewById(R.id.date_text);
         mAuth = FirebaseAuth.getInstance();
         mFDb = FirebaseFirestore.getInstance();
        view.findViewById(R.id.edit_update).setOnClickListener(this);
        emailTxt= view.findViewById(R.id.email_des);
        pointTxt = view.findViewById(R.id.points_des);
        nameTxt= view.findViewById(R.id.name_des);
        phoneTxt = view.findViewById(R.id.phone_des);
        addressTxt= view.findViewById(R.id.Address_des);
        bloodTxt= view.findViewById(R.id.text_type);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
                {
                    if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permission,PERMISSION_CODE);
                    }
                    else {
                        pickImageFromGallery();
                    }
                }
                else {
                    pickImageFromGallery();
                }
            }
        });
        if (mAuth.getCurrentUser() != null){
            getUser();
        }else {
            googleSignInConfigure();
        }
        return view ;
    }
    private void googleSignInConfigure(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            nameTxt.setText(personName);
            emailTxt.setText(personEmail);
            DocumentReference userDocRef = mFDb.collection(getString(R.string.collection_users))
                    .document(personId);
            User user = new User();
            user.setUser_id(personId);
            user.setEmail(personEmail);
            user.setFull_name(personName);
            userDocRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getActivity().getApplicationContext(),getString(R.string.successful_registration),Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity().getApplicationContext(),getString(R.string.failed_registration),Toast.LENGTH_LONG).show();
                    }
                }
            });
            Glide.with(requireContext())
                    .load(personPhoto)
                    .placeholder(R.drawable.ic_baseline_loop_24)
                    .into(circleImageView);
            if (addressTxt.getText().toString().isEmpty()) {
                GoogleSignInFragment googleSignInFragment = new GoogleSignInFragment();
                googleSignInFragment.show(getActivity().getSupportFragmentManager(), "google fragment");
                getUser();
            }
        }
    }

    private void getUser() {
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getLiveData().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userArrayList) {
                for (User user : userArrayList) {
                    if (user.getUser_id().equals(mAuth.getCurrentUser().getUid()) || user.getUser_id().equals(acct.getId())) {
                        Log.d(TAG,acct.getId());
                        String email = user.getEmail();
                        long phone = user.getPhone_no();
                        String name = user.getFull_name();
                        String bloodType = user.getBlood_type();
                        String address = user.getAddress();
                        float points = user.getPoints();
                        emailTxt.setText(email);
                        nameTxt.setText(name);
                        phoneTxt.setText(String.valueOf(phone));
                        addressTxt.setText(address);
                        bloodTxt.setText(bloodType);
                        pointTxt.setText(String.valueOf(points));
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser.getPhotoUrl() != null){
                            Glide.with(requireContext())
                                    .load(firebaseUser.getPhotoUrl())
                                    .into(circleImageView);
                        }
                        readFromFile();
                    }
                }
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_CODE :
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    pickImageFromGallery();
                }
                else {
                    Toast.makeText(getActivity(), "Permission was denied .!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            Glide.with(requireContext())
                    .load(imageUri)
                    .into(circleImageView);
            if (imageUri != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final StorageReference imageRef = storageReference.child(user.getUid() + imageUri.getLastPathSegment());
                imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadedImage(imageRef);
                        Toast.makeText(getActivity().getApplicationContext(),"Uploaded",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void saveProfileImage(Uri imageUri){
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri);
       UserProfileChangeRequest request = builder.build();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"image downloaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"image failed");
            }
        });
    }
    private void getDownloadedImage(StorageReference storageReference){
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                saveProfileImage(uri);
            }
        });
    }
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        dateView.setText(new StringBuilder().append(i).append("/")
                .append(i1+1).append("/").append(i2));
        saveToFile();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_update :{
                Calendar c  = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),R.style.Theme_AppCompat_DayNight_Dialog_Alert,ProfileFragment.this,day,month,year);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        }
    }
    public void saveToFile()
    {
        try
        {
            FileOutputStream fout = getActivity().getApplicationContext().openFileOutput("donation_date.txt",MODE_PRIVATE);
            String content = dateView.getText().toString();
            byte[] b = content.getBytes();
            fout.write(b);
            fout.close();
        }catch (Exception ex)
        {
            if (ex.getMessage() != null) {
                Log.d(TAG, ex.getMessage());
            }
        }
    }

    private void readFromFile()
    {
        try {
            FileInputStream fin = getActivity().getApplicationContext().openFileInput("donation_date.txt");
            int index = 0;
            String content = "";
            while ((index = fin.read()) != -1)
            {
                content = content + (char)index;
            }
            fin.close();
            dateView.setText(content);
        }
        catch (IOException e){
            if (e.getMessage() != null) {
                Log.d(TAG, e.getMessage());
            }
        }
    }
}

