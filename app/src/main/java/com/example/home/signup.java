package com.example.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class signup extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail,signupPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth= FirebaseAuth.getInstance();
        signupEmail=findViewById(R.id.signup_email);
        signupPassword=findViewById(R.id.signup_password);
        Button signupButton = findViewById(R.id.signup_button);
        TextView loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(view -> {
            String user= signupEmail.getText().toString().trim();
            String pass = signupPassword.getText().toString().trim();

            if(user.isEmpty())
            {
                signupEmail.setError("Email Cannot be Empty");
            }
            if(pass.isEmpty()){
                signupPassword.setError("Password Cannot be Empty");
            }
            else{
                auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(signup.this,"SignUp Successful",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(signup.this, com.example.home.login_activity.class));
                    }
                    else {
                        Toast.makeText(signup.this,"SignUp Failed"+ Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        loginRedirectText.setOnClickListener(view -> startActivity(new Intent(signup.this, com.example.home.login_activity.class)));
    }
}