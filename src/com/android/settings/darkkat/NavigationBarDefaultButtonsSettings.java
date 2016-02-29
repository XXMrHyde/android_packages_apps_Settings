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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.InstrumentedFragment;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationBarDefaultButtonsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_ENABLE_BACK_LONG_CLICK =
            "navigation_bar_enable_back_button_long_click";
    private static final String PREF_ENABLE_HOME_LONG_CLICK =
            "navigation_bar_enable_home_button_long_click";
    private static final String PREF_ENABLE_RECENTS_LONG_CLICK =
            "navigation_bar_enable_recents_button_long_click";

    private SwitchPreference mEnableBackLongClick;
    private SwitchPreference mEnableHomeLongClick;
    private SwitchPreference mEnableRecentsLongClick;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation_bar_default_buttons_settings);

        mResolver = getContentResolver();

        mEnableBackLongClick = (SwitchPreference) findPreference(PREF_ENABLE_BACK_LONG_CLICK);
        mEnableBackLongClick.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_ENABLE_BACK_BUTTON_LONG_CLICK, 0) == 1);
        mEnableBackLongClick.setOnPreferenceChangeListener(this);

        mEnableHomeLongClick = (SwitchPreference) findPreference(PREF_ENABLE_HOME_LONG_CLICK);
        mEnableHomeLongClick.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_ENABLE_HOME_BUTTON_LONG_CLICK, 1) == 1);
        mEnableHomeLongClick.setOnPreferenceChangeListener(this);

        mEnableRecentsLongClick = (SwitchPreference) findPreference(PREF_ENABLE_RECENTS_LONG_CLICK);
        mEnableRecentsLongClick.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_ENABLE_RECENTS_BUTTON_LONG_CLICK, 0) == 1);
        mEnableRecentsLongClick.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mEnableBackLongClick) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_ENABLE_BACK_BUTTON_LONG_CLICK,
                    value ? 1 : 0);
            return true;
        } else if (preference == mEnableHomeLongClick) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_ENABLE_HOME_BUTTON_LONG_CLICK,
                    value ? 1 : 0);
            return true;
        } else if (preference == mEnableRecentsLongClick) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_ENABLE_RECENTS_BUTTON_LONG_CLICK,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.NAVIGATIONBAR;
    }
}
