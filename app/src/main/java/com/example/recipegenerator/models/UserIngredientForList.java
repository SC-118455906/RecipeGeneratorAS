package com.example.recipegenerator.models;

public class UserIngredientForList {
    String ingredientName;
    int quantity;

    public UserIngredientForList() {
    }

    public UserIngredientForList(String ingredientName, int quantity) {
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

    @Override
    public String toString() {
        return "UserIngredientForList{" +
                "ingredientName='" + ingredientName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
