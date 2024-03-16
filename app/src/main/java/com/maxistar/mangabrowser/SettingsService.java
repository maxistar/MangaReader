package com.maxistar.mangabrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsService {

    public static final String SETTING_PAGE = "currentpage";

    int getCurrentPage(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(SETTING_PAGE, 0);
    }

    void setCurrentPage(int value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SETTING_PAGE, value);
        editor.apply();
    }
}
