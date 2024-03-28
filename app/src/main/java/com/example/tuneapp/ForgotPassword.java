package com.example.tuneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private ImageButton buttonBfor;
    private EditText forgotPasswordEditText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        buttonBfor = findViewById(R.id.buttonBfor);
        buttonBfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        forgotPasswordEditText = findViewById(R.id.forgotPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.forgotPassButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgotPasswordEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    forgotPasswordEditText.setError("Email is required");
                    forgotPasswordEditText.requestFocus();
                    return;
                }

                // Send password reset email
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPassword.this, "Password reset email sent. Check your email.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgotPassword.this, "Failed to send reset email. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    public void openMainActivity() {
        finish(); // Close the current activity
    }
}
