package com.disheka.ui;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.disheka.R;

public class StartCooking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_cooking);

        // Apply window insets if needed
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the button and set an onClickListener
        Button logIn = findViewById(R.id.button2);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define what happens when the button is clicked
                Intent intent = new Intent(StartCooking.this, LoginActivity.class);
                startActivity(intent); // Navigate to CookingActivity
            }
        });
        Button startCookingButton = findViewById(R.id.button); // Assuming button2 is the ID of "Start Cooking" button
        startCookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartCooking.this, RegistrationActivity.class);
                startActivity(intent); // Start the SkillLevelActivity
            }
        });
    }
}