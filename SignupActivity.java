package com.example.juwapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.juwapp.R;
import com.example.juwapp.helperClases.Storingdata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    EditText userID, userName, email, userpass;
    AutoCompleteTextView batch, depart;
    String[] bt, department;
    CheckBox isTecher, isStudent;
    Button btnSignup;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    boolean selectionControl = false;
    boolean isAllFieldsChecked = false;
    String userID_, userName_, batch_, depart_, email_, password, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        userID = (EditText) findViewById(R.id.userID);
        userName = (EditText) findViewById(R.id.userName);
        batch = (AutoCompleteTextView) findViewById(R.id.batch);
        bt = getResources().getStringArray(R.array.batch);
        department = getResources().getStringArray(R.array.depart);
        depart = (AutoCompleteTextView) findViewById(R.id.depart);
        email = (EditText) findViewById(R.id.email);
        userpass = (EditText) findViewById(R.id.userpass);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        isTecher = (CheckBox) findViewById(R.id.isTeacher);
        isStudent = (CheckBox) findViewById(R.id.isStudent);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("credentials");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignupActivity.this, R.layout.spinner_layout, bt);
        ArrayAdapter<String> adapterd = new ArrayAdapter<>(SignupActivity.this, R.layout.spinner_layout, department);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batch.setAdapter(adapter);
        depart.setAdapter(adapterd);


        checkboxchecked();

        isStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType = "Student";
            }
        });

        isTecher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType = "Teacher";
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectionControl = isAllFieldsChecked;

                isAllFieldsChecked = CheckAllFields();

                final String emailid = email.getText().toString();

                if (!isValidEmail(emailid)) {
                    email.setError("Please Enter a Valid Email");
                }

                // the boolean variable turns to be true then
                // only the user must be proceed to the activity2
                if (isAllFieldsChecked) {

                }

            }
        });

    }

    private void checkboxchecked() {
        userType = isStudent && isTecher;
    }

    private boolean isValidEmail(String emailid) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern p = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = p.matcher(emailid);
        return matcher.matches();
    }

    private boolean CheckAllFields() {

         userID_ = userID.getText().toString();
         userName_ = userName.getText().toString().toUpperCase();
         batch_ = batch.getText().toString();
         depart_ = depart.getText().toString();
         email_ = email.getText().toString();
         password = userpass.getText().toString();



        if (userID_.length() < 6) {
            userID.setError("This field is required");
            return false;
        }
        if (userName_.length() == 0) {
            userName.setError("This field is required");
            return false;
        }
        if (batch_.length() == 0) {
            batch.setError("Select Batch");
            return false;
        }

        if (depart_.length() == 0) {
            depart.setError("Select Department");
            return false;
        }

        if (email_.length() == 0) {
            email.setError("This field is required");
            return false;
        }

        if (password.length() == 0) {
            userpass.setError("Password is required, It must contain mix of Upper and lower case letters as well as digits and one special character(4-20)");
            return false;
        } else if (password.length() < 8) {
            userpass.setError("Password must be minimum 8 characters");
            return false;
        } else if (!password.matches(".*[A-Z].*")) {
            userpass.setError("Must Contain 1 Upper-case Character");
            return false;
        } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$") ||
                !password.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*+=?-]).{8,15}$")) {
//not having special characters error message
            userpass.setError("Password must have special characters and digits(4-20)");
            return false;
        }
        if (!isTecher.isChecked() && !isStudent.isChecked()){
            Toast.makeText(this, "Select the account type.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // after all validation return true.

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email_, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Storingdata user = new Storingdata (userID_, userName_, batch_, depart_, email_, password, userType);
                    FirebaseDatabase.getInstance().getReference("Credentials")
                            .child(depart_).child(userID_)
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        sendUserTokenActivity();
                                        Toast.makeText(SignupActivity.this,"Registration Successful", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(SignupActivity.this,"Failed to Register! Try again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }

                                private void sendUserTokenActivity() {
                                    Intent i = new Intent(SignupActivity.this, DashboardActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            });

                }else{
                    Toast.makeText(SignupActivity.this,"Failed to Register! Try again!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        return true;
    }

    public void Login(View view) {
        Intent i = new Intent(SignupActivity.this, Login.class);
        startActivity(i);
    }
}