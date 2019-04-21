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

public class CustomerLoginRegisterActivity extends AppCompatActivity {

    private Button CustomerLoginBtn, CustomerRegisterBtn;
    private TextView CustomerStatus, CustomerRegisterLink;
    private EditText CustomerEmail, CustomerPassword;
    private FirebaseAuth mAuth;
    ProgressDialog LoadingBar;
    private DatabaseReference CustomerDatabaseRef;
    private String OnlineCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);
        CustomerLoginBtn = (Button) findViewById(R.id.login_customer_btn);
        CustomerRegisterBtn = (Button) findViewById(R.id.register_customer_btn);
        CustomerStatus = (TextView) findViewById(R.id.customer_status);
        CustomerRegisterLink = (TextView) findViewById(R.id.customer_register_link);
        CustomerRegisterBtn.setVisibility(View.INVISIBLE);
        CustomerRegisterBtn.setEnabled(false);
        CustomerEmail = (EditText) findViewById(R.id.email_customer);
        CustomerPassword = (EditText) findViewById(R.id.password_customer);
        LoadingBar = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();





        CustomerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerLoginBtn.setVisibility(View.INVISIBLE);
                CustomerRegisterLink.setVisibility(View.INVISIBLE);
                CustomerRegisterBtn.setVisibility(View.VISIBLE);
                CustomerRegisterBtn.setEnabled(true);
                CustomerStatus.setText("Customer Register");


            }
        });
        CustomerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = CustomerEmail.getText().toString();
                String password = CustomerPassword.getText().toString();
                SignInCustomer(email, password);

            }
        });

        CustomerRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = CustomerEmail.getText().toString();
                String password = CustomerPassword.getText().toString();
                CustomerRegister(email, password);
            }
        });

    }

    private void SignInCustomer(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Your Email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter Your Password...", Toast.LENGTH_SHORT).show();
        } else {
            LoadingBar.setTitle("Customer Sign In");
            LoadingBar.setMessage("Please Wait ...While we are checking your credentials");
            LoadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent customerintent=new Intent(CustomerLoginRegisterActivity.this,CustomersMapActivity.class);
                                startActivity(customerintent);
                                Toast.makeText(CustomerLoginRegisterActivity.this, " Logged in Successfully....", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();

                            } else {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Login Unsuccessful ..Please try again..", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void CustomerRegister(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Your Email...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter Your Password...", Toast.LENGTH_SHORT).show();
        } else {
            LoadingBar.setTitle("Customer Registration");
            LoadingBar.setMessage("Please Wait ...You are being Registered");
            LoadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                OnlineCustomerId=mAuth.getCurrentUser().getUid();
                                CustomerDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(OnlineCustomerId);

                                CustomerDatabaseRef.setValue(true);

                                Toast.makeText(CustomerLoginRegisterActivity.this, " Registered Successfully....", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                                Intent CustomerIntent=new Intent(CustomerLoginRegisterActivity.this,CustomersMapActivity.class);
                                startActivity(CustomerIntent);

                            } else {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Registration Unsuccessful ..Please try again..", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }
                        }
                    });

        }
    }
}

