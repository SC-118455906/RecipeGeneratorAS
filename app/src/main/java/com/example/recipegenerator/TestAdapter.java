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
import android.widget.Toast;

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


    public Ingredient getIngredientByName(String name){
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
            Log.e(TAG, "error getting ingredient from database"+ mSQLException.toString());
            Toast.makeText(mContext, "Error finding ingredient", Toast.LENGTH_SHORT).show();
        }

        return ingredient;
    }

    public void writeUser_Ingredients(Ingredient ingredient, int userID, int quantity, String measurement){
        try {
            mDb = mDbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("USER_ID", userID);
            contentValues.put("INGREDIENT_ID", ingredient.getIngredient_ID());
            contentValues.put("QUANTITY", quantity);
            contentValues.put("MEASUREMENT", measurement);

            long result = mDb.insert("USER_INGREDIENTS", null, contentValues);

            if (result == -1) {
                Log.println(Log.INFO, TAG, "didn't write users ingredient to db");
            } else {
                Log.println(Log.INFO, TAG, "Successfully wrote users ingredient to db");
            }
        } catch(NullPointerException e){
            Log.e(TAG, "ingredient passed to method is null: "+ e.toString());
            Toast.makeText(mContext, "Could not find this ingredient in the database. Please enter it as a custom ingredient if you wish to add it to your list", Toast.LENGTH_SHORT).show();
        }
    }
}
