package com.disheka.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.disheka.R;
import com.disheka.model.Recipe;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditRecipeActivity extends AppCompatActivity {

    private EditText editRecipeImageUrl;
    private EditText editRecipeTitle;
    private EditText editRecipeIngredients;
    private EditText editRecipeSteps;
    private Button saveButton;
    private Button discardButton;

    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        editRecipeTitle = findViewById(R.id.editRecipeTitle);
        editRecipeIngredients = findViewById(R.id.editRecipeIngredients);
        editRecipeSteps = findViewById(R.id.editRecipeSteps);
        saveButton = findViewById(R.id.saveButton);
        discardButton = findViewById(R.id.discardButton);

        // Get the recipe passed from the adapter
        recipe = (Recipe) getIntent().getSerializableExtra("RECIPE");

        // Populate fields with existing recipe details
        if (recipe != null) {
            editRecipeTitle.setText(recipe.getName());
            editRecipeIngredients.setText(recipe.getIngredients());
            editRecipeSteps.setText(recipe.getSteps().toString());
        }

        saveButton.setOnClickListener(v -> saveChanges());
        discardButton.setOnClickListener(v -> finish());
    }

    private void saveChanges() {
        String title = editRecipeTitle.getText().toString().trim();
        String ingredients = editRecipeIngredients.getText().toString().trim();
        String steps = editRecipeSteps.getText().toString().trim();

        // Validate input
        if (title.isEmpty() || ingredients.isEmpty() || steps.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the recipe object
        recipe.setName(title);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps); // Assuming you have a setSteps method in your Recipe class

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
