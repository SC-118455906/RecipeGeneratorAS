package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.User;
import com.example.recipegenerator.models.IngredientForList;

import java.util.ArrayList;

public class Ingredients extends AppCompatActivity {

    ArrayAdapter arrayAdapter;

    Button btn_IngToHome, btn_IngToCustIng, btn_Add;
    EditText et_IngredientName, et_quantity;
    ListView lst_CurrentIngredients;
    TestAdapter testAdapter;

    User currentUser = new User(2, "Dan", "Murphy", "Coeliac", null);

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

        lst_CurrentIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 IngredientForList ingredient = (IngredientForList) parent.getItemAtPosition(position);
                 deleteItem(currentUser.getID(), ingredient.getIngredientID());
                 getUsersCurrentIngredients(currentUser.getID());
            }
        });

        getUsersCurrentIngredients(currentUser.getID());
    }

    private void addIngredient(String name, int userID, int quantity) {
        Ingredient ingredient = testAdapter.getIngredientByName(name, quantity);
//        addIngredientToList(ingredient);
        testAdapter.writeUser_Ingredients(ingredient, userID, quantity);
        getUsersCurrentIngredients(currentUser.getID());
    }



    private void deleteItem(int userID, int ingredientID){
        SQLiteDatabase db = getSqLiteDatabase(true);
        String sql = "DELETE FROM USER_INGREDIENTS WHERE USER_ID = " + userID + " AND INGREDIENT_ID = " + ingredientID;

        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            Toast.makeText(this, "Successfully deleted item", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Could not delete item. Please try again", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void switchActivity(Class _class) {
        testAdapter.close();
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        startActivity(switchActivityIntent);
    }

    private void getUsersCurrentIngredients(int userID){
        String getUsersCurrentIngredientsFromDB = "SELECT INGREDIENTS.INGREDIENT_ID, INGREDIENTS.NAME, USER_INGREDIENTS.QUANTITY FROM INGREDIENTS, USER_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = INGREDIENTS.INGREDIENT_ID AND USER_INGREDIENTS.USER_ID = " + userID;

        SQLiteDatabase db = getSqLiteDatabase(false);
        arrayAdapter = new ArrayAdapter<IngredientForList>(Ingredients.this, android.R.layout.simple_list_item_1);
        Cursor cursor = db.rawQuery(getUsersCurrentIngredientsFromDB, null);

        addIngredientsToArrayAdapterForIngredientsList(cursor);

        lst_CurrentIngredients.setAdapter(arrayAdapter);
    }

    private SQLiteDatabase getSqLiteDatabase(boolean writable) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(Ingredients.this);
        if(writable){
            db = dbHelper.getWritableDatabase();
        } else {
            db = dbHelper.getReadableDatabase();
        }
        return db;
    }
    private void addIngredientsToArrayAdapterForIngredientsList(Cursor cursor) {
        int ingredientID;
        String ingredientName;
        int ingredientQuantity;
        if(cursor.moveToFirst()){
            do{
                ingredientID = cursor.getInt(0);
                ingredientName = cursor.getString(1);
                ingredientQuantity = cursor.getInt(2);

                IngredientForList ingredientForList = new IngredientForList(ingredientID, ingredientName, ingredientQuantity);
                arrayAdapter.add(ingredientForList);

            } while(cursor.moveToNext());
        } else {

        }
    }
}