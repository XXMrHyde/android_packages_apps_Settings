/* 
 * Copyright (C) 2014 DarkKat
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarExpandedWeatherSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_CURRENT =
            "weather_show_current";
    private static final String PREF_ICON_TYPE =
            "weather_icon_type";

    private SwitchPreference mShowCurrent;
    private ListPreference mIconType;

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

        addPreferencesFromResource(R.xml.status_bar_expanded_weather_settings);
        mResolver = getActivity().getContentResolver();

        mShowCurrent =
                (SwitchPreference) findPreference(PREF_SHOW_CURRENT);
        mShowCurrent.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_CURRENT, 1) == 1);
        mShowCurrent.setOnPreferenceChangeListener(this);

        mIconType = (ListPreference) findPreference(PREF_ICON_TYPE);
        int iconType = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON_TYPE, 0);
        mIconType.setValue(String.valueOf(iconType));
        mIconType.setSummary(mIconType.getEntry());
        mIconType.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        int index;

        if (preference == mShowCurrent) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_CURRENT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mIconType) {
            intValue = Integer.valueOf((String) newValue);
            index = mIconType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON_TYPE,
                    intValue);
            mIconType.setSummary(mIconType.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
