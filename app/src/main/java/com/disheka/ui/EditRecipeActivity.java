package com.disheka.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.disheka.R;
import com.disheka.model.Recipe;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditRecipeActivity extends AppCompatActivity {

    private EditText editRecipeTitle;
    private EditText editRecipeIngredients;
    private EditText editRecipeSteps;
    private Spinner spinnerPreparationTime;
    private Button saveButton;
    private Button discardButton;

    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Recipe");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        // Initialize Views
        editRecipeTitle = findViewById(R.id.editRecipeTitle);
        editRecipeIngredients = findViewById(R.id.editRecipeIngredients);
        editRecipeSteps = findViewById(R.id.editRecipeSteps);
        spinnerPreparationTime = findViewById(R.id.spinnerPreparationTime);
        saveButton = findViewById(R.id.saveButton);
        discardButton = findViewById(R.id.discardButton);

        // Set up the spinner
        setupPreparationTimeSpinner();

        // Get the recipe passed from the adapter
        recipe = (Recipe) getIntent().getSerializableExtra("RECIPE");

        // Populate fields with existing recipe details
        if (recipe != null) {
            editRecipeTitle.setText(recipe.getName());
            editRecipeIngredients.setText(recipe.getIngredients());
            editRecipeSteps.setText(recipe.getSteps());

            // Set the spinner to the correct time if available
            String timeToCook = recipe.getTime();
            if (timeToCook != null) {
                int timePosition = ((ArrayAdapter<String>) spinnerPreparationTime.getAdapter()).getPosition(timeToCook);
                spinnerPreparationTime.setSelection(timePosition);
            }
        }

        saveButton.setOnClickListener(v -> saveChanges());
        discardButton.setOnClickListener(v -> finish());
    }

    // Method to set up spinner with preparation times
    private void setupPreparationTimeSpinner() {
        String[] preparationTimes = {"15 minutes", "30 minutes", "45 minutes", "1 hour", "2 hours", "2+ hours"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, preparationTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPreparationTime.setAdapter(adapter);
    }

    private void saveChanges() {
        String title = editRecipeTitle.getText().toString().trim();
        String ingredients = editRecipeIngredients.getText().toString().trim();
        String steps = editRecipeSteps.getText().toString().trim();
        String timeToCook = spinnerPreparationTime.getSelectedItem().toString(); // Get selected time

        // Validate input
        if (title.isEmpty() || ingredients.isEmpty() || steps.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the recipe object
        recipe.setName(title);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);
        recipe.setTime(timeToCook); // Save the selected time

        // Save to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(recipe.getDocumentId())
                .set(recipe)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
