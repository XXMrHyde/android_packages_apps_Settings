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

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarExpandedQsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_BRIGHTNESS_SLIDER =
            "qs_show_brightness_slider";

    private SwitchPreference mShowBrightnessSlider;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.status_bar_expanded_qs_settings);
        mResolver = getContentResolver();

        mShowBrightnessSlider =
                (SwitchPreference) findPreference(PREF_SHOW_BRIGHTNESS_SLIDER);
        mShowBrightnessSlider.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_QS_SHOW_BRIGHTNESS_SLIDER, 1) == 1);
        mShowBrightnessSlider.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowBrightnessSlider) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_QS_SHOW_BRIGHTNESS_SLIDER,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
