package com.disheka.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.disheka.R;
import com.disheka.model.Recipe;

import java.util.List;

public class PopularRecipeAdapter extends RecyclerView.Adapter<PopularRecipeAdapter.ViewHolder> {
    private Context context;
    private List<Recipe> popularRecipeList;

    // Constructor
    public PopularRecipeAdapter(Context context, List<Recipe> popularRecipeList) {
        this.context = context;
        this.popularRecipeList = popularRecipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for popular recipes
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind popular recipe data
        Recipe popularRecipe = popularRecipeList.get(position);
        holder.ratingBar.setRating(popularRecipe.getRating());
        // Set image or other details here if needed
    }

    @Override
    public int getItemCount() {
        return popularRecipeList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        ImageView imageViewRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
        }
    }
}
