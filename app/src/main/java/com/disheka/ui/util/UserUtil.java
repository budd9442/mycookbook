package com.disheka.ui.util;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserUtil {

    private FirebaseFirestore db;

    public UserUtil() {
        db = FirebaseFirestore.getInstance();
    }

    public String getUsernameByUid(String uid) {
        try {
            // Synchronously get the document
            DocumentSnapshot document = db.collection("users")
                    .document(uid)
                    .get().getResult(); // This is blocking and should be called on a background thread in a real app

            if (document.exists()) {
                return document.getString("name"); // Assuming the field in Firestore is named "name"
            } else {
                return null; // User not found
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as needed
            return null; // Return null in case of error
        }
    }
}
