package com.disheka.model;

import java.io.Serializable;
import java.util.Date;

public class Recipe implements Serializable {
    private String name;
    private String ingredients;
    private String steps;
    private String imageUrl;
    private String time;
    private String[] tags;
    private int rating;
    private String author;
    private String authorUid;
    private Date timestamp;
    private String documentId;

    // Empty constructor required for Firestore
    public Recipe() {}

    public Recipe(String recipeName, String ingredients, String steps, String url, String time, String uid, String author) {
        this.name = recipeName;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = url;
        this.time = time;
        this.authorUid = uid;
        this.author = author;
        this.timestamp = new Date(); // Automatically set current timestamp

    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setDocumentId(String id) {
        this.documentId = id;
    }

    public String getDocumentId(){
        return this.documentId;
    }
}
