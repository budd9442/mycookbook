package com.disheka.ui.addrecipe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.disheka.R;
import com.disheka.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class AddRecipeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextRecipeName, editTextIngredients, editTextSteps;
    private ImageView imageViewRecipe;
    private Uri imageUri;
    private Button buttonUploadImage, buttonSubmit;
    private Spinner spinnerPreparationTime;

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

        // Set up Spinner for Preparation Time
        setupPreparationTimeSpinner();

        buttonUploadImage.setOnClickListener(v -> openFileChooser());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewRecipe.setImageURI(imageUri);
            imageViewRecipe.setVisibility(View.VISIBLE);
        }
    }

    private void submitRecipe() {
        String recipeName = editTextRecipeName.getText().toString().trim();
        String ingredients = editTextIngredients.getText().toString().trim();
        String steps = editTextSteps.getText().toString().trim();
        String time = spinnerPreparationTime.getSelectedItem().toString().trim();
        if (recipeName.isEmpty() || ingredients.isEmpty() || steps.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and upload an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image and recipe details to Firebase
        uploadRecipe(recipeName, ingredients, steps, imageUri,time);
    }
    private void uploadRecipe(String recipeName, String ingredients, String steps, Uri imageUri, String time) {
        // Get the Firebase Storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("recipes");
        String imageName = System.currentTimeMillis() + ".jpg";
        StorageReference fileReference = storageReference.child(imageName);

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(AddRecipeActivity.this, "User not signed in", Toast.LENGTH_SHORT).show();
            return; // Exit if the user is not logged in
        }

        // Upload the image to Firebase Storage
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL after a successful upload
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Create a recipe object to store in Firestore
                        Recipe recipe = new Recipe(recipeName, ingredients, steps, uri.toString(), time, user.getUid(), user.getDisplayName());

                        // Save the recipe to Firestore
                        db.collection("recipes") // Change "recipes" to your desired collection name
                                .add(recipe)
                                .addOnSuccessListener(documentReference -> {
                                    // Set the document ID in the recipe object
                                    recipe.setDocumentId(documentReference.getId());

                                    // Now update the recipe in Firestore with the document ID
                                    db.collection("recipes").document(documentReference.getId()).set(recipe)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(AddRecipeActivity.this, "Recipe uploaded successfully", Toast.LENGTH_SHORT).show();
                                                finish(); // Close the activity
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(AddRecipeActivity.this, "Error updating recipe with document ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AddRecipeActivity.this, "Error saving recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddRecipeActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(AddRecipeActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
