package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.User;

import java.lang.reflect.Array;
import java.util.List;

public class Ingredients extends AppCompatActivity {

    ArrayAdapter arrayAdapter;

    Button btn_IngToHome, btn_IngToCustIng, btn_Add;
    EditText et_IngredientName, et_quantity;
    ListView lst_CurrentIngredients;
    TestAdapter testAdapter;

    User currentUser = new User(1, "Sean", "Cronin", null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        testAdapter = new TestAdapter(Ingredients.this);
        testAdapter.open();

        btn_IngToHome = findViewById(R.id.btn_IngToHome);
        btn_Add = findViewById(R.id.btn_AddIngredient);
        btn_IngToCustIng = findViewById(R.id.btn_IngToCustIng);
        et_IngredientName = findViewById(R.id.et_IngredientName);
        et_quantity = findViewById(R.id.et_IngredientQuantity);
        lst_CurrentIngredients = findViewById(R.id.lst_CurrentIngredients);

        btn_IngToHome.setOnClickListener((v) -> {
            switchActivity(MainActivity.class);
        });

        btn_IngToCustIng.setOnClickListener((v) -> {
            switchActivity(CustomIngredient.class);
        });

        btn_Add.setOnClickListener((v)->{
            String name = et_IngredientName.getText().toString().toUpperCase();
            int quantity = Integer.parseInt(et_quantity.getText().toString());
            int userID = currentUser.getID();
            addIngToList(name, userID, quantity);
        });
    }

    private void addIngToList(String name, int userID, int quantity) {
        Ingredient ingredient = testAdapter.getIngredientByName(name, userID, quantity);
        arrayAdapter = new ArrayAdapter<Ingredient>(Ingredients.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add(ingredient);
        lst_CurrentIngredients.setAdapter(arrayAdapter);
    }


    private void switchActivity(Class _class) {
        testAdapter.close();
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        startActivity(switchActivityIntent);
    }
}