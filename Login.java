package com.example.juwapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.juwapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;


public class Login extends AppCompatActivity {
    EditText userID, userPass;
    Button btnLogin;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ProgressBar progressBar;

    String userID_, password, depart_;


    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userID = (EditText) findViewById(R.id.userID);
        userPass = (EditText) findViewById(R.id.userpass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Credentials");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUserId() & !validateUserPass()) {

                } else checkUser();

            }
        });
    }

    private Boolean validateUserId() {
        userID_ = userID.getText().toString();
        if (userID_.isEmpty()) {
            userID.setError("Field cannot be empty");
            return false;
        } else {
            userID.setError(null);
            return true;
        }
    }

    private Boolean validateUserPass() {
        password = userPass.getText().toString();
        if (password.isEmpty()) {
            userPass.setError("Password cannot be empty");
            return false;
        } else if (password.length() == 0) {
            userPass.setError("Password is required, It must contain mix of Upper and lower case letters as well as digits and one special character(4-20)");
            return false;
        } else if (password.length() < 8) {
            userPass.setError("Password must be minimum 8 characters");
            return false;
        } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$") ||
                !password.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*+=?-]).{8,15}$")) {
//not having special characters error message
            userPass.setError("Password must have special characters and digits(4-20)");
            return false;
        }
        progressBar.setVisibility(View.VISIBLE);
        // after all validation return true..
        return true;
    }

    private void checkUser() {
        if (!userID_.isEmpty()) {

            userID_ = userID.getText().toString().trim();
            password = userPass.getText().toString().trim();

            databaseReference.child(userID_).addListenerForSingleValueEvent(listener);

        } else {
            Toast.makeText(this, "Enter a correct User ID", Toast.LENGTH_SHORT).show();
        }
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()){
                String userid = snapshot.child("userID_").getValue(String.class);
                String pass = snapshot.child("password").getValue(String.class);
                if (userid.equals(userID_) && pass.equals(password)){
                    Intent i = new Intent(Login.this,DashboardActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(Login.this, "Please Enter Valid Credentials..", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(Login.this, "User ID not Found.!", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

            Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();

        }
    };

    public void signup(View view) {
        Intent i = new Intent(Login.this, SignupActivity.class);
        startActivity(i);
    }
}