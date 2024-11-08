package com.example.espresso;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
/**
 * The NewEventForm activity is responsible for displaying the form to create a new event.
 * It allows the user to fill in event details and navigate to the image upload fragment to upload an event image.
 */
public class NewEventForm extends AppCompatActivity {

    /**
     * Called when the activity is created. Initializes the UI and handles the logic for navigating to the image upload fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Enables edge-to-edge display on the activity
        setContentView(R.layout.activity_new_event_form);  // Sets the layout for the activity

        // Adjust the window insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the "Next" button
        Button nextButton = findViewById(R.id.next_button);

        // Set onClickListener for the "Next" button to navigate to the image upload fragment
        nextButton.setOnClickListener(v -> {
            // Begin the fragment transaction to navigate to the image upload fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // Create a new instance of the image upload fragment
            image_upload_fragment fragment = new image_upload_fragment();
            // Replace the current layout with the image upload fragment
            fragmentTransaction.replace(R.id.landing_page, fragment);
            // Optionally add this transaction to the back stack for navigation
            fragmentTransaction.addToBackStack(null);
            // Commit the transaction to apply the changes
            fragmentTransaction.commit();
        });
    }
}
