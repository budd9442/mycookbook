package com.disheka.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.disheka.R;
import com.disheka.adapter.MyRecipesAdapter;
import com.disheka.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyRecipesFragment extends Fragment {

    private RecyclerView recipesRecyclerView;
    private MyRecipesAdapter recipeAdapter; // Use MyRecipesAdapter
    private List<Recipe> recipeList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipes, container, false);

        recipesRecyclerView = view.findViewById(R.id.recipesRecyclerView);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recipeList = new ArrayList<>();
        recipeAdapter = new MyRecipesAdapter(getContext(), recipeList); // Use MyRecipesAdapter
        recipesRecyclerView.setAdapter(recipeAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadMyRecipes();

        return view;
    }

    private void loadMyRecipes() {
        String currentUserUid = mAuth.getCurrentUser().getUid();

        // Query the Firestore database
        db.collection("recipes")
                .whereEqualTo("authorUid", currentUserUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recipeList.clear(); // Clear the existing list to avoid duplication
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Create a recipe object from the document
                            Recipe recipe = document.toObject(Recipe.class);
                            // Set the document ID in the recipe object
                            recipe.setDocumentId(document.getId());
                            // Add the recipe to the list
                            recipeList.add(recipe);
                            Log.d("RECIPESSSSSSS", "loadMyRecipes: "+ recipe.getDocumentId());
                        }
                        recipeAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    } else {
                        Toast.makeText(getActivity(), "Failed to load recipes.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
