package com.icheung.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDatabaseHelper extends SQLiteOpenHelper {
    /**
     * If the database schema is changed, the database version must be
     * incremented.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Database name.
     */
    public static final String DATABASE_NAME = "movies.db";

    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE "
                        + MovieContract.FavoriteMoviesEntry.TABLE_NAME + " ("
                        + MovieContract.FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MovieContract.FavoriteMoviesEntry.COLUMN_ID + " INTEGER NOT NULL, "
                        + MovieContract.FavoriteMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                        + MovieContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                        + MovieContract.FavoriteMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                        + MovieContract.FavoriteMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                        + MovieContract.FavoriteMoviesEntry.COLUMN_RATING + " REAL NOT NULL "
                        + ");";

        // Create the table.
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
