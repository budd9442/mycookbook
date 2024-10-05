package com.disheka.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.disheka.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {

    private String[][] answers = {
            {"Beginner", "Intermediate", "Advanced"},
            {"Under 15 minutes", "15-30 minutes", "30-60 minutes", "Over 60 minutes"},
            {"1 (just for me)", "2 (couple)", "3-4 (small family)", "5+ (large family or group)"},
            {"None", "Vegan", "Vegetarian", "Gluten free", "Dairy Free", "Nut free", "Low Carb", "Keto"}
    };

    private Spinner spinnerCookingSkill;
    private Spinner spinnerTimeAllocation;
    private Spinner spinnerServingQuantity;
    private Spinner spinnerDietaryRestrictions;

    private ImageView profilePicture;
    private TextView userName;
    private Button buttonSave; // Button to save changes

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Set up the ActionBar
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Account");
        }

        // Enable the action bar menu
        setHasOptionsMenu(true);

        // Initialize views
        profilePicture = view.findViewById(R.id.profilePicture);
        //userName = view.findViewById(R.id.userName);
        spinnerCookingSkill = view.findViewById(R.id.spinnerCookingSkill);
        spinnerTimeAllocation = view.findViewById(R.id.spinnerTimeAllocation);
        spinnerServingQuantity = view.findViewById(R.id.spinnerServingQuantity);
        spinnerDietaryRestrictions = view.findViewById(R.id.spinnerDietaryRestrictions);
        buttonSave = view.findViewById(R.id.buttonSave); // Initialize the Save button

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        setupSpinners();
        loadUserData(); // Load user data

        // Set button click listener
        buttonSave.setOnClickListener(v -> saveUserData());

        return view;
    }

    private void setupSpinners() {
        ArrayAdapter<String> cookingSkillAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, answers[0]);
        cookingSkillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCookingSkill.setAdapter(cookingSkillAdapter);

        ArrayAdapter<String> timeAllocationAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, answers[1]);
        timeAllocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeAllocation.setAdapter(timeAllocationAdapter);

        ArrayAdapter<String> servingQuantityAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, answers[2]);
        servingQuantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServingQuantity.setAdapter(servingQuantityAdapter);

        ArrayAdapter<String> dietaryRestrictionsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, answers[3]);
        dietaryRestrictionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDietaryRestrictions.setAdapter(dietaryRestrictionsAdapter);
    }

    private void loadUserData() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query to find the user document based on the uid field
        db.collection("users")
                .whereEqualTo("uid", currentUserUid) // Query by uid field
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0); // Get the first matching document

                            // Set user name
                            // userName.setText(document.getString("name"));
                            // Set profile picture (if applicable)
                            // Example: profilePicture.setImageResource(R.drawable.your_profile_image);

                            // Set initial values for the spinners based on Firestore data
                            String answer1 = document.getString("answer1");
                            String answer2 = document.getString("answer2");
                            String answer3 = document.getString("answer3");
                            String answer4 = document.getString("answer4");

                            // Set selected item for cooking skill
                            int cookingSkillPosition = getPositionInArray(answers[0], answer1);
                            spinnerCookingSkill.setSelection(cookingSkillPosition);

                            // Set selected item for time allocation
                            int timeAllocationPosition = getPositionInArray(answers[1], answer2);
                            spinnerTimeAllocation.setSelection(timeAllocationPosition);

                            // Set selected item for serving quantity
                            int servingQuantityPosition = getPositionInArray(answers[2], answer3);
                            spinnerServingQuantity.setSelection(servingQuantityPosition);

                            // Set selected item for dietary restrictions
                            int dietaryRestrictionsPosition = getPositionInArray(answers[3], answer4);
                            spinnerDietaryRestrictions.setSelection(dietaryRestrictionsPosition);
                        } else {
                            Toast.makeText(getActivity(), "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error loading user data.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private int getPositionInArray(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return 0; // Return default position if not found
    }

    private void saveUserData() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a map to hold the updated user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("answer1", spinnerCookingSkill.getSelectedItem().toString());
        userData.put("answer2", spinnerTimeAllocation.getSelectedItem().toString());
        userData.put("answer3", spinnerServingQuantity.getSelectedItem().toString());
        userData.put("answer4", spinnerDietaryRestrictions.getSelectedItem().toString());

        // Save the updated data in Firestore
        db.collection("users").document(currentUserUid) // Update document by UID
                .update(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "User data updated successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error updating user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate the menu with logout option
        inflater.inflate(R.menu.account_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the logout action
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Log out the user
        Intent intent = new Intent(getActivity(), StartCooking.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the activity stack
        startActivity(intent);
    }
}
