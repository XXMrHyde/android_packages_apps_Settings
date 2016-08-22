/*
 * Copyright (C) 2015 DarkKat
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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.util.darkkat.DetailedWeatherHelper;
import com.android.internal.util.darkkat.WeatherHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class DetailedWeatherSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_SHOW_LOCATION =
            "detailed_weather_show_location";
    private static final String PREF_CONDITION_ICON =
            "detailed_weather_condition_icon";

    private SwitchPreference mShowLocation;
    private ListPreference mConditionIcon;

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

        addPreferencesFromResource(R.xml.weather_detailed_weather_settings);
        mResolver = getContentResolver();

        mShowLocation = (SwitchPreference) findPreference(PREF_SHOW_LOCATION);
        mShowLocation.setChecked(Settings.System.getInt(mResolver,
                Settings.System.DETAILED_WEATHER_SHOW_LOCATION, 1) == 1);
        mShowLocation.setOnPreferenceChangeListener(this);

        mConditionIcon = (ListPreference) findPreference(PREF_CONDITION_ICON);
        final int conditionIcon = Settings.System.getInt(mResolver,
                Settings.System.DETAILED_WEATHER_CONDITION_ICON, 0);
        mConditionIcon.setValue(String.valueOf(conditionIcon));
        mConditionIcon.setSummary(mConditionIcon.getEntry());
        mConditionIcon.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowLocation) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_SHOW_LOCATION, value ? 1 : 0);
            return true;
        } else if (preference == mConditionIcon) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mConditionIcon.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CONDITION_ICON, intValue);
            mConditionIcon.setSummary(mConditionIcon.getEntries()[index]);
            refreshSettings();
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.WEATHER;
    }
}
