package com.example.recipegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.recipegenerator.models.User;

public class CreateProfile extends AppCompatActivity {

    public static final String USERS = "USER";

    Button btn_AddUser;
    EditText et_FirstName, et_Surname, et_EmailAddress, et_DOB, et_Password, et_ConfirmPassword, et_Allergens;
    Switch sw_Allergens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        btn_AddUser = findViewById(R.id.btn_AddUser);
        et_FirstName = findViewById(R.id.et_FirstName);
        et_Surname = findViewById(R.id.et_Surname);
        et_EmailAddress = findViewById(R.id.et_EmailAddress);
        et_DOB = findViewById(R.id.et_DOB);
        et_Password = findViewById(R.id.et_Password);
        et_ConfirmPassword = findViewById(R.id.et_ConfirmPassword);
        et_Allergens = findViewById(R.id.et_Allergens);
        et_Allergens.setVisibility(View.INVISIBLE);


        btn_AddUser.setOnClickListener((v) -> {
            if (!fNameEmpty() && !sNameEmpty() && !emailEmpty() && !dobEmpty() && !passEmpty() && !cPassEmpty()) {
                ContentValues contentValues = getUserInformation();
                addUserToDb(contentValues);
                clearText();
            } else {
                Toast.makeText(this, "One or more fields are blank. Please enter valid values", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearText() {
        et_FirstName.setText(null);
        et_FirstName.requestFocus();
        et_Surname.setText(null);
        et_EmailAddress.setText(null);
        et_DOB.setText(null);
        et_Password.setText(null);
        et_ConfirmPassword.setText(null);
        et_Allergens.setText(null);
    }

    private boolean fNameEmpty() {
        return et_FirstName.getText().toString().trim().length() == 0;
    }

    private boolean sNameEmpty() {
        return et_Surname.getText().toString().trim().length() == 0;
    }

    private boolean emailEmpty() {
        return et_EmailAddress.getText().toString().trim().length() == 0;
    }

    private boolean dobEmpty() {
        return et_DOB.getText().toString().trim().length() == 0;
    }

    private boolean passEmpty() {
        return et_Password.getText().toString().trim().length() == 0;
    }

    private boolean cPassEmpty() {
        return et_ConfirmPassword.getText().toString().trim().length() == 0;
    }

    private ContentValues getUserInformation() {
        String firstName = et_FirstName.getText().toString().toUpperCase();
        String surname = et_Surname.getText().toString().toUpperCase();
        String emailAddress = et_EmailAddress.getText().toString().toUpperCase();
        String allergens = et_Allergens.getText().toString().toUpperCase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FIRST_NAME", firstName);
        contentValues.put("SURNAME", surname);
        contentValues.put("ALLERGENS", allergens);
        contentValues.put("EMAIL", emailAddress);

        return contentValues;
    }

    private void addUserToDb(ContentValues contentValues) {
        SQLiteDatabase db = getSqLiteDatabase(true);
        writeToDB(contentValues, db, USERS);
        db.close();
    }

    private void writeToDB(ContentValues contentValues, SQLiteDatabase db, String tableName) {
        try {
            db.insert(tableName, null, contentValues);
        } catch (SQLiteException e) {
            Toast.makeText(this, "error writing customer to db -> " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private SQLiteDatabase getSqLiteDatabase(boolean writable) {
        SQLiteDatabase db;
        DataBaseHelper dbHelper = new DataBaseHelper(CreateProfile.this);
        if (writable) {
            db = dbHelper.getWritableDatabase();
        } else {
            db = dbHelper.getReadableDatabase();
        }
        return db;
    }

}