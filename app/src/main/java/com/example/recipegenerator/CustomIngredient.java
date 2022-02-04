package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CustomIngredient extends AppCompatActivity {

    Button btn_ToHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ingredient);

        btn_ToHome = findViewById(R.id.btn_CustomIngToHome);

        btn_ToHome.setOnClickListener((v -> {
            switchActivity(MainActivity.class);
        }));



    }

    private void switchActivity(Class _class) {
        //code for swapping between Activites was taken from this tutorial https://learntodroid.com/how-to-switch-between-activities-in-android/
        Intent switchActivityIntent = new Intent(this, _class);
        startActivity(switchActivityIntent);
    }
}