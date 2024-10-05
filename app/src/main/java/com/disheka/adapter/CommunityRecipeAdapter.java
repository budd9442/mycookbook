package com.disheka.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.disheka.R;
import com.disheka.model.Recipe;
import com.squareup.picasso.Picasso; // Make sure Picasso is added to your project

import java.util.List;

public class CommunityRecipeAdapter extends RecyclerView.Adapter<CommunityRecipeAdapter.CommunityRecipeViewHolder> {

    private final List<Recipe> recipeList;

    public CommunityRecipeAdapter(List<Recipe> recipeList, Context context) {
        this.recipeList = recipeList;

    }

    @NonNull
    @Override
    public CommunityRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the recipe_item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item_community, parent, false);
        return new CommunityRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityRecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class CommunityRecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeTitle;
        TextView recipeAuthor;

        public CommunityRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            recipeAuthor = itemView.findViewById(R.id.recipe_author);
        }

        public void bind(Recipe recipe) {
            recipeTitle.setText(recipe.getName());
            recipeAuthor.setText("Recipe by "+recipe.getAuthor()); // Adjusted to use ingredients

            // Load image using Picasso
            Picasso.get().load(recipe.getImageUrl()) // Ensure your Recipe model has an image URL
                    .into(recipeImage);
        }
    }
}
