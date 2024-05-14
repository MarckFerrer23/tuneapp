package com.example.tuneapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SettingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView fullNameTextView, emailTextView, phoneTextView, patientName;
    private EditText editFullName, editEmail, editPhone, editPatientName;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;

    private ImageView profileImage;

    private ImageButton changeProfileImage;

    private StorageReference storageReference;
    private StorageReference profileRef;

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
        profileImage = view.findViewById(R.id.profile_image);
        changeProfileImage = view.findViewById(R.id.edit_profile_image);

        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        profileRef = storageReference.child("profile.jpg");

        Button editButton = view.findViewById(R.id.editbutton);
        editButton.setOnClickListener(v -> showEditDialog());

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
                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                    // Set text views
                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    phoneTextView.setText(phone);
                    patientName.setText(patient);

                    // Load profile image using Glide or any other image loading library
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.profile_image); // default image if no URL
                    }
                } else {
                    Log.d("SettingFragment", "Document does not exist");
                }
            }).addOnFailureListener(e -> {
                Log.e("SettingFragment", "Error fetching document", e);
            });

            changeProfileImage.setOnClickListener(v -> {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Save the image URL to Firestore
                saveImageUrlToFirestore(uri.toString());
                // Load the image using Picasso or Glide
                Picasso.get().load(uri).into(profileImage);
            });
        }).addOnFailureListener(e -> {
            Log.e("SettingFragment", "Error uploading image", e);
            Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveImageUrlToFirestore(String url) {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = fireStore.collection("users").document(userId);
            docRef.update("profileImageUrl", url)
                    .addOnSuccessListener(aVoid -> Log.d("SettingFragment", "Profile image URL saved successfully"))
                    .addOnFailureListener(e -> Log.e("SettingFragment", "Error saving profile image URL", e));
        }
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.edit_info_dialog, null);
        builder.setView(dialogView);

        editFullName = dialogView.findViewById(R.id.edit_full_name);
        editEmail = dialogView.findViewById(R.id.edit_email);
        editPhone = dialogView.findViewById(R.id.edit_phone);
        editPatientName = dialogView.findViewById(R.id.edit_patient_name);

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

                    editFullName.setText(fullName);
                    editEmail.setText(email);
                    editPhone.setText(phone);
                    editPatientName.setText(patient);
                } else {
                    Log.d("SettingFragment", "Document does not exist");
                }
            }).addOnFailureListener(e -> Log.e("SettingFragment", "Error fetching document", e));
        }

        builder.setTitle("Edit Information");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newFullName = editFullName.getText().toString();
            String newEmail = editEmail.getText().toString();
            String newPhone = editPhone.getText().toString();
            String newPatientName = editPatientName.getText().toString();

            fullNameTextView.setText(newFullName);
            emailTextView.setText(newEmail);
            phoneTextView.setText(newPhone);
            patientName.setText(newPatientName);

            if (currentUser != null) {
                String userId = currentUser.getUid();
                DocumentReference docRef = fireStore.collection("users").document(userId);
                docRef.update("fName", newFullName, "email", newEmail, "phone", newPhone, "patient", newPatientName)
                        .addOnSuccessListener(aVoid -> Log.d("SettingFragment", "DocumentSnapshot successfully updated!"))
                        .addOnFailureListener(e -> Log.e("SettingFragment", "Error updating document", e));
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

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
