package ru.koldaev.entity;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private int id;

    private String name;
    private String description;
    private String date_added;
    private int portion;
    private double calories;
    private double proteins;
    private double fats;
    private double carbohydrate;

    private String source;
    private String base;
    private String category;
    private String kitchen;
    private String authorName;

    private List<String> cookMethod;

    private List<String> ingredient;
    private List<String> countIngredient;
    private List<String> unit;

    public Recipe() {
        name = "";
        description = "";
        date_added = "";
        portion = 0;
        calories = 0;
        proteins = 0;
        fats = 0;
        carbohydrate = 0;
        source = "";
        base = "";
        category = "";
        kitchen = "";
        authorName = "";
        cookMethod = new ArrayList<>();
        ingredient = new ArrayList<>();
        countIngredient = new ArrayList<>();
        unit = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public int getPortion() {
        return portion;
    }

    public void setPortion(int portion) {
        this.portion = portion;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKitchen() {
        return kitchen;
    }

    public void setKitchen(String kitchen) {
        this.kitchen = kitchen;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public List<String> getCookMethod() {
        return cookMethod;
    }

    public void setCookMethod(List<String> cookMethod) {
        this.cookMethod = cookMethod;
    }

    public List<String> getIngredient() {
        return ingredient;
    }

    public void setIngredient(List<String> ingredient) {
        this.ingredient = ingredient;
    }

    public List<String> getCountIngredient() {
        return countIngredient;
    }

    public void setCountIngredient(List<String> countIngredient) {
        this.countIngredient = countIngredient;
    }

    public List<String> getUnit() {
        return unit;
    }

    public void setUnit(List<String> unit) {
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
