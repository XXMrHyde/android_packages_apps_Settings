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
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockScreenVisualizerSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW =
            "visualizer_show";
    private static final String PREF_USE_CUSTOM_COLOR =
            "visualizer_use_custom_color";

    private SwitchPreference mShow;
    private SwitchPreference mUseCustomColor;

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

        addPreferencesFromResource(R.xml.lock_screen_visualizer_settings);
        mResolver = getContentResolver();

        final boolean show = Settings.System.getInt(mResolver,
               Settings.System.LOCK_SCREEN_VISUALIZER_SHOW, 0) == 1;

        mShow = (SwitchPreference) findPreference(PREF_SHOW);
        mShow.setChecked(show);
        mShow.setOnPreferenceChangeListener(this);

        if (show) {
            mUseCustomColor = (SwitchPreference) findPreference(PREF_USE_CUSTOM_COLOR);
            mUseCustomColor.setChecked(Settings.System.getInt(mResolver,
               Settings.System.LOCK_SCREEN_VISUALIZER_USE_CUSTOM_COLOR, 0) == 1);
            mUseCustomColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_USE_CUSTOM_COLOR);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShow) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_SHOW,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mUseCustomColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_VISUALIZER_USE_CUSTOM_COLOR,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.LOCK_SCREEN;
    }
}
