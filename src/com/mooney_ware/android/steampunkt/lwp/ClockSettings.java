/**
 *
 */
package com.mooney_ware.android.steampunkt.lwp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.mooney_ware.android.steampunkt.R;

/**
 * @author sean
 * 
 */
public class ClockSettings extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(
                ClockLWP.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.clock_settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                this);
    }
    
    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
    }
}
