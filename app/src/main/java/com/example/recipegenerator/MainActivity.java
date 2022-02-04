package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //references to controls in the layout
    Button btn_ViewAll, btn_Add, btn_GoToIngredients, btn_GoToCustomRecipe, btn_GoToCustomIng;
    EditText et_Fname, et_Sname, et_Allergens;
    View img_Profile;
    ListView lst_Users;
//    DatabaseHelper databaseHelper;
    ArrayAdapter arrayAdapter;
    public int CurrentUserID = 1;

    //test commit
    public int getCurrentUserID() {
        return CurrentUserID;
    }

    public void setCurrentUserID(int currentUserID) {
        CurrentUserID = currentUserID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TestAdapter mDbHelper = new TestAdapter(MainActivity.this);
//        mDbHelper.createDatabase();
//        mDbHelper.open();
//        Cursor testData = mDbHelper.getIngredientData();
//
//        mDbHelper.close();


//        btn_Add = findViewById(R.id.btn_Add);
//        btn_ViewAll = findViewById(R.id.btn_ViewAll);
        btn_GoToIngredients = findViewById(R.id.btn_GoToIngredients);
        btn_GoToCustomRecipe = findViewById(R.id.btn_GoToCustomRecipe);
        btn_GoToCustomIng = findViewById(R.id.btn_GoToCustomIng);
        img_Profile = findViewById(R.id.img_Profile);
//        et_Fname = findViewById(R.id.et_Fname);
//        et_Sname = findViewById(R.id.et_Sname);
        et_Allergens = findViewById(R.id.et_Allergens);
        lst_Users = findViewById(R.id.lst_Users);
        BottomNavigationView bottomNavigationView = new BottomNavigationView(MainActivity.this);


//        bottomNavigationView.setOnItemSelectedListener(OnItemSelectedListener listener);{ item ->
//                when(item.itemId) {
//            R.id.item1 -> {
//                // Respond to navigation item 1 click
//                true
//            }
//            R.id.item2 -> {
//                // Respond to navigation item 2 click
//                true
//            }
//        else -> false
//        }
//        }

//        showTablesInDatabase();
//        showAllUsersInListView();
//        showIngredientsInListView();
        //listeners
//        btn_Add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                User user = null;
//                try {
//                    user = new User(-1, et_Fname.getText().toString(), et_Sname.getText().toString(), et_Allergens.getText().toString());
//                    Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Toast.makeText(MainActivity.this, "Error Creating customer", Toast.LENGTH_SHORT).show();
//                }
//
//                boolean success = databaseHelper.addOne(user);
//
////                showAllUsersInListView();
//                showIngredientsInListView();
//            }
//        });


//        btn_ViewAll.setOnClickListener((v) -> {
//
//            showAllUsersInListView();
//        });

        btn_GoToIngredients.setOnClickListener((v) -> {
            switchActivity(Ingredients.class);
        });

        btn_GoToCustomRecipe.setOnClickListener((v) -> {
            switchActivity(CustomRecipe.class);
        });

        btn_GoToCustomIng.setOnClickListener((v)->{
            switchActivity(CustomIngredient.class);
        });

        img_Profile.setOnClickListener((v -> {
            switchActivity(CreateProfile.class);
        }));
    }
//
//    private void showTablesInDatabase() {
//        boolean success = databaseHelper.checkTables();
//    }
//
//    private void showIngredientsInListView() {
//        arrayAdapter = new ArrayAdapter<Ingredient>(MainActivity.this, android.R.layout.simple_list_item_1, databaseHelper.getIngredients());
//        lst_Users.setAdapter(arrayAdapter);
//    }
//
//    private void showAllUsersInListView() {
//        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
//        arrayAdapter = new ArrayAdapter<User>(MainActivity.this,
//                android.R.layout.simple_list_item_1,
//                dbHelper.getAllUsers());
//        lst_Users.setAdapter(arrayAdapter);
//    }

    //code for swapping between activites came from this tutorial: https://learntodroid.com/how-to-switch-between-activities-in-android/
    private void switchActivity(Class _class) {
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
//        Intent switchActivityIntent = new Intent(this, Ingredients.class);
        Intent switchActivityIntent = new Intent(this, _class);
        startActivity(switchActivityIntent);
    }
}