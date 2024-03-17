package com.example.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.home.navbar;
import com.example.home.signup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_activity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private Button verificationButton; // Added verification button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        verificationButton = findViewById(R.id.verification_button); // Initialize verification button
        TextView signupRedirectText = findViewById(R.id.signupRedirectText);

        loginButton.setOnClickListener(view -> {
            String email = loginEmail.getText().toString();
            String pass = loginPassword.getText().toString();
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!pass.isEmpty()) {
                    // Check if email is verified before logging in
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(authResult -> {
                                    Toast.makeText(login_activity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(login_activity.this, navbar.class));
                                    finish();
                                });
                    } else {
                        Toast.makeText(login_activity.this, "Email is not verified. Please verify your email address.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loginPassword.setError("Password cannot be Empty");
                }
            } else if (email.isEmpty()) {
                loginEmail.setError("Email cannot be Empty");
            } else {
                loginEmail.setError("Please Enter Valid Email");
            }
        });

        signupRedirectText.setOnClickListener(view -> startActivity(new Intent(login_activity.this, signup.class)));

        // Add click listener for verification button
        verificationButton.setOnClickListener(view -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(login_activity.this, "Verification email sent. Please verify your email address.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(login_activity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

