package com.example.recipegenerator.models;

public class Ingredient {
    private int Ingredient_ID;
    private String Name;
    private String Type;

    public Ingredient() {
    }

    public Ingredient(int ingredient_ID, String name, String type) {
        Ingredient_ID = ingredient_ID;
        Name = name;
        Type = type;
    }

    public int getIngredient_ID() {
        return Ingredient_ID;
    }

    public void setIngredient_ID(int ingredient_ID) {
        Ingredient_ID = ingredient_ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "Ingredient_ID=" + Ingredient_ID +
                ", Name='" + Name + '\'' +
                ", Type='" + Type + '\'' +
                '}';
    }
}
