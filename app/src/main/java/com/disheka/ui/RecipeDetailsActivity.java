//IM/2021/032- Dinuvi Nethumila
package com.disheka.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

import com.disheka.R;
import com.disheka.model.Recipe;
import com.squareup.picasso.Picasso;

public class RecipeDetailsActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        // Get the intent and the recipe data
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        // Initialize views
        TextView ingredientsTextView = findViewById(R.id.recipe_author);
        TextView stepsTextView = findViewById(R.id.recipe_steps);
        ImageView imageView = findViewById(R.id.recipe_image);
        videoView = findViewById(R.id.video_view); // Make sure you have a VideoView in your layout

        // Set the data to views
        if (recipe != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(recipe.getName());
            }
            ingredientsTextView.setText(recipe.getIngredients());
            stepsTextView.setText(recipe.getSteps());
            Picasso.get().load(recipe.getImageUrl()).into(imageView);

            // Load video
            loadVideo(recipe.getVideoUrl());
        }
    }

    private void loadVideo(String videoUrl) {
        try {
            Log.e("VIDEO PLAYER", "LOADING VIDEO");
            Uri videoUri = Uri.parse(videoUrl); // Parse the video URL
            videoView.setVideoURI(videoUri);

            // Create a MediaController to allow play/pause controls
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.requestFocus(); // Request focus to play the video
            videoView.start(); // Start video playback
        } catch (Exception e) {
            Log.e("VIDEO PLAYER", "Error loading video: " + e.getMessage());
        }
    }
}
