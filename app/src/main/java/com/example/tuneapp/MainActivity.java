package com.example.tuneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private Button button; // Button declaration
    private TextView registerButton; // TextView declaration for registering
    private  TextView forgotPasswordButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button); // Button initialization
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogInPage();
            }
        });

        registerButton = findViewById(R.id.registerButton); // TextView initialization for registering
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpPage();
            }
        });

        forgotPasswordButton = findViewById(R.id.forgotPasswordButton); // TextView initialization for registering
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPassword();
            }
        });


    }

    public void openLogInPage() {
        Intent intent = new Intent(this, LogInPage.class);
        startActivity(intent);
    }

    public void openSignUpPage() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void openForgotPassword() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

}
