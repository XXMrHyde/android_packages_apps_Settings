/*
 * Copyright (C) 2013 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.darkkat;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockScreenAmbientDisplaySettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_ENABLE_DOZE =
            "ambient_display_enable_doze";
    private static final String PREF_SHOW_BATTERY =
            "ambient_display_show_battery";

    private SwitchPreference mEnableDoze;
    private SwitchPreference mShowBattery;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.lock_screen_ambient_display_settings);

        mResolver = getContentResolver();

        final boolean isDozeEnabled = Settings.Secure.getInt(mResolver,
                Settings.Secure.DOZE_ENABLED, 1) == 1;

        mEnableDoze = (SwitchPreference) findPreference(PREF_ENABLE_DOZE);
        mEnableDoze.setChecked(isDozeEnabled);
        mEnableDoze.setOnPreferenceChangeListener(this);

        if (isDozeEnabled) {
            mShowBattery = (SwitchPreference) findPreference(PREF_SHOW_BATTERY);
            mShowBattery.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_SHOW_BATTERY, 1) == 1);
            mShowBattery.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_BATTERY);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        boolean value;

        if (preference == mEnableDoze) {
            value = (Boolean) objValue;
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.DOZE_ENABLED, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowBattery) {
            value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_SHOW_BATTERY, value ? 1 : 0);
            return true;
        }

        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.LOCK_SCREEN;
    }
}
