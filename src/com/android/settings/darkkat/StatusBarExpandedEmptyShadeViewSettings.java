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
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarExpandedEmptyShadeViewSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_CARRIER_NAME =
            "empty_shade_view_show_carrier_name";
    private static final String PREF_SHOW_WIFI_NAME =
            "empty_shade_view_show_wifi_name";
    private static final String PREF_TEXT_COLOR =
            "empty_shade_view_text_color";

    private static final int WHITE           = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private SwitchPreference mShowCarrierName;
    private SwitchPreference mShowWifiName;
    private ColorPickerPreference mTextColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_expanded_empty_shade_view_settings);
        mResolver = getContentResolver();

        if (DeviceUtils.deviceSupportsMobileData(getActivity())) {
            mShowCarrierName = (SwitchPreference) findPreference(PREF_SHOW_CARRIER_NAME);
            mShowCarrierName.setChecked(Settings.System.getInt(mResolver,
               Settings.System.EMPTY_SHADE_VIEW_SHOW_CARRIER_NAME, 0) == 1);
            mShowCarrierName.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_CARRIER_NAME);
        }

        mShowWifiName = (SwitchPreference) findPreference(PREF_SHOW_WIFI_NAME);
        mShowWifiName.setChecked(Settings.System.getInt(mResolver,
               Settings.System.EMPTY_SHADE_VIEW_SHOW_WIFI_NAME, 0) == 1);
        mShowWifiName.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                Settings.System.EMPTY_SHADE_VIEW_TEXT_COLOR, WHITE);
        mTextColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setResetColors(WHITE, HOLO_BLUE_LIGHT);
        mTextColor.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShowCarrierName) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EMPTY_SHADE_VIEW_SHOW_CARRIER_NAME,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowWifiName) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EMPTY_SHADE_VIEW_SHOW_WIFI_NAME,
                    value ? 1 : 0);
            return true;
        } else if (preference == mTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.EMPTY_SHADE_VIEW_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
