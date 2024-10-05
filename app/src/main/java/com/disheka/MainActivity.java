//IM/2021/091 - Dulanjika Bandara
package com.disheka;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.disheka.ui.LoginActivity;
import com.disheka.ui.NavigationActivity;
import com.disheka.ui.StartCooking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Optional: Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Set the layout for the splash screen
        setContentView(R.layout.activity_main);

        // Delay for 5 seconds before transitioning to StartCooking or NavigationActivity
        new Handler().postDelayed(() -> {
            // Check if the user is logged in
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                // User is not logged in, redirect to StartCooking activity
                Intent intent = new Intent(MainActivity.this, StartCooking.class);
                startActivity(intent);
            } else {
                // User is logged in, redirect to NavigationActivity
                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(intent);
            }

            // Finish the splash activity
            finish();

        }, 5000); // 5000 milliseconds = 5 seconds
    }
}