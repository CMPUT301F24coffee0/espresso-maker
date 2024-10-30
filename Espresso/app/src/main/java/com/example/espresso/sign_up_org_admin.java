package com.example.espresso;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class sign_up_org_admin extends AppCompatActivity {

    private TextView adminTab, organizerTab;
    private EditText facilityNameField, fullNameField, passwordField, confirmPasswordField;
    private Button signUpButton_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_signup);

        adminTab = findViewById(R.id.admin_tab);
        organizerTab = findViewById(R.id.organizer_tab);
        facilityNameField = findViewById(R.id.facility_name);
        fullNameField = findViewById(R.id.full_name);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        signUpButton_admin = findViewById(R.id.AdminSignUpButton);


        selectAdminTab();

        adminTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAdminTab();
            }
        });

        organizerTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOrganizerTab();
            }
        });

        signUpButton_admin.setOnClickListener(v -> {
            if (inputConstraint() && isVisible()) {
                // Proceed with sign-up logic for database then start new activity for organizer screen based on facility name field visibility screen
            }

            else if (inputConstraint() && !isVisible()) {

                // Proceed with sign-up logic for database then start new activity for admin screen based on facility name field visibility screen
            }
        });
    }

    private void selectAdminTab() {
        adminTab.setBackgroundResource(R.drawable.selected_tab_selector);
        organizerTab.setBackgroundResource(R.drawable.unselected_tab_selector);
        adminTab.setTextColor(getResources().getColor(R.color.black));
        organizerTab.setTextColor(getResources().getColor(R.color.blueGrey));
        facilityNameField.setVisibility(View.GONE);
    }

    private void selectOrganizerTab() {
        adminTab.setBackgroundResource(R.drawable.unselected_tab_selector);
        organizerTab.setBackgroundResource(R.drawable.selected_tab_selector);
        facilityNameField.setVisibility(View.VISIBLE);
    }

    private boolean isVisible() {
        return facilityNameField.getVisibility() == View.VISIBLE;
    }

    private boolean inputConstraint() {
        if (fullNameField.length() == 0) {
            fullNameField.setError("Field Required");
            return false;
        }
        if (passwordField.length() == 0) {
            passwordField.setError("Field Required");
            return false;
        }
        if (confirmPasswordField.length() == 0) {
            confirmPasswordField.setError("Field Required");
            return false;
        }
        if (facilityNameField.getVisibility() == View.VISIBLE && facilityNameField.length() == 0) {
            facilityNameField.setError("Field Required");
            return false;
        }
        return true;
    }
}
