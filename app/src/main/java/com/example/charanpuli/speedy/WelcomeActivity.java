package com.example.charanpuli.speedy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
   private Button DriverLoginButton,CustomerLoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        DriverLoginButton=(Button)findViewById(R.id.driver_login_btn);
        CustomerLoginButton=(Button)findViewById(R.id.customer_login_btn);

        DriverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent driver_login_intent=new Intent(WelcomeActivity.this,DriverLoginRegisterActivity.class);
                startActivity(driver_login_intent);
            }
        });
        CustomerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customer_login_intent=new Intent(WelcomeActivity.this,CustomerLoginRegisterActivity.class);
                startActivity(customer_login_intent);
            }
        });

    }
}
