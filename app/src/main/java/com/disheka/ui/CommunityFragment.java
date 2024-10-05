package com.disheka.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.disheka.R;
import com.disheka.adapter.CommunityRecipeAdapter;
import com.disheka.model.Recipe;
import com.disheka.ui.addrecipe.AddRecipeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommunityRecipeAdapter recipeAdapter;
    private List<Recipe> recipeList; // All recipes loaded from Firestore
    private FirebaseFirestore db;
    private FloatingActionButton fabAddRecipe;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);

        fabAddRecipe = view.findViewById(R.id.fab_add_recipe);

        // Set click listener for FAB
        fabAddRecipe.setOnClickListener(v -> {
            // Start the activity to add a recipe
            Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
            startActivity(intent);
        });

        // Set up LinearLayoutManager for a single recipe per row
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Initialize the recipe list and adapter
        recipeList = new ArrayList<>();
        recipeAdapter = new CommunityRecipeAdapter(recipeList, getContext());
        recyclerView.setAdapter(recipeAdapter);

        fetchRecipesFromFirestore(); // Load all recipes from Firestore

        return view;
    }

    private void fetchRecipesFromFirestore() {
        db.collection("recipes") // Replace with your collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            recipeList.add(recipe); // Add to the main list
                            Log.d("AAAAAAAAAAA", "fetchRecipesFromFirestore: "+recipe);
                        }
                        recipeAdapter.notifyDataSetChanged(); // Notify adapter to update the UI
                    } else {
                        Snackbar.make(getView(), "Failed to fetch recipes", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
}
