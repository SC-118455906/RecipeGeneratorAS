package com.example.recipegenerator;

//I was able to find this tutorial :https://stackoverflow.com/questions/9109438/how-to-use-an-existing-database-with-an-android-application/9109728#9109728
//and take the code from this tutorial for accessing my database. I had tried many other methods including these tutorials: https://www.edureka.co/community/79202/how-to-use-an-existing-database-with-an-android-application & https://www.youtube.com/watch?v=9t8VVWebRFM&t=1053s
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.User;
import com.example.recipegenerator.models.UserIngredient;

public class TestAdapter {

    public static final String USERS = "USER";
    public static final String COLUMN_FIRST_NAME = "FIRST_NAME";
    public static final String COLUMN_SURNAME = "SURNAME";
    public static final String COLUMN_ALLERGENS = "ALLERGENS";
    public static final String COLUMN_ID = "ID";

    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public TestAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public TestAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public TestAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public List<UserIngredient> getTestData() {
        List<UserIngredient> returnUsers = new ArrayList<>();
        User currentUser = new User(6, "Dan", "Murphy", "Coeliac", null);
        try {
            String sql ="SELECT INGREDIENTS.NAME, USER_INGREDIENTS.QUANTITY FROM INGREDIENTS, USER_INGREDIENTS WHERE USER_INGREDIENTS.INGREDIENT_ID = INGREDIENTS.INGREDIENT_ID AND USER_INGREDIENTS.USER_ID = " + currentUser.getID();
            Cursor cursor = mDb.rawQuery(sql, null);
            if(cursor.moveToFirst()){
                do {
                    String IngredientName = cursor.getString(0);
                    int IngredientID = cursor.getInt(1);

//                    UserIngredient userIngredients = new UserIngredient(UserID, IngredientID, quantity);
//                    returnUsers.add(userIngredients);

                } while(cursor.moveToNext());
            } else {

            }
            cursor.close();
            return returnUsers;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }


    public Cursor getUserIngredientData() {
        try {
            String sql ="SELECT * FROM INGREDIENTS";
            Cursor cursor = mDb.rawQuery(sql, null);
            if (cursor != null) {

            }
            return cursor;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public Ingredient getIngredientByName(String name, int quantity){
        Ingredient ingredient = null;
        try {
            String getIngredientFromDBSQL = "SELECT INGREDIENTS.INGREDIENT_ID, INGREDIENTS.NAME, FOODTYPE.TYPE FROM INGREDIENTS, FOODTYPE WHERE INGREDIENTS.TYPE = FOODTYPE.TYPE_ID AND UPPER(INGREDIENTS.NAME) = '" + name + "'";

            Cursor cursor = mDb.rawQuery(getIngredientFromDBSQL, null);
            if (cursor != null) {
                if(cursor.moveToFirst()){
                    do {
                        int ingredientID = cursor.getInt(0);
                        String ingredientName = cursor.getString(1);
                        String ingredientType = cursor.getString(2);
                        ingredient = new Ingredient(ingredientID, ingredientName, ingredientType);
                    } while(cursor.moveToNext());
                } else {

                }
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }

        return ingredient;
    }

    public void writeUser_Ingredients(Ingredient ingredient, int userID, int quantity){
        mDb = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USER_ID", userID);
        contentValues.put("INGREDIENT_ID", ingredient.getIngredient_ID());
        contentValues.put("QUANTITY", quantity);

        long result = mDb.insert("USER_INGREDIENTS", null, contentValues);

        if(result == -1){
            Log.println(Log.INFO, TAG, "didn't write users ingredient to db");
        } else {
            Log.println(Log.INFO, TAG, "Successfully wrote users ingredient to db");
        }

//        String writeUserIngToDatabase = "INSERT INTO USER_INGREDIENTS(USER_ID, INGREDIENT_ID, QUANTITY) VALUES (" + userID + ", " + ingredient.getIngredient_ID() + ", " + quantity + ")";
//        executeDatabaseQueryToWriteToUserIngredients(writeUserIngToDatabase);
    }

    private void executeDatabaseQueryToWriteToUserIngredients(String writeUserIngToDatabase) {
        try {
            mDb.execSQL(writeUserIngToDatabase);
            Log.println(Log.INFO, TAG, "Successfully wrote users ingredient to db");
        } catch (SQLException e){
            Log.println(Log.ERROR, TAG, "Error writing to User_Ingredients ==> " + e);
        }
    }
}
