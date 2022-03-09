package com.example.recipegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.IngredientForList;
import com.example.recipegenerator.models.RecipeIngredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Ingredients extends AppCompatActivity {

    ArrayAdapter arrayAdapter;
    ArrayAdapter<RecipeIngredient> ingredientsForDeleting;

    Button btn_IngToCustIng, btn_Add;
    AutoCompleteTextView et_IngredientName;
    EditText et_quantity;
    ListView lst_CurrentIngredients;
    TestAdapter testAdapter;
    Spinner sp_Measurements;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        testAdapter = new TestAdapter(Ingredients.this);
        testAdapter.open();

        btn_Add = findViewById(R.id.btn_AddIngredient);
        btn_IngToCustIng = findViewById(R.id.btn_IngToCustIng);
        et_quantity = findViewById(R.id.et_IngredientQuantity);
        lst_CurrentIngredients = findViewById(R.id.lst_CurrentIngredients);
        et_IngredientName = findViewById(R.id.et_IngredientName);
        sp_Measurements = findViewById(R.id.sp_MeasurementIng);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //creating an ArrayAdapter for the autofill textbox
        ArrayAdapter<String> ingredients = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        getAllIngredients(ingredients);
        et_IngredientName.setAdapter(ingredients);

        //getting the current users ID which was passed from the HomeScreen
        int userID = getUserID();


        ingredientsForDeleting = new ArrayAdapter<>(Ingredients.this, android.R.layout.simple_list_item_1);
        //setting the button onClick methods

        btn_IngToCustIng.setOnClickListener((v) -> {
            switchActivity(CustomIngredient.class, userID);
        });

        btn_Add.setOnClickListener((v) -> {
            if (!nameIsEmpty() && !quantityIsEmpty()) {
                String name = et_IngredientName.getText().toString().toUpperCase();
                int quantity = Integer.parseInt(et_quantity.getText().toString());
                String measurement = sp_Measurements.getSelectedItem().toString().toUpperCase();
                addIngredient(name, userID, quantity, measurement);
                clearText();
            } else {
                Toast.makeText(this, "One or more fields are blank. Please enter a valid ingredient name and quantity", Toast.LENGTH_SHORT).show();
            }
        });

        //clicking an item in the list view will remove it from the users list of ingredients
        lst_CurrentIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecipeIngredient ingredient = ingredientsForDeleting.getItem(position);
                deleteItem(userID, ingredient.getIngredientID());
                getUsersCurrentIngredients(userID);
            }
        });

        //this method queries the database to return all the current users ingredients.
        //Is called on creation to populate list view. Is also called whenever a new ingredient is added to
        //or deleted from the listview
        getUsersCurrentIngredients(userID);

        bottomNavigationView.setSelectedItemId(R.id.Ingredients);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.MainActivity:
                        switchActivity(MainActivity.class, 0);
                        break;
                    case R.id.Ingredients:
                        break;
                    case R.id.FindRecipes:
                        switchActivity(Recipes.class, userID);
                        break;
                    case R.id.CustomRecipe:
                        switchActivity(CustomRecipe.class, userID);
                        break;
                }
                return true;
            }
        });
    }

    private void clearText() {
        et_IngredientName.setText(null);
        et_IngredientName.requestFocus();
        et_quantity.setText(null);
    }

    private void getAllIngredients(ArrayAdapter<String> ingredients) {
        String sql = "SELECT NAME FROM INGREDIENTS";
        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                ingredients.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        db.close();
    }

    private void addIngredient(String name, int userID, int quantity, String measurement) {
        Ingredient ingredient = testAdapter.getIngredientByName(name);
        if (ingredient != null) {
            testAdapter.writeUser_Ingredients(ingredient, userID, quantity, measurement);
        } else {
            Toast.makeText(this, "Ingredient cannot be found in database. Please create this ingredient as a custom ingredient if you wish to add it to your list", Toast.LENGTH_SHORT).show();
        }
        getUsersCurrentIngredients(userID);
    }

    private void deleteItem(int userID, int ingredientID) {
        SQLiteDatabase db = getSqLiteDatabase(true);
        String sql = "DELETE FROM USER_INGREDIENTS WHERE USER_ID = " + userID + " AND INGREDIENT_ID = " + ingredientID;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Could not delete item. Please try again", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Successfully deleted item", Toast.LENGTH_SHORT).show();
        }

        //ensure the database is updated with the new information
        db.close();
    }

    private void switchActivity(Class _class, int userID) {
        testAdapter.close();
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        switchActivityIntent.putExtra("userID", userID);
        startActivity(switchActivityIntent);
    }

    private void getUsersCurrentIngredients(int userID) {
        String getUsersCurrentIngredientsFromDB = "SELECT INGREDIENTS.INGREDIENT_ID, INGREDIENTS.NAME, USER_INGREDIENTS.QUANTITY, USER_INGREDIENTS.MEASUREMENT FROM INGREDIENTS, USER_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = INGREDIENTS.INGREDIENT_ID AND USER_INGREDIENTS.USER_ID = " + userID;

        SQLiteDatabase db = getSqLiteDatabase(false);
        arrayAdapter = new ArrayAdapter<String>(Ingredients.this, android.R.layout.simple_list_item_1);
        Cursor cursor = db.rawQuery(getUsersCurrentIngredientsFromDB, null);

        addIngredientsToArrayAdapterForIngredientsList(cursor);

        lst_CurrentIngredients.setAdapter(arrayAdapter);
    }

    private SQLiteDatabase getSqLiteDatabase(boolean writable) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(Ingredients.this);
        if (writable) {
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
        String measurement;
        if (cursor.moveToFirst()) {
            do {
                ingredientID = cursor.getInt(0);
                ingredientName = cursor.getString(1);
                ingredientQuantity = cursor.getInt(2);
                measurement = cursor.getString(3);

                RecipeIngredient ingredientForList = new RecipeIngredient(ingredientID, ingredientName, ingredientQuantity, measurement);
                ingredientsForDeleting.add(ingredientForList);
                arrayAdapter.add(ingredientForList.toString());

            } while (cursor.moveToNext());
        } else {

        }
    }

    private int getUserID() {
        int userID = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getInt("userID");
        }

        return userID;
    }

    private boolean nameIsEmpty() {
        //https://stackoverflow.com/questions/6290531/how-do-i-check-if-my-edittext-fields-are-empty
        return et_IngredientName.getText().toString().trim().length() == 0;
    }

    private boolean quantityIsEmpty() {
        //https://stackoverflow.com/questions/6290531/how-do-i-check-if-my-edittext-fields-are-empty
        return et_quantity.getText().toString().trim().length() == 0;
    }
}