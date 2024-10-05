package com.disheka.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.disheka.R;
import com.disheka.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Recipe> recipeList;
    private Context context;
    private boolean isFeatured; // New variable to differentiate featured recipes

    public RecipeAdapter(List<Recipe> recipeList, Context context, boolean isFeatured) {
        this.recipeList = recipeList;
        this.context = context;
        this.isFeatured = isFeatured; // Initialize the variable
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isFeatured) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_featured_recipe, parent, false);
            return new FeaturedRecipeViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
            return new RegularRecipeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        if (isFeatured && holder instanceof FeaturedRecipeViewHolder) {
            ((FeaturedRecipeViewHolder) holder).bind(recipe);
        } else if (holder instanceof RegularRecipeViewHolder) {
            ((RegularRecipeViewHolder) holder).bind(recipe);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // Method to update the recipe list
    public void updateList(List<Recipe> newList) {
        recipeList.clear();
        recipeList.addAll(newList);
        notifyDataSetChanged();
    }

    static class RegularRecipeViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        ImageView image;

        RegularRecipeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipe_name);
            description = itemView.findViewById(R.id.recipe_description);
            image = itemView.findViewById(R.id.recipe_image);
        }

        void bind(Recipe recipe) {
            name.setText(recipe.getName());
            description.setText(recipe.getTime());
            Glide.with(itemView.getContext()).load(recipe.getImageUrl()).into(image);
        }
    }

    static class FeaturedRecipeViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        ImageView image;

        FeaturedRecipeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.featured_recipe_name);
            description = itemView.findViewById(R.id.featured_recipe_description);
            image = itemView.findViewById(R.id.featured_recipe_image);
        }

        void bind(Recipe recipe) {
            name.setText(recipe.getName());
            description.setText(recipe.getTime());
            Glide.with(itemView.getContext()).load(recipe.getImageUrl()).into(image);
        }
    }
}
