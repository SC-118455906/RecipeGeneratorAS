package com.example.recipegenerator;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.constraintlayout.utils.widget.MockView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DatabaseTest {
    SQLiteDatabase db;
    DataBaseHelper dbHelper;
    Context context = Mockito.mock(MainActivity.class);

    @Before
    public void setUp() {
        dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

//    @After
//    public void closeDb(){
//        db.close();
//        dbHelper.close();
//    }

    @Test
    public void testPreConditions() {
        assertNotNull(dbHelper);
        assertNotNull(db);
    }
}
