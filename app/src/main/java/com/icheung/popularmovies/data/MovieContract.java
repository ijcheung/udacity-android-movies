package com.icheung.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY =
            "com.icheung.popularmovies";

    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://"
                    + CONTENT_AUTHORITY);

    public static final String PATH_ACRONYM =
            FavoriteMoviesEntry.TABLE_NAME;

    public static final class FavoriteMoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_ACRONYM).build();

        public static final String CONTENT_ITEMS_TYPE =
                "vnd.android.cursor.dir/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_ACRONYM;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_ACRONYM;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";

        /**
         * Return a Uri that points to the row containing a given id.
         *
         * @param id
         * @return Uri
         */
        public static Uri buildAcronymUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
