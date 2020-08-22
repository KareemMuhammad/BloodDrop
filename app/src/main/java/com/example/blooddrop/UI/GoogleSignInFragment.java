package com.example.blooddrop.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.blooddrop.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GoogleSignInFragment extends DialogFragment {

    public GoogleSignInFragment(){

    }

    private Spinner mSpinner;
    private FirebaseFirestore mFDb;
    private FirebaseAuth mAuth;
    private EditText addressTxt ,phone_no_txt;
    private String address , blood_type;
    private long phone_no;
    private Button regB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.google_fragment,container,false);
        mSpinner = view.findViewById(R.id.spinner_google);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),R.array.blood,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mFDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        addressTxt = view.findViewById(R.id.address_google);
        phone_no_txt = view.findViewById(R.id.phone_number_google);
        regB = view.findViewById(R.id.register_btn_google);
        regB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mFDb = FirebaseFirestore.getInstance();
        return view;
    }
    public void createUser(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personId = acct.getId();
            DocumentReference userDocRef = mFDb.collection(getString(R.string.collection_users))
                    .document(personId);
            address = addressTxt.getText().toString();
            phone_no = Long.parseLong(phone_no_txt.getText().toString());
            blood_type = mSpinner.getSelectedItem().toString();
            if (address.isEmpty()) {
                addressTxt.setError(getString(R.string.fill_fields));
            } else if (phone_no == -1) {
                phone_no_txt.setError(getString(R.string.fill_fields));
            } else if (blood_type.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "please enter blood type", Toast.LENGTH_LONG).show();
            } else {
                userDocRef.update("address", address).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                userDocRef.update("phone_no", phone_no).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                userDocRef.update("blood_type", mSpinner.getSelectedItem()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity().getApplicationContext(), "profile updated", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
