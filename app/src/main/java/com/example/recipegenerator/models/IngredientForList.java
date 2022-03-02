package com.example.recipegenerator.models;

public class IngredientForList {
    private int ingredientID;
    private String ingredientName;
    private int quantity;

    public IngredientForList() {
    }

    public IngredientForList(int ingredientID, String ingredientName, int quantity) {
        this.ingredientID = ingredientID;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
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

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    @Override
    public String toString() {
        return "\u2022" + ingredientName + ", Quantity: " + quantity;
    }
}
