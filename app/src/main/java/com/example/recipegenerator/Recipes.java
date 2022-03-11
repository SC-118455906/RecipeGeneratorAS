package com.example.recipegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Recipes extends AppCompatActivity {

    protected static final String TAG = "Recipes";

    Button btn_FindRecipes;
    ListView lst_Recipes;
    CheckBox chk_Vegetarian, chk_Vegan;
    RadioGroup rg_AllOrSome;
    RadioButton radioButton, rb_Some, rb_All, rb_AllRecipes;
    boolean allIngredients, isVegetarian, isVegan, allRecipes;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        btn_FindRecipes = findViewById(R.id.btn_FindRecipes);
        lst_Recipes = findViewById(R.id.lstRecipes);
        rg_AllOrSome = findViewById(R.id.rg_AllOrSome);
        rb_Some = findViewById(R.id.rb_Some);
        rb_All = findViewById(R.id.rb_All);
        rb_AllRecipes = findViewById(R.id.rb_AllRecipes);
        chk_Vegetarian = findViewById(R.id.chkVegetarian);
        chk_Vegan = findViewById(R.id.chk_Vegan);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //setting the variables
        isVegetarian = getIsVegetarian();
        isVegan = getIsVegan();
        int userID = getUserID();
        allIngredients = false;
        allRecipes = false;

        //onclick listeners for the checkboxes, buttons, and listview
        chk_Vegetarian.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isVegetarian = true;
            } else {
                isVegetarian = false;
            }
        });

        chk_Vegan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isVegan = true;
            } else {
                isVegan = false;
            }
        });

        btn_FindRecipes.setOnClickListener((v) -> {
            findAllEligibleRecipes(userID, isVegetarian, isVegan);
        });

        lst_Recipes.setOnItemClickListener((parent, view, position, id) -> {
            String recipeName = lst_Recipes.getItemAtPosition(position).toString().toUpperCase();
            getRecipeDetails(recipeName, userID);
        });


        bottomNavigationView.setSelectedItemId(R.id.FindRecipes);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.MainActivity:
                        switchActivity(MainActivity.class, 0, 0);
                        break;
                    case R.id.Ingredients:
                        switchActivity(Ingredients.class, 0, userID);
                        break;
                    case R.id.FindRecipes:
                        break;
                    case R.id.CustomRecipe:
                        switchActivity(CustomRecipe.class, 0,userID);
                        break;
                }
                return true;
            }
        });
    }

    //gets the recipe id of the currently selected recipe so it can be passed to the recipe viewer page
    private void getRecipeDetails(String recipeName, int userID) {
        String sql = "SELECT RECIPE_ID FROM RECIPE WHERE UPPER(NAME) = '" + recipeName + "'";
        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);
        int id = 0;

        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }

        if (id != 0) {
            switchActivity(RecipeViewer.class, id, userID);
        }

        db.close();
    }

    private void findAllEligibleRecipes(int userID, boolean isVegetarian, boolean isVegan) {
        String sql;
        if (allRecipes) {
            if(isVegetarian){
                if(isVegan){
                    sql = "SELECT NAME, DESCRIPTION FROM RECIPE WHERE ALLERGENS LIKE '%VEGETARIAN%' OR '%VEGAN%'";
                } else {
                    sql = "SELECT NAME, DESCRIPTION FROM RECIPE WHERE ALLERGENS LIKE '%VEGETARIAN%'";
                }
            } else if(isVegan){
                sql = "SELECT NAME, DESCRIPTION FROM RECIPE WHERE ALLERGENS LIKE '%VEGAN%'";
            } else {
                sql = "SELECT NAME, DESCRIPTION FROM RECIPE";
            }
        } else {
            if (allIngredients) {
                if (isVegetarian) {
                    if (isVegan) {
                        sql = "SELECT R.NAME, R.DESCRIPTION FROM RECIPE AS R WHERE R.RECIPE_ID IN (SELECT DISTINCT RI1.RECIPE_ID\n" +
                                "FROM RECIPE_INGREDIENTS AS RI1, USER_INGREDIENTS AS UI1\n" +
                                " WHERE NOT EXISTS\n" +
                                "        (SELECT *\n" +
                                "           FROM RECIPE_INGREDIENTS AS RI2\n" +
                                "          WHERE RI2.RECIPE_ID = RI1.RECIPE_ID\n" +
                                "            AND RI2.INGREDIENT_ID\n" +
                                "                NOT IN (SELECT UI2.INGREDIENT_ID\n" +
                                "                          FROM USER_INGREDIENTS AS UI2\n" +
                                "                         WHERE UI2.USER_ID = UI1.USER_ID)) AND UI1.USER_ID = " + userID + ") AND R.ALLERGENS LIKE '%VEGETARIAN%' OR '%VEGAN%'";
                    } else {
                        sql = "SELECT R.NAME, R.DESCRIPTION FROM RECIPE AS R WHERE R.RECIPE_ID IN (SELECT DISTINCT RI1.RECIPE_ID\n" +
                                "FROM RECIPE_INGREDIENTS AS RI1, USER_INGREDIENTS AS UI1\n" +
                                " WHERE NOT EXISTS\n" +
                                "        (SELECT *\n" +
                                "           FROM RECIPE_INGREDIENTS AS RI2\n" +
                                "          WHERE RI2.RECIPE_ID = RI1.RECIPE_ID\n" +
                                "            AND RI2.INGREDIENT_ID\n" +
                                "                NOT IN (SELECT UI2.INGREDIENT_ID\n" +
                                "                          FROM USER_INGREDIENTS AS UI2\n" +
                                "                         WHERE UI2.USER_ID = UI1.USER_ID)) AND UI1.USER_ID = " + userID + ") AND R.ALLERGENS LIKE '%VEGETARIAN%'";
                    }
                } else if (isVegan) {
                    sql = "SELECT R.NAME, R.DESCRIPTION FROM RECIPE AS R WHERE R.RECIPE_ID IN (SELECT DISTINCT RI1.RECIPE_ID\n" +
                            "FROM RECIPE_INGREDIENTS AS RI1, USER_INGREDIENTS AS UI1\n" +
                            " WHERE NOT EXISTS\n" +
                            "        (SELECT *\n" +
                            "           FROM RECIPE_INGREDIENTS AS RI2\n" +
                            "          WHERE RI2.RECIPE_ID = RI1.RECIPE_ID\n" +
                            "            AND RI2.INGREDIENT_ID\n" +
                            "                NOT IN (SELECT UI2.INGREDIENT_ID\n" +
                            "                          FROM USER_INGREDIENTS AS UI2\n" +
                            "                         WHERE UI2.USER_ID = UI1.USER_ID)) AND UI1.USER_ID = " + userID + ") AND R.ALLERGENS LIKE '%VEGAN%'";
                } else {
                    sql = "SELECT R.NAME, R.DESCRIPTION FROM RECIPE AS R WHERE R.RECIPE_ID IN (SELECT DISTINCT RI1.RECIPE_ID\n" +
                            "FROM RECIPE_INGREDIENTS AS RI1, USER_INGREDIENTS AS UI1\n" +
                            " WHERE NOT EXISTS\n" +
                            "        (SELECT *\n" +
                            "           FROM RECIPE_INGREDIENTS AS RI2\n" +
                            "          WHERE RI2.RECIPE_ID = RI1.RECIPE_ID\n" +
                            "            AND RI2.INGREDIENT_ID\n" +
                            "                NOT IN (SELECT UI2.INGREDIENT_ID\n" +
                            "                          FROM USER_INGREDIENTS AS UI2\n" +
                            "                         WHERE UI2.USER_ID = UI1.USER_ID)) AND UI1.USER_ID = " + userID + ")";
                }
            } else {
                if (isVegetarian) {
                    if (isVegan) {
                        sql = "SELECT DISTINCT(RECIPE.NAME),(RECIPE.DESCRIPTION) FROM RECIPE, USER_INGREDIENTS, RECIPE_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = RECIPE_INGREDIENTS.INGREDIENT_ID AND RECIPE.RECIPE_ID = RECIPE_INGREDIENTS.RECIPE_ID AND USER_INGREDIENTS.USER_ID = " + userID + " AND RECIPE.ALLERGENS LIKE '%VEGETARIAN%' OR '%VEGAN%'";
                    } else {
                        sql = "SELECT DISTINCT(RECIPE.NAME),(RECIPE.DESCRIPTION) FROM RECIPE, USER_INGREDIENTS, RECIPE_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = RECIPE_INGREDIENTS.INGREDIENT_ID AND RECIPE.RECIPE_ID = RECIPE_INGREDIENTS.RECIPE_ID AND USER_INGREDIENTS.USER_ID = " + userID + " AND RECIPE.ALLERGENS LIKE '%VEGETARIAN%'";
                    }
                } else if (isVegan) {
                    sql = "SELECT DISTINCT(RECIPE.NAME),(RECIPE.DESCRIPTION) FROM RECIPE, USER_INGREDIENTS, RECIPE_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = RECIPE_INGREDIENTS.INGREDIENT_ID AND RECIPE.RECIPE_ID = RECIPE_INGREDIENTS.RECIPE_ID AND USER_INGREDIENTS.USER_ID = " + userID + " AND RECIPE.ALLERGENS LIKE '%VEGAN%'";
                } else {
                    sql = "SELECT DISTINCT(RECIPE.NAME),(RECIPE.DESCRIPTION) FROM RECIPE, USER_INGREDIENTS, RECIPE_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = RECIPE_INGREDIENTS.INGREDIENT_ID AND RECIPE.RECIPE_ID = RECIPE_INGREDIENTS.RECIPE_ID AND USER_INGREDIENTS.USER_ID = " + userID;
                }
            }
        }
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
        if (cursor.moveToFirst()) {
            try {
                do {
                    String name = cursor.getString(0);
                    recipes.add(name);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                Log.println(Log.ERROR, TAG, e.toString());
            }
        } else {
            recipes.add("You do not have sufficient ingredients at the moment to make any recipes. Please update your ingredients list!");
        }
    }

    private SQLiteDatabase getSqLiteDatabase(boolean writable) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(Recipes.this);
        if (writable) {
            db = dbHelper.getWritableDatabase();
        } else {
            db = dbHelper.getReadableDatabase();
        }
        return db;
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

    //this method checks whether or not the current users profile has marked them as a vegetarian or not
    private boolean getIsVegetarian() {
        boolean isVegetarian = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isVegetarian = extras.getBoolean("veggie");
        }

        return isVegetarian;
    }

    //this method checks whether or not the current users profile has marked them as a vegan or not
    private boolean getIsVegan() {
        boolean isVegan = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isVegan = extras.getBoolean("vegan");
        }

        return isVegan;
    }

    private void switchActivity(Class _class, int id, int userID) {
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        switchActivityIntent.putExtra("recipeID", id);
        switchActivityIntent.putExtra("userID", userID);
        startActivity(switchActivityIntent);
    }

    //was able to follow this tutorial for getting my radio buttons set up correctly: https://www.youtube.com/watch?v=fwSJ1OkK304
    public void checkRadioButton(View view) {
        int radioId = rg_AllOrSome.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        if (radioButton == rb_Some) {
            allIngredients = false;
        } else {
            allIngredients = true;
        }
        if (radioButton == rb_AllRecipes) {
            allRecipes = true;
        } else{
            allRecipes = false;
        }
    }
}