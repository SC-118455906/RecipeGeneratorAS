package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CustomIngredient extends AppCompatActivity {

    protected static final String TAG = "CustomIngredient";

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


        //https://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list
        sp_FoodTypes.setAdapter(arrayAdapter);

        btn_ToHome.setOnClickListener((v -> {
            switchActivity(MainActivity.class);
        }));

        btn_AddCustomIngredient.setOnClickListener((v -> {
            String ingredientName = et_CustIngName.getText().toString().toUpperCase();
            String foodType = sp_FoodTypes.getSelectedItem().toString().toUpperCase();
            addCustomIngredientToDatabase(ingredientName, foodType);
        }));


    }

    private ArrayAdapter getAllFoodTypes() {
        String sql = "SELECT TYPE FROM FOODTYPE";
        SQLiteDatabase db = getSqLiteDatabase(false);

        Cursor cursor = db.rawQuery(sql, null);
        arrayAdapter = new ArrayAdapter<String>(CustomIngredient.this, android.R.layout.simple_list_item_1);

        addTypesToAdapter(cursor);
        db.close();

        return arrayAdapter;
    }

    private void addTypesToAdapter(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                arrayAdapter.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
    }

    private void addCustomIngredientToDatabase(String ingredientName, String foodType) {
        SQLiteDatabase db = getSqLiteDatabase(true);

        int ingredientType = getTypeNumericValue(foodType);

        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", ingredientName);
        contentValues.put("TYPE", ingredientType);

        long result = db.insert("INGREDIENTS", null, contentValues);

        if(result == -1){
            Log.println(Log.INFO, TAG, "didn't write users ingredient to db");
        } else {
            Log.println(Log.INFO, TAG, "Successfully wrote users ingredient to db");
        }

        db.close();
    }

    private int getTypeNumericValue(String foodType) {
        int type = 0;
        SQLiteDatabase db = getSqLiteDatabase(false);
        String sql = "SELECT TYPE_ID FROM FOODTYPE WHERE UPPER(TYPE) = '" + foodType + "'";

        Cursor cursor = db.rawQuery(sql, null);
        try {
            if(cursor.moveToFirst()) {
                type = cursor.getInt(0);
            } else {
                return 0;
            }
        } catch(SQLiteException e){
        }
        db.close();
        return type;
    }

    private void switchActivity(Class _class) {
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        startActivity(switchActivityIntent);
    }

    private SQLiteDatabase getSqLiteDatabase(boolean writable) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(CustomIngredient.this);
        if(writable){
            db = dbHelper.getWritableDatabase();
        } else {
            db = dbHelper.getReadableDatabase();
        }
        return db;
    }
}