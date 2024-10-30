package com.example.espresso; 

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class attendee_signup extends AppCompatActivity {
    private EditText firstNameField, lastNameField, emailAddressField, phoneNumField;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_signup);

        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        emailAddressField = findViewById(R.id.email_address);
        phoneNumField = findViewById(R.id.optional_phone_num);
        signUpButton = findViewById(R.id.attendeeSignUpButton);

        signUpButton.setOnClickListener(v -> {
            if (inputConstraint()) {
                // Proceed with sign-up logic for database then start new activity for attendee screen
            }
        });
    }

    private boolean inputConstraint() {
        if (firstNameField.length() == 0) {
            firstNameField.setError("Field Required");
            return false;
        }
        if (lastNameField.length() == 0) {
            lastNameField.setError("Field Required");
            return false;
        }
        if (emailAddressField.length() == 0) {
            emailAddressField.setError("Field Required");
            return false;
        }
        return true;
    }
}
