package com.disheka.ui;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

//        // Initialize views
//        TextView titleTextView = findViewById(R.id.recipe_title);
        TextView ingredientsTextView = findViewById(R.id.recipe_author);
        TextView stepsTextView = findViewById(R.id.recipe_steps);
        ImageView imageView = findViewById(R.id.recipe_image);
        WebView youtubeWebView = findViewById(R.id.youtube_webview);

        // Set the data to views
        if (recipe != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(recipe.getName());
            }
//            titleTextView.setText(recipe.getName());
            ingredientsTextView.setText(recipe.getIngredients());
            stepsTextView.setText(recipe.getSteps());
            Picasso.get().load(recipe.getImageUrl()).into(imageView);

            // Load YouTube video
            try {
                loadYouTubeVideo(youtubeWebView, recipe.getVideoUrl()); // Assume recipe has a getVideoUrl() method
            }
            catch (Exception e){
                Log.d("YOUTUBE PLAYER", "Link is prolly invalid");
            }

        }
    }

    private void loadYouTubeVideo(WebView webView, String videoUrl) {
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript

        // Load the YouTube IFrame Player API URL
        String videoId = extractVideoId(videoUrl); // Extract video ID from the URL
        String iframeHtml = "<iframe width=\"100%\" height=\"250\" " +
                "src=\"https://www.youtube.com/embed/" + videoId + "\" " +
                "frameborder=\"0\" allowfullscreen></iframe>";

        webView.loadData(iframeHtml, "text/html", "UTF-8");
    }

    private String extractVideoId(String videoUrl) {
        // Assuming videoUrl is in the format "https://www.youtube.com/watch?v=VIDEO_ID"
        String videoId = videoUrl.split("v=")[1];
        if (videoId.contains("&")) {
            videoId = videoId.split("&")[0];
        }
        return videoId;
    }
}
