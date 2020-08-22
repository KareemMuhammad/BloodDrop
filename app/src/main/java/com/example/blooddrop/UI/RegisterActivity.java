package com.example.blooddrop.UI;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddrop.Models.User;
import com.example.blooddrop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity  {

    private Spinner mSpinner;
    private FirebaseFirestore mFDb;
    private FirebaseAuth mAuth;
    private EditText emailTxt ,passwordTxt ,addressTxt ,phone_no_txt ,confirmPassTxt ,usernameTxt;
    private String email ,password ,username ,address ,confirm_pass , blood_type;
    private long phone_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.blood,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        hideSoftKeyboard();
    }
    private void initializeView(){
        mFDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mSpinner = findViewById(R.id.spinner);
        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
        addressTxt = findViewById(R.id.address);
        phone_no_txt = findViewById(R.id.phone_number);
        confirmPassTxt = findViewById(R.id.confirm_pass);
        usernameTxt = findViewById(R.id.user_name);
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void registerNewAccount(View view) {
        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();
        username = usernameTxt.getText().toString();
        address = addressTxt.getText().toString();
        confirm_pass = confirmPassTxt.getText().toString();
        phone_no = Long.parseLong(phone_no_txt.getText().toString());
        blood_type = mSpinner.getSelectedItem().toString();
        if (email.isEmpty()){
            emailTxt.setError(getString(R.string.fill_fields));
        }else if (password.isEmpty()){
            passwordTxt.setError(getString(R.string.fill_fields));
        }else if(username.isEmpty()){
            usernameTxt.setError(getString(R.string.fill_fields));
        }else if (address.isEmpty()){
            addressTxt.setError(getString(R.string.fill_fields));
        }else if (!confirm_pass.isEmpty()){
            confirmPassTxt.setError(getString(R.string.fill_fields));
        }else if (confirm_pass.matches(password)){
            confirmPassTxt.setError(getString(R.string.pass_not_match));
        }else if (phone_no == -1){
            phone_no_txt.setError(getString(R.string.fill_fields));
        }else if (blood_type.isEmpty()){
            Toast.makeText(getApplicationContext(), "please enter blood type", Toast.LENGTH_LONG).show();
        }else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference userDocRef = mFDb.collection(getString(R.string.collection_users))
                                .document(userId);
                        User user = new User();
                        user.setUser_id(userId);
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setFull_name(username);
                        user.setAddress(address);
                        user.setPhone_no(phone_no);
                        user.setBlood_type(blood_type);
                        user.setPoints(0);
                        userDocRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    makeSnackBar(getString(R.string.successful_registration));
                                } else {
                                    makeSnackBar(getString(R.string.failed_registration));
                                }
                            }
                        });
                    } else {
                        makeSnackBar(getString(R.string.failed_registration));
                    }
                }
            });
        }
    }

    public void makeSnackBar(String msg) {
        View v = findViewById(android.R.id.content);
        Snackbar.make(v,msg, Snackbar.LENGTH_LONG)
                .setAction(null, null).show();
    }

}
