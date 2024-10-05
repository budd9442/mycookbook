package com.disheka;

import android.content.Intent;
import android.os.Bundle;
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

        // Check if the user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, redirect to LoginActivity
            startActivity(new Intent(this, StartCooking.class));
            // User is logged in, redirect to NavigationActivity
             }else{
            startActivity(new Intent(this, NavigationActivity.class));

        }

        finish(); // Close the MainActivity
    }
}
