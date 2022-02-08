package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.example.recipegenerator.models.UserIngredientForList;

public class CustomIngredient extends AppCompatActivity {

    Button btn_ToHome, btn_AddCustomIngredient;
    EditText et_CustIngName;
    Spinner sp_FoodTypes;

    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ingredient);

        btn_ToHome = findViewById(R.id.btn_CustomIngToHome);
        btn_AddCustomIngredient = findViewById(R.id.btn_AddCustomIngredient);
        et_CustIngName = findViewById(R.id.et_CustIngredientName);
        sp_FoodTypes = findViewById(R.id.sp_FoodTypes);

        arrayAdapter = getAllFoodTypes();

        sp_FoodTypes.setAdapter(arrayAdapter);

        btn_ToHome.setOnClickListener((v -> {
            switchActivity(MainActivity.class);
        }));

        btn_AddCustomIngredient.setOnClickListener((v -> {
            String ingredientName = et_CustIngName.getText().toString().toUpperCase();
            addCustomIngredientToDatabase();
        }));


    }

    private ArrayAdapter getAllFoodTypes() {
        String sql = "SELECT TYPE FROM FOODTYPE";
        SQLiteDatabase db = getSqLiteDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        arrayAdapter = new ArrayAdapter<String>(CustomIngredient.this, android.R.layout.simple_list_item_1);

        addTypesToAdapter(cursor);

        return arrayAdapter;
    }

    private void addTypesToAdapter(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                arrayAdapter.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
    }

    private void addCustomIngredientToDatabase() {
    }

    private void switchActivity(Class _class) {
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        startActivity(switchActivityIntent);
    }

    private SQLiteDatabase getSqLiteDatabase() {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(CustomIngredient.this);
        db = dbHelper.getReadableDatabase();
        return db;
    }
}