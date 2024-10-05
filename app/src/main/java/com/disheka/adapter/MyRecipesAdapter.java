package com.disheka.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Import Toast for displaying messages
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.disheka.R;
import com.disheka.model.Recipe;
import com.bumptech.glide.Glide; // Import Glide
import com.disheka.ui.EditRecipeActivity;
import com.google.firebase.firestore.FirebaseFirestore; // Import Firestore
import java.util.List;

public class MyRecipesAdapter extends RecyclerView.Adapter<MyRecipesAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipeList;

    public MyRecipesAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe, position); // Pass position to the bind method
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeTitle;
        private TextView recipeDescription;
        private ImageView recipeImage; // Add ImageView for recipe image
        private ImageView deleteIcon; // Add ImageView for delete icon
        private ImageView editIcon;
        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            recipeDescription = itemView.findViewById(R.id.recipeDescription);
            recipeImage = itemView.findViewById(R.id.recipeImage); // Initialize the ImageView
            deleteIcon = itemView.findViewById(R.id.deleteIcon); // Initialize the delete icon
            editIcon = itemView.findViewById(R.id.editIcon);
        }

        public void bind(Recipe recipe, int position) {
            recipeTitle.setText(recipe.getName());
            recipeDescription.setText(recipe.getIngredients()); // Update description to use ingredients

            // Load the recipe image from the URL using Glide
            if (recipe.getImageUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(recipe.getImageUrl()) // Load the image URL
                        .into(recipeImage); // Set the image in the ImageView
            } else {
                //recipeImage.setImageResource(R.drawable.placeholder_image); // Set a default image if no image URL is provided
            }

            // Handle delete icon click
            deleteIcon.setOnClickListener(v -> deleteRecipe(recipe.getDocumentId(), position));

            editIcon.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), EditRecipeActivity.class);
                intent.putExtra("RECIPE", recipe); // Pass the Recipe object
                itemView.getContext().startActivity(intent);
            });
        }

        private void deleteRecipe(String documentId, int position) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Delete the recipe from Firestore
            db.collection("recipes").document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Remove the recipe from the local list
                        recipeList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, recipeList.size());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(itemView.getContext(), "Error deleting recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });


        }
    }
}
