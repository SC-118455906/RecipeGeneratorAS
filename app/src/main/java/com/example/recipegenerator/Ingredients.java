package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.User;
import com.example.recipegenerator.models.UserIngredientForList;

public class Ingredients extends AppCompatActivity {

    ArrayAdapter arrayAdapter;

    Button btn_IngToHome, btn_IngToCustIng, btn_Add;
    EditText et_IngredientName, et_quantity;
    ListView lst_CurrentIngredients;
    TestAdapter testAdapter;

    User currentUser = new User(6, "Dan", "Murphy", "Coeliac");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        testAdapter = new TestAdapter(Ingredients.this);
        testAdapter.open();

        btn_IngToHome = findViewById(R.id.btn_IngToHome);
        btn_Add = findViewById(R.id.btn_AddIngredient);
        btn_IngToCustIng = findViewById(R.id.btn_IngToCustIng);
        et_IngredientName = findViewById(R.id.et_CustIngredientName);
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
            addIngredient(name, userID, quantity);
        });

        getUsersCurrentIngredients(currentUser.getID());
    }

    private void addIngredient(String name, int userID, int quantity) {
        Ingredient ingredient = testAdapter.getIngredientByName(name, quantity);
        addIngredientToList(ingredient);
        testAdapter.writeUser_Ingredients(ingredient, userID, quantity);
    }

    private void addIngredientToList(Ingredient ingredient) {
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

    private void getUsersCurrentIngredients(int userID){
        String getUsersCurrentIngredientsFromDB = "SELECT INGREDIENTS.NAME, USER_INGREDIENTS.QUANTITY FROM INGREDIENTS, USER_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = INGREDIENTS.INGREDIENT_ID AND USER_INGREDIENTS.USER_ID = " + userID;

        SQLiteDatabase db = getSqLiteDatabase();
        arrayAdapter = new ArrayAdapter<UserIngredientForList>(Ingredients.this, android.R.layout.simple_list_item_1);
        Cursor cursor = db.rawQuery(getUsersCurrentIngredientsFromDB, null);
        Cursor cursor2 = db.rawQuery("SELECT TYPE FROM FOODTYPE", null);

        addIngredientsToArrayAdapterForIngredientsList(cursor);

        lst_CurrentIngredients.setAdapter(arrayAdapter);
    }

    private SQLiteDatabase getSqLiteDatabase() {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(Ingredients.this);
        db = dbHelper.getReadableDatabase();
        return db;
    }

    private void addIngredientsToArrayAdapterForIngredientsList(Cursor cursor) {
        String ingredientName;
        int ingredientQuantity;
        if(cursor.moveToFirst()){
            do{
                ingredientName = cursor.getString(0);
                ingredientQuantity = cursor.getInt(1);

                UserIngredientForList ingredientForList = new UserIngredientForList(ingredientName, ingredientQuantity);
                arrayAdapter.add(ingredientForList);

            } while(cursor.moveToNext());
        } else {

        }
    }
}