package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button attendee_sign_in_btn, org_sign_in_btn, adm_sign_in_btn, create_acc_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.start_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        attendee_sign_in_btn = findViewById(R.id.AttendeeSignInButton);
        org_sign_in_btn = findViewById(R.id.OrganizerSignInButton);
        adm_sign_in_btn = findViewById(R.id.AdminSignInButton);
        create_acc_btn = findViewById(R.id.CreateAccountButton);

        attendee_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, attendee_signup.class);
                startActivity(intent);
            }
        });

        org_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sign_up_org_admin.class);
                startActivity(intent);
            }
        });

        adm_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sign_up_org_admin.class);
                startActivity(intent);
            }
        });



    }
}