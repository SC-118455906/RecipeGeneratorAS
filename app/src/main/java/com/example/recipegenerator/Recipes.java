package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.recipegenerator.models.User;

import java.util.ArrayList;
import java.util.List;

public class Recipes extends AppCompatActivity {

    protected static final String TAG = "Recipes";

    Button btn_FindRecipes;
    ListView lst_Recipes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        btn_FindRecipes = findViewById(R.id.btn_FindRecipes);
        lst_Recipes = findViewById(R.id.lstRecipes);

        int userID = getUserID();

        btn_FindRecipes.setOnClickListener((v)->{
            findAllEligibleRecipes(userID);
        });

        lst_Recipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String recipeName = lst_Recipes.getItemAtPosition(position).toString().toUpperCase();
                getRecipeDetails(recipeName, userID);
            }
        });
    }

    private void getRecipeDetails(String recipeName, int userID) {
        String sql = "SELECT RECIPE_ID FROM RECIPE WHERE UPPER(NAME) = '" + recipeName + "'";
        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);
        int id = 0;

        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }

        if(id!=0){
            switchActivity(RecipeViewer.class, id, userID);
        }

        db.close();
    }

    private void findAllEligibleRecipes(int userID) {
        String sql = "SELECT DISTINCT(RECIPE.NAME),(RECIPE.DESCRIPTION) FROM RECIPE, USER_INGREDIENTS, RECIPE_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = RECIPE_INGREDIENTS.INGREDIENT_ID AND RECIPE.RECIPE_ID = RECIPE_INGREDIENTS.RECIPE_ID AND USER_INGREDIENTS.USER_ID = " + userID;
        SQLiteDatabase db = getSqLiteDatabase(false);
        List<String> recipes = new ArrayList<String>();

        Cursor cursor = db.rawQuery(sql, null);

        addRecipesToArrayList(recipes, cursor);
        populateAdapterAndSetAsListSource(recipes);

        db.close();
    }

    private void populateAdapterAndSetAsListSource(List<String> recipes) {
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(Recipes.this, android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(recipes);
        lst_Recipes.setAdapter(arrayAdapter);
    }

    private void addRecipesToArrayList(List<String> recipes, Cursor cursor) {
        if(cursor.moveToFirst()) {
            try {
                do{
                    String name = cursor.getString(0);
                    recipes.add(name);
                } while(cursor.moveToNext());
            } catch (Exception e){
                Log.println(Log.ERROR, TAG, e.toString());
            }
        } else{
            recipes.add("You do not have sufficient ingredients at the moment to make any recipes. Please update your ingredients list!");
        }
    }

    private SQLiteDatabase getSqLiteDatabase(boolean writable) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(Recipes.this);
        if(writable){
            db = dbHelper.getWritableDatabase();
        } else {
            db = dbHelper.getReadableDatabase();
        }
        return db;
    }

    private int getUserID() {
        int userID = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getInt("userID");
        }

        return userID;
    }

    private void switchActivity(Class _class, int id, int userID) {
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        switchActivityIntent.putExtra("recipeID", id);
        switchActivityIntent.putExtra("userID", userID);
        startActivity(switchActivityIntent);
    }
}