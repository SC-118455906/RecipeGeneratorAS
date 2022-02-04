package com.example.recipegenerator.models;

public class User {
    private int ID;
    private String First_Name;
    private String Surname;
    private String Allergens;

    public User(int ID, String first_Name, String surname, String allergens) {
        this.ID = ID;
        First_Name = first_Name;
        Surname = surname;
        Allergens = allergens;
    }

    public User() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getAllergens() {
        return Allergens;
    }

    public void setAllergens(String allergens) {
        Allergens = allergens;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", First_Name='" + First_Name + '\'' +
                ", Surname='" + Surname + '\'' +
                ", Allergens='" + Allergens + '\'' +
                '}';
    }
}
