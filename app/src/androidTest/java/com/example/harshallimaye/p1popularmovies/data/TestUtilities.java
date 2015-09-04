package com.example.harshallimaye.p1popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by harshallimaye on 8/31/15.
 */

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your MovieContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_MOVIE_ID = "99705";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default movie values for your database tests.
     */
    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Jurassic World");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 88.5518);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 7.1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_REL_DATE, "2015-06-12");

        return movieValues;
    }


    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the MovieContract.

    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        testValues.put(MovieContract.LocationEntry.COLUMN_CITY_NAME, "North Pole");
        testValues.put(MovieContract.LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(MovieContract.LocationEntry.COLUMN_COORD_LONG, -147.353);

        return testValues;
    }
    */
    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the MovieContract as well as the WeatherDbHelper.

    static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(MovieContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }
    */
    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
 */
}
