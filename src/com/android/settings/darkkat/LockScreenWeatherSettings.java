/*
 * Copyright (C) 2016 DarkKat
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

import java.util.ArrayList;

public class LockScreenWeatherSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_WEATHER =
            "lock_screen_weather_show_weather";
    private static final String PREF_SHOW_WEATHER_LOCATION =
            "lock_screen_weather_show_weather_location";

    private SwitchPreference mShowWeather;
    private SwitchPreference mShowWeatherLocation;

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

        addPreferencesFromResource(R.xml.lock_screen_weather_settings);

        mResolver = getContentResolver();

        final boolean showWeather = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_SHOW_WEATHER, 0) == 1;
        final boolean showWeatherOnAmbientDisplay = Settings.System.getInt(mResolver,
                Settings.System.AMBIENT_DISPLAY_SHOW_WEATHER, 0) == 1;

        mShowWeather = (SwitchPreference) findPreference(PREF_SHOW_WEATHER);
        mShowWeather.setChecked(showWeather);
        mShowWeather.setOnPreferenceChangeListener(this);

        if (showWeather || showWeatherOnAmbientDisplay) {
            mShowWeatherLocation = (SwitchPreference) findPreference(PREF_SHOW_WEATHER_LOCATION);
            mShowWeatherLocation.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_WEATHER_LOCATION, 1) == 1);
            mShowWeatherLocation.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_WEATHER_LOCATION);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShowWeather) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_WEATHER, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowWeatherLocation) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHOW_WEATHER_LOCATION, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.LOCK_SCREEN;
    }
}
