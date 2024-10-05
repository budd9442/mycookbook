package com.disheka.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.disheka.R;
import com.disheka.model.Recipe;
import com.squareup.picasso.Picasso;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        // Get the intent and the recipe data
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        // Initialize views
        TextView titleTextView = findViewById(R.id.recipe_title);
        TextView ingredientsTextView = findViewById(R.id.recipe_author);
        TextView stepsTextView = findViewById(R.id.recipe_steps);
        ImageView imageView = findViewById(R.id.recipe_image);

        // Set the data to views
        if (recipe != null) {
            titleTextView.setText(recipe.getName());
            ingredientsTextView.setText(recipe.getIngredients());
            stepsTextView.setText(recipe.getSteps());
            Picasso.get().load(recipe.getImageUrl()).into(imageView);
        }
    }
}
