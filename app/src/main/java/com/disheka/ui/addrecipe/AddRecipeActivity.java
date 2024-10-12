//IM/2021/036 - Bimbara Theekshani
package com.disheka.ui.addrecipe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.disheka.R;
import com.disheka.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.firestore.FirebaseFirestore;

public class AddRecipeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2; // Unique request code for video selection

    private EditText editTextRecipeName, editTextIngredients, editTextSteps, editTextUrl;
    private ImageView imageViewRecipe;
    private Uri imageUri, videoUri;
    private Button buttonUploadImage, buttonUploadVideo, buttonSubmit;
    private Spinner spinnerPreparationTime;
    private ProgressBar uploadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Recipe");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        editTextIngredients = findViewById(R.id.editTextIngredients);
        editTextSteps = findViewById(R.id.editTextSteps);
        imageViewRecipe = findViewById(R.id.imageViewRecipe);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        spinnerPreparationTime = findViewById(R.id.spinnerPreparationTime);
        buttonUploadVideo = findViewById(R.id.buttonUploadVideo); // New button for video upload
        uploadProgressBar = findViewById(R.id.uploadProgressBar); // Progress bar for video upload

        // Set up Spinner for Preparation Time
        setupPreparationTimeSpinner();

        buttonUploadImage.setOnClickListener(v -> openFileChooser());
        buttonUploadVideo.setOnClickListener(v -> openVideoChooser()); // Video upload button listener
        buttonSubmit.setOnClickListener(v -> submitRecipe());
    }

    private void setupPreparationTimeSpinner() {
        String[] preparationTimes = {"15 minutes", "30 minutes", "45 minutes", "1 hour", "2 hours", "2+ hours"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, preparationTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPreparationTime.setAdapter(adapter);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_REQUEST);
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewRecipe.setImageURI(imageUri);
            imageViewRecipe.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData(); // Get the video URI
            Toast.makeText(this, "Video selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitRecipe() {
        String recipeName = editTextRecipeName.getText().toString().trim();
        String ingredients = editTextIngredients.getText().toString().trim();
        String steps = editTextSteps.getText().toString().trim();
        String time = spinnerPreparationTime.getSelectedItem().toString().trim();
        if (recipeName.isEmpty() || ingredients.isEmpty() || steps.isEmpty() || imageUri == null || videoUri == null) {
            Toast.makeText(this, "Please fill all fields, upload an image, and select a video.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload video and recipe details to Firebase
        uploadRecipeWithVideo(recipeName, ingredients, steps, imageUri, videoUri, time);
    }

    private void uploadRecipeWithVideo(String recipeName, String ingredients, String steps, Uri imageUri, Uri videoUri, String time) {
        // Get the Firebase Storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("recipes");
        String imageName = System.currentTimeMillis() + ".jpg";
        String videoName = System.currentTimeMillis() + ".mp4"; // Generate video name
        StorageReference imageFileReference = storageReference.child(imageName);
        StorageReference videoFileReference = storageReference.child(videoName); // Video file reference

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(AddRecipeActivity.this, "User not signed in", Toast.LENGTH_SHORT).show();
            return; // Exit if the user is not logged in
        }

        // Show the progress bar for video upload
        uploadProgressBar.setVisibility(View.VISIBLE);

        // Upload the image first
        imageFileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageFileReference.getDownloadUrl().addOnSuccessListener(imageUriResult -> {

                        // Upload the video after the image is successfully uploaded
                        videoFileReference.putFile(videoUri)
                                .addOnSuccessListener(videoTaskSnapshot -> {
                                    videoFileReference.getDownloadUrl().addOnSuccessListener(videoUriResult -> {

                                        // Create a recipe object to store in Firestore
                                        Recipe recipe = new Recipe(recipeName, ingredients, steps, imageUriResult.toString(), time, user.getUid(), user.getDisplayName());
                                        recipe.setVideoUrl(videoUriResult.toString()); // Set video URL

                                        // Save the recipe to Firestore
                                        db.collection("recipes")
                                                .add(recipe)
                                                .addOnSuccessListener(documentReference -> {
                                                    uploadProgressBar.setVisibility(View.GONE); // Hide progress bar after success
                                                    Toast.makeText(AddRecipeActivity.this, "Recipe uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    finish(); // Close the activity
                                                })
                                                .addOnFailureListener(e -> {
                                                    uploadProgressBar.setVisibility(View.GONE); // Hide progress bar after failure
                                                    Toast.makeText(AddRecipeActivity.this, "Error saving recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });

                                    }).addOnFailureListener(e -> {
                                        uploadProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(AddRecipeActivity.this, "Failed to get video URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }).addOnFailureListener(e -> {
                                    uploadProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(AddRecipeActivity.this, "Video upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }).addOnFailureListener(e -> {
                        uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(AddRecipeActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    uploadProgressBar.setVisibility(View.GONE);
                    Toast.makeText(AddRecipeActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public String getNameByAuthorUid(String uid) {
        try {
            // Synchronously get the document where authorUid matches the provided uid
            QuerySnapshot querySnapshot = db.collection("users")
                    .whereEqualTo("authorUid", uid)
                    .get().getResult(); // This is blocking and should be called on a background thread in a real app

            // Check if the query returned any documents
            for (QueryDocumentSnapshot document : querySnapshot) {
                return document.getString("name"); // Assuming the field in Firestore is named "name"
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as needed
        }
        return null; // Return null if no match is found or an error occurs
    }

}
