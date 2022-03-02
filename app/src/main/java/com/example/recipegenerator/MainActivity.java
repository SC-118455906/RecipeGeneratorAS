package com.example.recipegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.UserIngredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarMenuView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    //references to controls in the layout
    Button btn_ViewAll, btn_Add, btn_GoToIngredients, btn_GoToCustomRecipe, btn_GoToCustomIng, btn_GoToRecipes;
    EditText et_Fname, et_Sname, et_Allergens;
    View img_Profile;
    ListView lst_Users;
    ArrayAdapter arrayAdapter;
    public int currentUserID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestAdapter mDbHelper = new TestAdapter(MainActivity.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        mDbHelper.close();

        btn_GoToIngredients = findViewById(R.id.btn_GoToIngredients);
        btn_GoToCustomRecipe = findViewById(R.id.btn_GoToCustomRecipe);
        btn_GoToCustomIng = findViewById(R.id.btn_GoToCustomIng);
        btn_GoToRecipes = findViewById(R.id.btn_GoToRecipes);
        img_Profile = findViewById(R.id.img_Profile);
        et_Allergens = findViewById(R.id.et_Allergens);
        BottomNavigationView bottomNavigationView = new BottomNavigationView(MainActivity.this);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.MainActivity:
//                        switchActivity(MainActivity.class, currentUserID);
//                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Ingredients:
                        switchActivity(Ingredients.class, currentUserID);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.FindRecipes:
                        switchActivity(MainActivity.class, currentUserID);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        btn_GoToIngredients.setOnClickListener((v) -> {
            switchActivity(Ingredients.class, currentUserID);
        });

        btn_GoToCustomRecipe.setOnClickListener((v) -> {
            switchActivity(CustomRecipe.class, currentUserID);
        });

        btn_GoToCustomIng.setOnClickListener((v)->{
            switchActivity(CustomIngredient.class, currentUserID);
        });

        btn_GoToRecipes.setOnClickListener((v)->{
            switchActivity(Recipes.class, currentUserID);
        });

        img_Profile.setOnClickListener((v -> {
            switchActivity(CreateProfile.class, currentUserID);
        }));
    }

    //code for swapping between activites came from this tutorial: https://learntodroid.com/how-to-switch-between-activities-in-android/
    private void switchActivity(Class _class, int currentUserID) {
        Intent switchActivityIntent = new Intent(this, _class);
        switchActivityIntent.putExtra("userID", currentUserID);
        startActivity(switchActivityIntent);
    }
}