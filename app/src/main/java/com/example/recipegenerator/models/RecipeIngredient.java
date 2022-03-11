package com.example.recipegenerator.models;

public class RecipeIngredient {
    private int ingredientID;
    private String ingredientName;
    private int quantity;
    private String measurement;

    public RecipeIngredient(int ingredientID, String ingredientName, int quantity, String measurement) {
        this.ingredientID = ingredientID;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.measurement = measurement;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    @Override
    public String toString() {
        return "\u2022" + ingredientName + ", Quantity: " + quantity + " " + measurement;
    }
}
