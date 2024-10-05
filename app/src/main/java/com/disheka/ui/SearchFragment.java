package com.disheka.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.disheka.R;
import com.disheka.adapter.RecipeAdapter;
import com.disheka.model.Recipe;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView featuredRecyclerView, fullRecyclerView;
    private RecipeAdapter fullRecipeAdapter, featuredRecipeAdapter;
    private List<Recipe> recipeList;
    private List<Recipe> featuredList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("MyCookBook");
        }

        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firestore and RecyclerViews
        db = FirebaseFirestore.getInstance();
        featuredRecyclerView = view.findViewById(R.id.featured_recycler_view);
        fullRecyclerView = view.findViewById(R.id.full_recycler_view);

        // Setup RecyclerView for featured recipes (horizontal)
        featuredRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredList = new ArrayList<>();
        featuredRecipeAdapter = new RecipeAdapter(featuredList, getContext(), true); // Pass true for featured
        featuredRecyclerView.setAdapter(featuredRecipeAdapter);

        // Setup RecyclerView for full recipe list (grid, one recipe per row)
        fullRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recipeList = new ArrayList<>();
        fullRecipeAdapter = new RecipeAdapter(recipeList, getContext(), false); // Pass false for regular
        fullRecyclerView.setAdapter(fullRecipeAdapter);

        // Fetch recipes from Firestore
        fetchRecipesFromFirestore();

        return view;
    }

    private void fetchRecipesFromFirestore() {
        db.collection("recipes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Recipe> allRecipes = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Recipe recipe = document.toObject(Recipe.class);
                    allRecipes.add(recipe);
                }

                // Shuffle the list and pick 4 random featured recipes
                Collections.shuffle(allRecipes);
                List<Recipe> featuredRecipes = allRecipes.subList(0, Math.min(4, allRecipes.size())); // Pick 4 or less if fewer exist
                featuredList.addAll(featuredRecipes);
                featuredRecipeAdapter.notifyDataSetChanged();

                // Add remaining recipes to the full list
                recipeList.addAll(allRecipes);
                fullRecipeAdapter.notifyDataSetChanged();
            } else {
                Snackbar.make(getView(), "Failed to fetch recipes", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRecipes(newText);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void filterRecipes(String query) {
        List<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredRecipes.add(recipe);
            }
        }
        fullRecipeAdapter.updateList(filteredRecipes); // Make sure your adapter has this method
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_filter) {
            openFilterOptions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFilterOptions() {
        Snackbar.make(getView(), "Filter options clicked", Snackbar.LENGTH_SHORT).show();
    }
}
