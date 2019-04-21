package com.example.charanpuli.speedy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginRegisterActivity extends AppCompatActivity {
    private Button DriverLoginBtn, DriverRegisterBtn;
    private TextView DriverStatus, DriverRegisterLink;
    private EditText DriverEmail, DriverPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;
    private DatabaseReference DriverDatabaseRef;
    private String OnlineDriverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);
        DriverLoginBtn = (Button) findViewById(R.id.login_driver_btn);
        DriverRegisterBtn = (Button) findViewById(R.id.register_driver_btn);
        DriverStatus = (TextView) findViewById(R.id.driver_status);
        DriverRegisterLink = (TextView) findViewById(R.id.driver_register_link);
        DriverRegisterBtn.setVisibility(View.INVISIBLE);
        DriverRegisterBtn.setEnabled(false);
        DriverEmail = (EditText) findViewById(R.id.email_driver);
        DriverPassword = (EditText) findViewById(R.id.password_driver);

        mAuth = FirebaseAuth.getInstance();





        LoadingBar = new ProgressDialog(this);
        DriverRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverLoginBtn.setVisibility(View.INVISIBLE);
                DriverRegisterLink.setVisibility(View.INVISIBLE);
                DriverRegisterBtn.setVisibility(View.VISIBLE);
                DriverRegisterBtn.setEnabled(true);
                DriverStatus.setText("Driver Register");
            }
        });
        DriverRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = DriverEmail.getText().toString();
                String password = DriverPassword.getText().toString();
                RegisterDriver(email, password);
            }
        });
        DriverLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = DriverEmail.getText().toString();
                String password = DriverPassword.getText().toString();
                SignInDriver(email, password);
            }
        });
    }

    private void SignInDriver(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Your Email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter Your Password...", Toast.LENGTH_SHORT).show();
        } else {
            LoadingBar.setTitle("Driver Sign In");
            LoadingBar.setMessage("Please Wait ...While we are checking your credentials");
            LoadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Intent intent=new Intent(DriverLoginRegisterActivity.this,DriversMapsActivity.class);
                        startActivity(intent);

                        Toast.makeText(DriverLoginRegisterActivity.this,  " Logged in Successfully....", Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();
                    } else {
                        Toast.makeText(DriverLoginRegisterActivity.this, "Login Unsuccessful ..Please try again..", Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();
                    }
                }
            });
        }
    }

        private void RegisterDriver ( String email, String password){

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please Enter Your Email...", Toast.LENGTH_SHORT).show();
            }
           else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please Enter Your Password...", Toast.LENGTH_SHORT).show();
            } else {
                LoadingBar.setTitle("Driver Registration");
                LoadingBar.setMessage("Please Wait ...You are being Registered");
                LoadingBar.show();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    OnlineDriverId=mAuth.getCurrentUser().getUid();
                                    DriverDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(OnlineDriverId);


                                    DriverDatabaseRef.setValue(true);


                                    Toast.makeText(DriverLoginRegisterActivity.this,  " Registered Successfully....", Toast.LENGTH_SHORT).show();
                                    LoadingBar.dismiss();
                                    Intent DriverIntent=new Intent(DriverLoginRegisterActivity.this,DriversMapsActivity.class);
                                    startActivity(DriverIntent);
                                } else {
                                    Toast.makeText(DriverLoginRegisterActivity.this, "Registration Unsuccessful ..Please try again..", Toast.LENGTH_SHORT).show();
                                    LoadingBar.dismiss();
                                }
                            }
                        });

            }
        }

}
