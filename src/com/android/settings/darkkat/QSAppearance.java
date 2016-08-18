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
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class QSAppearance extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_BRIGHTNESS_SLIDER =
            "qs_appearance_show_brightness_slider";
    private static final String PREF_SHOW_BLUETOOTH_DETAIL_ON_CLICK =
            "qs_appearance_show_bluetooth_detail_on_click";
    private static final String PREF_SHOW_DND_DETAIL_ON_CLICK =
            "qs_appearance_show_dnd_detail_on_click";
    private static final String PREF_SHOW_WIFI_DETAIL_ON_CLICK =
            "qs_appearance_show_wifi_detail_on_click";

    private SwitchPreference mShowBrightnessSlider;
    private SwitchPreference mShowBluetoothDetailOnClick;
    private SwitchPreference mShowDndDetailOnClick;
    private SwitchPreference mShowWifiDetailOnClick;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.qs_appearance);

        mResolver = getContentResolver();

        mShowBrightnessSlider = (SwitchPreference) findPreference(PREF_SHOW_BRIGHTNESS_SLIDER);
        mShowBrightnessSlider.setChecked(Settings.System.getInt(mResolver,
                Settings.System.QS_SHOW_BRIGHTNESS_SLIDER, 1) == 1);
        mShowBrightnessSlider.setOnPreferenceChangeListener(this);

        mShowBluetoothDetailOnClick = (SwitchPreference) findPreference(PREF_SHOW_BLUETOOTH_DETAIL_ON_CLICK);
        mShowBluetoothDetailOnClick.setChecked(Settings.System.getInt(mResolver,
                Settings.System.QS_SHOW_BLUETOOTH_DETAIL_ON_CLICK, 0) == 1);
        mShowBluetoothDetailOnClick.setOnPreferenceChangeListener(this);

        mShowDndDetailOnClick = (SwitchPreference) findPreference(PREF_SHOW_DND_DETAIL_ON_CLICK);
        mShowDndDetailOnClick.setChecked(Settings.System.getInt(mResolver,
                Settings.System.QS_SHOW_DND_DETAIL_ON_CLICK, 1) == 1);
        mShowDndDetailOnClick.setOnPreferenceChangeListener(this);

        mShowWifiDetailOnClick = (SwitchPreference) findPreference(PREF_SHOW_WIFI_DETAIL_ON_CLICK);
        mShowWifiDetailOnClick.setChecked(Settings.System.getInt(mResolver,
                Settings.System.QS_SHOW_WIFI_DETAIL_ON_CLICK, 0) == 1);
        mShowWifiDetailOnClick.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShowBrightnessSlider) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_SHOW_BRIGHTNESS_SLIDER, value ? 1 : 0);
            return true;
        } else if (preference == mShowBluetoothDetailOnClick) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_SHOW_BLUETOOTH_DETAIL_ON_CLICK, value ? 1 : 0);
            return true;
        } else if (preference == mShowDndDetailOnClick) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_SHOW_DND_DETAIL_ON_CLICK, value ? 1 : 0);
            return true;
        } else if (preference == mShowWifiDetailOnClick) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_SHOW_WIFI_DETAIL_ON_CLICK, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
