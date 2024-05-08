package com.example.tuneapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView fullNameTextView, emailTextView, phoneTextView, patientName;
    private EditText editFullName, editEmail, editPhone, editPatientName;
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
        patientName = view.findViewById(R.id.patient_name);

        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        Button editButton = view.findViewById(R.id.editbutton);
        editButton.setOnClickListener(v -> {
            // Show the dialog
            showEditDialog();
        });

        // Fetch and display user information
        displayUserInfo();

        return view;
    }

    private void displayUserInfo() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = fireStore.collection("users").document(userId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fName");
                    String email = documentSnapshot.getString("email");
                    String phone = documentSnapshot.getString("phone");
                    String patient = documentSnapshot.getString("patient");

                    // Log the data
                    Log.d("SettingFragment", "Full Name: " + fullName);
                    Log.d("SettingFragment", "Email: " + email);
                    Log.d("SettingFragment", "Phone: " + phone);
                    Log.d("SettingFragment", "Patient: " + patient);

                    // Set text views
                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    phoneTextView.setText(phone);
                    patientName.setText(patient);
                } else {
                    Log.d("SettingFragment", "Document does not exist");
                }
            }).addOnFailureListener(e -> {
                Log.e("SettingFragment", "Error fetching document", e);
            });
        }
    }

    private void showEditDialog() {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        // Inflate the layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.edit_info_dialog, null);
        builder.setView(dialogView);

        // Initialize EditText fields
        editFullName = dialogView.findViewById(R.id.edit_full_name);
        editEmail = dialogView.findViewById(R.id.edit_email);
        editPhone = dialogView.findViewById(R.id.edit_phone);
        editPatientName = dialogView.findViewById(R.id.edit_patient_name);

        // Fetch current information and set it to EditText fields
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = fireStore.collection("users").document(userId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("fName");
                    String email = documentSnapshot.getString("email");
                    String phone = documentSnapshot.getString("phone");
                    String patient = documentSnapshot.getString("patient");

                    // Set current information to EditText fields
                    editFullName.setText(fullName);
                    editEmail.setText(email);
                    editPhone.setText(phone);
                    editPatientName.setText(patient);
                } else {
                    Log.d("SettingFragment", "Document does not exist");
                }
            }).addOnFailureListener(e -> {
                Log.e("SettingFragment", "Error fetching document", e);
            });
        }

        // Set dialog title
        builder.setTitle("Edit Information");

        // Set positive button for Save action
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Get edited information from EditText fields
            String newFullName = editFullName.getText().toString();
            String newEmail = editEmail.getText().toString();
            String newPhone = editPhone.getText().toString();
            String newPatientName = editPatientName.getText().toString();

            // Update TextViews with new information
            fullNameTextView.setText(newFullName);
            emailTextView.setText(newEmail);
            phoneTextView.setText(newPhone);
            patientName.setText(newPatientName);

            // Update information in Firebase
            FirebaseUser currentUser1 = fAuth.getCurrentUser();
            if (currentUser1 != null) {
                String userId = currentUser1.getUid();
                DocumentReference docRef = fireStore.collection("users").document(userId);
                docRef.update(
                                "fName", newFullName,
                                "email", newEmail,
                                "phone", newPhone,
                                "patient", newPatientName
                        ).addOnSuccessListener(aVoid -> Log.d("SettingFragment", "DocumentSnapshot successfully updated!"))
                        .addOnFailureListener(e -> Log.e("SettingFragment", "Error updating document", e));
            }
        });

        // Set negative button for Cancel action
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Dismiss dialog
            dialog.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
