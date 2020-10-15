package ru.koldaev.entity;

public class Review {

    private String user_login;
    private int recipeId;
    private String review;
    private String date;
    private String recipeName;

    public Review(String user_login, int recipeId, String review, String date, String recipeName) {
        this.user_login = user_login;
        this.recipeId = recipeId;
        this.review = review;
        this.date = date;
        this.recipeName = recipeName;
    }
    public Review() {}

    public String getUser() {
        return user_login;
    }

    public void setUser(String user_login) {
        this.user_login = user_login;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
}
