package com.icheung.popularmovies.util;

import android.content.Context;
import android.widget.Toast;

public class Utilities {
    public static void showError(Context context, int resId) {
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();
    }

    private Utilities() {}
}
