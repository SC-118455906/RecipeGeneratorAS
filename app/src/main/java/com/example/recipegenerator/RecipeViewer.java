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
import android.widget.TextView;

import com.example.recipegenerator.models.IngredientForList;

public class RecipeViewer extends AppCompatActivity {

    TextView txt_RecipeName;
    EditText et_RecipeDescription;
    ListView lst_RecipeIngredients;
    Button btn_Home;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer);

        txt_RecipeName = findViewById(R.id.txt_RecipesName);
        et_RecipeDescription = findViewById(R.id.et_RecipeDescription);
        lst_RecipeIngredients = findViewById(R.id.lst_RecipesIngredients);
        btn_Home = findViewById(R.id.btn_RVToHome);

        et_RecipeDescription.setFocusable(false);

        btn_Home.setOnClickListener((v) -> {
            switchActivity(MainActivity.class);
        });

        int recipeID = getRecipeID();

        getRecipe(recipeID);

        arrayAdapter = getIngredients(recipeID);

        lst_RecipeIngredients.setAdapter(arrayAdapter);

    }

    private ArrayAdapter getIngredients(int recipeID) {
        String sql = "SELECT R.INGREDIENT_ID, UPPER(I.NAME), R.QUANTITY, R.MEASUREMENT FROM RECIPE_INGREDIENTS R, INGREDIENTS I WHERE R.INGREDIENT_ID = I.INGREDIENT_ID AND R.RECIPE_ID = " + recipeID;

        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);

        arrayAdapter = new ArrayAdapter<String>(RecipeViewer.this, android.R.layout.simple_list_item_1);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int quantity = cursor.getInt(2);
                String measurement = cursor.getString(3);
                String ingredient = "\u2022" + name + ", Quantity: " + quantity + " " + measurement;
                arrayAdapter.add(ingredient);
            } while(cursor.moveToNext());
        }

        db.close();
        return arrayAdapter;
    }

    private void getRecipe(int recipeID) {
        String sql = "SELECT NAME, DESCRIPTION FROM RECIPE WHERE RECIPE_ID = " + recipeID;

        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);

        String recipeName = null;
        String recipeDesc = null;

        if (cursor.moveToFirst()) {
            recipeName = cursor.getString(0);
            recipeDesc = cursor.getString(1);
        }

        //set textview code adapted from this post on stackoverflow https://stackoverflow.com/questions/13452991/change-textview-text
        txt_RecipeName.setText(recipeName);
        et_RecipeDescription.setText(recipeDesc);

        db.close();
    }

    private int getRecipeID() {
        int recipeID = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipeID = extras.getInt("recipeID");
        }

        return recipeID;
    }

    private SQLiteDatabase getSqLiteDatabase(boolean writable) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(RecipeViewer.this);
        if (writable) {
            db = dbHelper.getWritableDatabase();
        } else {
            db = dbHelper.getReadableDatabase();
        }
        return db;
    }

    private void switchActivity(Class _class) {
        Intent switchActivityIntent = new Intent(this, _class);
        startActivity(switchActivityIntent);
    }
}