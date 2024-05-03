package com.example.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class login_activity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        TextView signupRedirectText = findViewById(R.id.signupRedirectText);

        // Check if user is already authenticated
        if (auth.getCurrentUser() != null) {
            // User is already logged in, proceed to main activity
            startActivity(new Intent(login_activity.this, navbar.class));
            finish(); // Close the login activity
        }

        loginButton.setOnClickListener(view -> {
            String email = loginEmail.getText().toString();
            String pass = loginPassword.getText().toString();
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!pass.isEmpty()) {
                    auth.signInWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(authResult -> {
                                Toast.makeText(login_activity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login_activity.this, navbar.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(login_activity.this, "Login Failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                            });
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
    }

}