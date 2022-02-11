package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.IngredientForList;
import com.example.recipegenerator.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomRecipe extends AppCompatActivity {

    protected static final String TAG = "CustomRecipe";

    EditText et_RecipeName, et_AddIngredientToRecipe, et_IngredientWeight, et_RecipeDesc;
    Button btn_AddRecipe, btn_AddIngToRecipe;
    ListView lst_IngredientsInRecipe;
    ArrayAdapter arrayAdapter;
    List<IngredientForList> ingredientsForList;

    User currentUser = new User(1, "Sean", "Cronin", null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_recipe);

        btn_AddRecipe = findViewById(R.id.btn_AddRecipe);
        et_RecipeName = findViewById(R.id.et_RecipeName);
        et_AddIngredientToRecipe = findViewById(R.id.et_AddIngredientToRecipe);
        et_IngredientWeight = findViewById(R.id.et_IngredientWeight);
        et_RecipeDesc = findViewById(R.id.et_RecipeDesc);
        btn_AddIngToRecipe = findViewById(R.id.btn_AddIngToRecipe);
        lst_IngredientsInRecipe = findViewById(R.id.lst_IngredientsInRecipe);

        arrayAdapter = new ArrayAdapter<IngredientForList>(CustomRecipe.this, android.R.layout.simple_list_item_1);

        btn_AddIngToRecipe.setOnClickListener((v) ->{
            String ingredientName = et_AddIngredientToRecipe.getText().toString().toUpperCase();
            int quantity = Integer.parseInt(String.valueOf(et_IngredientWeight.getText()));
            IngredientForList ingredient = getIngredientByName(ingredientName, quantity);
            addIngredientToList(ingredient);
        });

        btn_AddRecipe.setOnClickListener((v) ->{
            ingredientsForList = new ArrayList<IngredientForList>();
            for(int i =0; i < arrayAdapter.getCount(); i++){
                IngredientForList ing = (IngredientForList) arrayAdapter.getItem(i);
                ingredientsForList.add(ing);
            }
            addRecipeToDatabase();
        });
    }

    private void addIngredientToList(IngredientForList ingredient) {
        arrayAdapter.add(ingredient);
        lst_IngredientsInRecipe.setAdapter(arrayAdapter);
    }

    private void addRecipeToDatabase() {
    }

    public IngredientForList getIngredientByName(String name, int quantity){
        IngredientForList ingredient = null;
        SQLiteDatabase db = getSqLiteDatabase(false);
        try {
            String getIngredientFromDBSQL = "SELECT INGREDIENT_ID, NAME FROM INGREDIENTS WHERE UPPER(INGREDIENTS.NAME) = '" + name + "'";

            Cursor cursor = db.rawQuery(getIngredientFromDBSQL, null);
            ingredient = getIngredientForList(quantity, ingredient, cursor);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "error getting ingredient by name"+ mSQLException.toString());
            throw mSQLException;
        }
        db.close();
        return ingredient;
    }

    private IngredientForList getIngredientForList(int quantity, IngredientForList ingredient, Cursor cursor) {
        if (cursor != null) {
            if(cursor.moveToFirst()){
                do {
                    int ingredientID = cursor.getInt(0);
                    String ingredientName = cursor.getString(1);
                    ingredient = new IngredientForList(ingredientID, ingredientName, quantity);
                } while(cursor.moveToNext());
            } else {

            }
        }
        return ingredient;
    }

    private SQLiteDatabase getSqLiteDatabase(boolean writable) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(CustomRecipe.this);
        if(writable){
            db = dbHelper.getWritableDatabase();
        } else {
            db = dbHelper.getReadableDatabase();
        }
        return db;
    }
}