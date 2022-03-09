package com.example.recipegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipegenerator.models.IngredientForList;
import com.example.recipegenerator.models.RecipeIngredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CustomRecipe extends AppCompatActivity {

    protected static final String TAG = "CustomRecipe";
    public static final String RECIPE = "RECIPE";
    public static final String RECIPE_INGREDIENTS = "RECIPE_INGREDIENTS";

    EditText et_RecipeName, et_IngredientWeight, et_RecipeDesc;
    AutoCompleteTextView et_AddIngredientToRecipe;
    Button btn_AddRecipe, btn_AddIngToRecipe;
    ListView lst_IngredientsInRecipe;
    Spinner sp_Measurment;
    ArrayAdapter arrayForList;
    List<RecipeIngredient> ingredientsForDB = new ArrayList<RecipeIngredient>();
    CheckBox chk_Veggie, chk_Vegan;
    BottomNavigationView bottomNavigationView;

    boolean isVeggie;
    boolean isVegan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_recipe);

        //declaring all the items in the view
        btn_AddRecipe = findViewById(R.id.btn_SaveRecipe);
        et_RecipeName = findViewById(R.id.et_RecipeName);
        et_AddIngredientToRecipe = findViewById(R.id.et_AddIngredientToRecipe);
        et_IngredientWeight = findViewById(R.id.et_IngredientWeight);
        et_RecipeDesc = findViewById(R.id.et_RecipeDesc);
        btn_AddIngToRecipe = findViewById(R.id.btn_AddIngToRecipe);
        lst_IngredientsInRecipe = findViewById(R.id.lst_IngredientsInRecipe);
        sp_Measurment = findViewById(R.id.sp_Measurement);
        chk_Veggie = findViewById(R.id.chk_CustVegetarian);
        chk_Vegan = findViewById(R.id.chk_CustVegan);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        int userID = getUserID();

        arrayForList = new ArrayAdapter<IngredientForList>(CustomRecipe.this, android.R.layout.simple_list_item_1);

        ArrayAdapter<String> ingredients = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        getAllIngredients(ingredients);
        et_AddIngredientToRecipe.setAdapter(ingredients);

        isVeggie = false;
        isVegan = false;

        chk_Veggie.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isVeggie = true;
            } else {
                isVeggie = false;
            }
        });

        chk_Vegan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isVegan = true;
            } else {
                isVegan = false;
            }
        });

        btn_AddIngToRecipe.setOnClickListener((v) ->{
            if(!ingredientIsEmpty() && !quantityIsEmpty()) {
                String ingredientName = et_AddIngredientToRecipe.getText().toString().toUpperCase();
                int quantity = Integer.parseInt(String.valueOf(et_IngredientWeight.getText()));
                String measurement = sp_Measurment.getSelectedItem().toString();
                RecipeIngredient ingredient = getIngredientByName(ingredientName, quantity, measurement);
                addIngredientToList(ingredient);

                //clearing text and refocusing cursor
                clearText();
            } else {
                Toast.makeText(this, "Please enter valid input in both the ingredient name and quantity fields", Toast.LENGTH_SHORT).show();
            }
        });

        btn_AddRecipe.setOnClickListener((v) ->{
            //make sure all fields have been filled in
            if(!ingredientsForDB.isEmpty() && !nameIsEmpty() && !descIsEmpty()) {
                //declaring the name and description
                String recipeName = et_RecipeName.getText().toString();
                String recipeDesc = et_RecipeDesc.getText().toString();
                String allergens = checkCheckBoxes();
                addRecipeToDatabase(recipeName, recipeDesc, allergens);
                addRecipesIngredientsToDatabase(recipeName, ingredientsForDB);
            } else{
                Toast.makeText(this, "One or more fields are blank. Please enter a name, ingredient list and description", Toast.LENGTH_SHORT).show();
            }
        });

        lst_IngredientsInRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ingredient = (String) parent.getItemAtPosition(position);
                arrayForList.remove(ingredient);
                ingredientsForDB.remove(position);
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.CustomRecipe);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.MainActivity:
                        switchActivity(MainActivity.class, 0);
                        break;
                    case R.id.Ingredients:
                        switchActivity(MainActivity.class, userID);
                        break;
                    case R.id.FindRecipes:
                        switchActivity(Recipes.class, userID);
                        break;
                    case R.id.CustomRecipe:
                        break;
                }
                return true;
            }
        });
    }

    private String checkCheckBoxes() {
        String allergens = "";
        if(isVeggie && !isVegan){
            allergens = "VEGETARIAN";
        } else if(isVeggie && isVegan){
            allergens = "VEGETARIAN,VEGAN";
        } else if(isVegan && !isVeggie){
            allergens = "VEGAN";
        } else{
            allergens = "";
        }
        return allergens;
    }


    private void getAllIngredients(ArrayAdapter<String> ingredients) {
        String sql = "SELECT NAME FROM INGREDIENTS";
        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            do{
                ingredients.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }

        db.close();
    }

    private void addRecipesIngredientsToDatabase(String recipeName, List<RecipeIngredient> ingredientsForDB) {
        int recipeID = getRecipeID(recipeName);
        if(recipeID!=0) {
            SQLiteDatabase db = getSqLiteDatabase(true);

            ContentValues contentValues = new ContentValues();

            writeValuesToDatabase(ingredientsForDB, recipeID, db, contentValues);
            db.close();
        } else {
            Toast.makeText(this, "Error finding recipe. Please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeValuesToDatabase(List<RecipeIngredient> ingredientsForDB, int recipeID, SQLiteDatabase db, ContentValues contentValues) {
        for (int i = 0; i < ingredientsForDB.size(); i++) {
            RecipeIngredient ingredient = ingredientsForDB.get(i);
            contentValues.put("RECIPE_ID", recipeID);
            contentValues.put("INGREDIENT_ID", ingredient.getIngredientID());
            contentValues.put("QUANTITY", ingredient.getQuantity());
            contentValues.put("MEASUREMENT", ingredient.getMeasurement());

            db.insert(RECIPE_INGREDIENTS, null, contentValues);
        }
    }

    private int getRecipeID(String recipeName) {
        int recipeID = 0;
        String sql = "SELECT RECIPE_ID FROM RECIPE WHERE UPPER(NAME) = '" + recipeName.toUpperCase() + "'";

        SQLiteDatabase db = getSqLiteDatabase(false);


        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            recipeID = cursor.getInt(0);
        }
        db.close();

        return recipeID;
    }

    private void clearText() {
        et_AddIngredientToRecipe.setText(null);
        et_AddIngredientToRecipe.requestFocus();
        et_IngredientWeight.setText(null);
    }

    private boolean descIsEmpty() {
        //https://stackoverflow.com/questions/6290531/how-do-i-check-if-my-edittext-fields-are-empty
        return et_RecipeDesc.getText().toString().trim().length() == 0;
    }
    private boolean nameIsEmpty() {
        return et_RecipeName.getText().toString().trim().length() == 0;
    }
    private boolean quantityIsEmpty() {
        return et_IngredientWeight.getText().toString().trim().length() == 0;
    }
    private boolean ingredientIsEmpty() {
        return et_AddIngredientToRecipe.getText().toString().trim().length() == 0;
    }

    private void addIngredientToList(RecipeIngredient ingredient) {
        try {
            ingredientsForDB.add(ingredient);
            arrayForList.add(ingredient.toString());
            lst_IngredientsInRecipe.setAdapter(arrayForList);
        } catch(NullPointerException e){
            Log.e(TAG, "No value being passed to add ingredient to list: "+ e.toString());
            Toast.makeText(this, "Cannot find this ingredient in the database. Please add it as a custom ingredient if you wish to continue", Toast.LENGTH_SHORT).show();
        }
    }

    private void addRecipeToDatabase(String recipeName, String recipeDesc, String allergens) {
        SQLiteDatabase db = getSqLiteDatabase(true);

        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", recipeName);
        contentValues.put("DESCRIPTION", recipeDesc);
        contentValues.put("ALLERGENS", allergens);

        long result = db.insert(RECIPE, null, contentValues);

        if(result == -1){
            Log.println(Log.INFO, TAG, "Could not write recipe to database please try again later");
        } else {
            Log.println(Log.INFO, TAG, "Successfully wrote recipe to database!");
        }

        db.close();
    }

    public RecipeIngredient getIngredientByName(String name, int quantity, String measurement){
        RecipeIngredient ingredient = null;
        SQLiteDatabase db = getSqLiteDatabase(false);
        try {
            String getIngredientFromDBSQL = "SELECT INGREDIENT_ID, NAME FROM INGREDIENTS WHERE UPPER(INGREDIENTS.NAME) = '" + name + "'";

            Cursor cursor = db.rawQuery(getIngredientFromDBSQL, null);
            ingredient = getIngredientForList(quantity, ingredient, measurement, cursor);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "error getting ingredient by name"+ mSQLException.toString());
            Toast.makeText(this, "Cannot find this ingredient in the database. Please add it as a custom ingredient if you wish to continue", Toast.LENGTH_SHORT).show();
        }
        db.close();
        return ingredient;
    }

    private RecipeIngredient getIngredientForList(int quantity, RecipeIngredient ingredient, String measurement, Cursor cursor) {
        if (cursor != null) {
            if(cursor.moveToFirst()){
                do {
                    int ingredientID = cursor.getInt(0);
                    String ingredientName = cursor.getString(1);
                    ingredient = new RecipeIngredient(ingredientID, ingredientName, quantity, measurement);
                } while(cursor.moveToNext());
            } else {
            }
        } else {
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

    private void switchActivity(Class _class, int userID) {
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        switchActivityIntent.putExtra("userID", userID);
        startActivity(switchActivityIntent);
    }

    //gets the currently logged in users ID
    private int getUserID() {
        int userID = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getInt("userID");
        }

        return userID;
    }
}