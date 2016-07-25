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
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class ThemeColorPowerMenuBootDialog extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_THEME = "power_menu_boot_dialog_theme";

    private ListPreference mTheme;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.theme_color_power_menu_boot_dialog);

        mResolver = getContentResolver();

        mTheme = (ListPreference) findPreference(PREF_THEME);
        final int theme = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_BOOT_DIALOG_THEME, 0);
        mTheme.setValue(String.valueOf(theme));
        mTheme.setSummary(mTheme.getEntry());
        mTheme.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mTheme) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mTheme.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_BOOT_DIALOG_THEME, intValue);
            mTheme.setSummary(mTheme.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.THEME;
    }
}
