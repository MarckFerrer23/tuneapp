package com.example.tuneapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView fullNameTextView, emailTextView, phoneTextView;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        fullNameTextView = view.findViewById(R.id.user_name);
        emailTextView = view.findViewById(R.id.user_emailAddress);
        phoneTextView = view.findViewById(R.id.user_phone_number);

        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = fireStore.collection("users").document(userId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fName");
                    String email = documentSnapshot.getString("email");
                    String phone = documentSnapshot.getString("phone");

                    // Log the data
                    Log.d("SettingFragment", "Full Name: " + fullName);
                    Log.d("SettingFragment", "Email: " + email);
                    Log.d("SettingFragment", "Phone: " + phone);

                    // Set text views
                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    phoneTextView.setText(phone);
                } else {
                    Log.d("SettingFragment", "Document does not exist");
                }
            }).addOnFailureListener(e -> {
                Log.e("SettingFragment", "Error fetching document", e);
            });
        }

        return view;
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
}
