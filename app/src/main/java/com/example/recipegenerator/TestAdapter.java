package com.example.recipegenerator;

//I was able to find this tutorial :https://stackoverflow.com/questions/9109438/how-to-use-an-existing-database-with-an-android-application/9109728#9109728
//and take the code from this tutorial for accessing my database. I had tried many other methods including these tutorials: https://www.edureka.co/community/79202/how-to-use-an-existing-database-with-an-android-application &
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.recipegenerator.models.Ingredient;
import com.example.recipegenerator.models.User;

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

    public List<User> getTestData() {
        List<User> returnUsers = new ArrayList<>();
        try {
            String sql ="SELECT * FROM USER";
            Cursor cursor = mDb.rawQuery(sql, null);
            if(cursor.moveToFirst()){
                do {
                    int UserID = cursor.getInt(0);
                    String Fname = cursor.getString(1);
                    String Sname = cursor.getString(2);
                    String Allergens = cursor.getString(3);

                    User user = new User(UserID, Fname, Sname, Allergens);
                    returnUsers.add(user);

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


//    public Cursor getIngredientData() {
//        try {
//            String sql ="SELECT * FROM INGREDIENTS";
//            Cursor cursor = mDb.rawQuery(sql, null);
//            if (cursor != null) {
//
//            }
//            return mCur;
//        } catch (SQLException mSQLException) {
//            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
//            throw mSQLException;
//        }
//    }

    public Ingredient getIngredientByName(String name, int userID, int quantity){
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

            String writeUserIngToDatabase = "INSERT INTO USER_INGREDIENTS(USER_ID, INGREDIENT_ID, QUANTITY) VALUES (" + userID + ", " + ingredient.getIngredient_ID() + ", " + quantity + ")";
            mDb.execSQL(writeUserIngToDatabase);
            Log.println(Log.INFO, TAG, "Successfully wrote users ingredient to db");
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }

        return ingredient;
    }
}
