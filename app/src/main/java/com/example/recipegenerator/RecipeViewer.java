package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeViewer extends AppCompatActivity {

    protected static final String TAG = "RecipeViewer";
    public static final String USER_RECIPES = "USER_RECIPES";

    TextView txt_RecipeName;
    EditText et_RecipeDescription;
    ListView lst_RecipeIngredients;
    Button btn_Home, btn_Save;
    ArrayAdapter arrayAdapter;
    RatingBar rb_RecipeReview;

    Float newRating = Float.valueOf(0);
    Float existingRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer);

        txt_RecipeName = findViewById(R.id.txt_RecipesName);
        et_RecipeDescription = findViewById(R.id.et_RecipeDescription);
        lst_RecipeIngredients = findViewById(R.id.lst_RecipesIngredients);
        btn_Home = findViewById(R.id.btn_RVToHome);
        rb_RecipeReview = findViewById(R.id.rb_RecipeReview);
        btn_Save = findViewById(R.id.btn_Save);

        int userID = getUserID();
        int recipeID = getRecipeID();

        //gets rating of a recipe if it exists
        existingRating = getRating(userID, recipeID);

        //if rating exists set the rating to reflect this so users can see what they rated the recipe last time they tried it
        if (!existingRating.equals(0)) {
            rb_RecipeReview.setRating(existingRating);
        }

        //ensures the user cannot edit the recipe description
        et_RecipeDescription.setFocusable(false);

        btn_Home.setOnClickListener((v) -> {
            switchActivity(MainActivity.class);
        });

        btn_Save.setOnClickListener((v) -> {
            Toast.makeText(this, String.valueOf(newRating), Toast.LENGTH_SHORT).show();
            if (newRating != existingRating) {
                saveRating(userID, recipeID, newRating);
            }
        });

        rb_RecipeReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                newRating = ratingBar.getRating();
            }
        });


        getRecipe(recipeID);

        arrayAdapter = getIngredients(recipeID);

        lst_RecipeIngredients.setAdapter(arrayAdapter);

    }


    //was able to use this tutorial for information about getting the rating from a rating bar: https://abhiandroid.com/ui/ratingbar
    private Float getRating(int userID, int recipeID) {
        Float rating;
        String sql = "SELECT RATING FROM USER_RECIPES WHERE USER_ID = " + userID + " AND RECIPE_ID = " + recipeID;

        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            rating = cursor.getFloat(0);
        } else {
            rating = Float.valueOf(0);
        }
        db.close();
        return rating;
    }

    private void saveRating(int userID, int recipeID, Float newRating) {
        SQLiteDatabase db = getSqLiteDatabase(true);
        if(existingRating > 0){
            String sql = "UPDATE USER_RECIPES SET RATING = " + newRating + " WHERE USER_ID = " + userID + " AND RECIPE_ID = " + recipeID;
            Cursor cursor = db.rawQuery(sql, null);

            if(cursor.moveToFirst()){
                Log.println(Log.INFO, TAG, "Could not update recipe rating");
            } else{
                Log.println(Log.INFO, TAG, "Successfully updated recipe rating");
            }
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("USER_ID", userID);
            contentValues.put("RECIPE_ID", recipeID);
            contentValues.put("RATING", newRating);

            long result = db.insert(USER_RECIPES, null, contentValues);

            if(result == -1){
                Log.println(Log.INFO, TAG, "Could not add rating to recipe. Please try again later");
            } else {
                Log.println(Log.INFO, TAG, "Successfully wrote recipe to database!");
            }
        }

        db.close();
    }

    private ArrayAdapter getIngredients(int recipeID) {
        String sql = "SELECT R.INGREDIENT_ID, UPPER(I.NAME), R.QUANTITY, R.MEASUREMENT FROM RECIPE_INGREDIENTS R, INGREDIENTS I WHERE R.INGREDIENT_ID = I.INGREDIENT_ID AND R.RECIPE_ID = " + recipeID;

        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);

        arrayAdapter = new ArrayAdapter<String>(RecipeViewer.this, android.R.layout.simple_list_item_1);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int quantity = cursor.getInt(2);
                String measurement = cursor.getString(3);
                String ingredient = "\u2022" + name + ", Quantity: " + quantity + " " + measurement;
                arrayAdapter.add(ingredient);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Error fetching recipe details. Please return to home screen and try again", Toast.LENGTH_SHORT).show();
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
            txt_RecipeName.setText(recipeName);
            et_RecipeDescription.setText(recipeDesc);
        } else {
            Toast.makeText(this, "Error fetching recipe details. Please try again later", Toast.LENGTH_SHORT).show();
        }

        //set textview code adapted from this post on stackoverflow https://stackoverflow.com/questions/13452991/change-textview-text


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

    private int getUserID() {
        int userID = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getInt("userID");
        }

        return userID;
    }

    private void switchActivity(Class _class) {
        Intent switchActivityIntent = new Intent(this, _class);
        startActivity(switchActivityIntent);
    }
}