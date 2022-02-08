package com.example.recipegenerator.models;

import java.sql.Date;

public class UserIngredient {
    private int User_ID;
    private int Ingredient_ID;
    private Date bestBefore;
    private Date useBy;
    private int quantity;

    public UserIngredient(int user_ID, int ingredient_ID, Date bestBefore, Date useBy, int quantity) {
        User_ID = user_ID;
        Ingredient_ID = ingredient_ID;
        this.bestBefore = bestBefore;
        this.useBy = useBy;
        this.quantity = quantity;
    }

    public UserIngredient(int user_ID, int ingredient_ID, int quantity) {
        User_ID = user_ID;
        Ingredient_ID = ingredient_ID;
        this.quantity = quantity;
    }

    public UserIngredient() {
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public int getIngredient_ID() {
        return Ingredient_ID;
    }

    public void setIngredient_ID(int ingredient_ID) {
        Ingredient_ID = ingredient_ID;
    }

    public Date getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(Date bestBefore) {
        this.bestBefore = bestBefore;
    }

    public Date getUseBy() {
        return useBy;
    }

    public void setUseBy(Date useBy) {
        this.useBy = useBy;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "UserIngredient{" +
                "User_ID=" + User_ID +
                ", Ingredient_ID=" + Ingredient_ID +
                ", bestBefore=" + bestBefore +
                ", useBy=" + useBy +
                ", quantity=" + quantity +
                '}';
    }
}
