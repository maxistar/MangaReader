package com.maxistar.mangabrowser;


import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	/** Called when the activity is first created. */
	//Preference mCountOfFilesToRemember;
	
	Preference mVersion;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.settings);
		//this.setP
		addPreferencesFromResource(R.xml.preferences);
		

		mVersion = this.findPreference("version_name");
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			mVersion.setSummary(pInfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        // Setup the initial values        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        //set names for items
        List<BaseSearchAdapter> list = BaseSearchAdapter.getAvailableAdapters();
        for(BaseSearchAdapter adapter:list){
        	String key = adapter.getSettingsKey();
        	Preference m = this.findPreference(key);
        	m.setTitle(adapter.getName());
        	m.setSummary(adapter.getServerAddress()+" ("+adapter.getLanguage()+")");
        }
		//get default value for count of files
		//mCountOfFilesToRemember = this.findPreference("count_of_files_to_remember");

        //mCountOfFilesToRemember.setSummary("Current value is " + sharedPreferences.getString("count_of_files_to_remember", "")); 

        // Set up a listener whenever a key changes            
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes            
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);    
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        //if (key.equals("count_of_files_to_remember")) {
        //	mCountOfFilesToRemember.setSummary("Current value is " + sharedPreferences.getString(key, "")); 
        //}
    }	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
 
}