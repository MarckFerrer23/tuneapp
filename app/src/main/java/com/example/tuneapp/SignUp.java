package com.example.tuneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName,mEmail,mPassword,mPhone;
    Button mRegisterBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    private ImageButton buttonBup;
    private Text registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFullName = findViewById(R.id.EditTextName);
        mEmail = findViewById(R.id.editTextTextEmailAddress);
        mPassword = findViewById(R.id.editTextTextPassword);
        mPhone = findViewById(R.id.editTextPhone);
        mRegisterBtn= findViewById(R.id.buttonReg);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity2.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String name = mFullName.getText().toString();
                String phoneNumber = mPhone.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }
                if (!isEmailValid(email)) {
                    mEmail.setError("Invalid email format. Please enter a valid Gmail address.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }
                if (!isPasswordValid(password)) {
                    mPassword.setError("Password must contain at least one special character and one uppercase letter.");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    mFullName.setError("Name is Required.");
                    return;
                }
                if (!isNameValid(name)) {
                    mFullName.setError("Please input your valid FullName");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    mPhone.setError("Phone number is Required.");
                    return;
                }
                if (!isPhoneNumberValid(phoneNumber)) {
                    mPhone.setError("Invalid phone number format. Please enter an 11-digit number starting with 09.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Register the user in Firebase
                fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();
                                    userID = fAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fStore.collection("users").document(userID);
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("fName",name);
                                    user.put("email",email);
                                    user.put("phone",phoneNumber);
                                    documentReference.set(user).addOnSuccessListener((OnSuccessListener)(aVoid) -> {
                                          Log.d(TAG, "onSucccess: usr Profile is created for" + userID);
                                         }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                        }
                                    });

                                    startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                                } else {
                                    Toast.makeText(SignUp.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }

            private boolean isEmailValid(String email) {
                // Check if the email has the format *@gmail.com
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@gmail.com");
            }

            private boolean isPasswordValid(String password) {
                // Password must contain at least one special character and one uppercase letter
                String specialChars = "[!@#$%^&*()_+=|<>?{}\\[\\]~-]";
                return password.matches(".*" + specialChars + ".*") && !password.equals(password.toLowerCase());
            }

            private boolean isNameValid(String name) {
                // Name must not contain special characters except for the period '.' and must start with a capital letter
                return name.matches("[A-Z][a-zA-Z\\.\\s]*");
            }

            private boolean isPhoneNumberValid(String phoneNumber) {
                // Phone number must be exactly 11 digits and start with "09"
                return phoneNumber.matches("^09\\d{9}$");
            }




        });
        buttonBup = findViewById(R.id.buttonBup);
        buttonBup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}