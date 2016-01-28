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
import com.android.settings.Utils;

public class ExpandedBarsVisibilitySettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_QUICK_ACCESS_BAR =
            "expanded_bars_show_quick_access_bar";
    private static final String PREF_SHOW_BRIGHTNESS_SLIDER_BAR =
            "expanded_bars_show_brightness_slider_bar";
    private static final String PREF_SHOW_WIFI_BAR =
            "expanded_bars_show_wifi_network_bar";
    private static final String PREF_SHOW_MOBILE_BAR =
            "expanded_bars_show_mobile_network_bar";
    private static final String PREF_SHOW_BATTERY_STATUS_BAR =
            "expanded_bars_show_battery_status_bar";
    private static final String PREF_SHOW_WEATHER_BAR =
            "expanded_bars_show_weather_bar";

    private SwitchPreference mShowQuickAccessBar;
    private SwitchPreference mShowBrightnessSliderBar;
    private SwitchPreference mShowWifiBar;
    private SwitchPreference mShowMobileBar;
    private SwitchPreference mShowBatteryStatusBar;
    private SwitchPreference mShowWeatherBar;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.expanded_bars_visibility_settings);
        mResolver = getContentResolver();

        final boolean isLockClockInstalled =
                Utils.isPackageInstalled(getActivity(), "com.cyanogenmod.lockclock");
        final boolean supportsMobileData = DeviceUtils.deviceSupportsMobileData(getActivity());

        mShowQuickAccessBar =
                (SwitchPreference) findPreference(PREF_SHOW_QUICK_ACCESS_BAR);
        mShowQuickAccessBar.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_QAB, 1) == 1);
        mShowQuickAccessBar.setOnPreferenceChangeListener(this);

        mShowBrightnessSliderBar =
                (SwitchPreference) findPreference(PREF_SHOW_BRIGHTNESS_SLIDER_BAR);
        mShowBrightnessSliderBar.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_BRIGHTNESS_SLIDER, 1) == 1);
        mShowBrightnessSliderBar.setOnPreferenceChangeListener(this);

        mShowWifiBar =
                (SwitchPreference) findPreference(PREF_SHOW_WIFI_BAR);
        mShowWifiBar.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_WIFI_BAR, 0) == 1);
        mShowWifiBar.setOnPreferenceChangeListener(this);

        if (supportsMobileData) {
            mShowMobileBar =
                    (SwitchPreference) findPreference(PREF_SHOW_MOBILE_BAR);
            mShowMobileBar.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_SHOW_MOBILE_BAR, 0) == 1);
            mShowMobileBar.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_MOBILE_BAR);
        }

        mShowBatteryStatusBar =
                (SwitchPreference) findPreference(PREF_SHOW_BATTERY_STATUS_BAR);
        mShowBatteryStatusBar.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_BATTERY_STATUS_BAR, 0) == 1);
        mShowBatteryStatusBar.setOnPreferenceChangeListener(this);

        mShowWeatherBar =
                (SwitchPreference) findPreference(PREF_SHOW_WEATHER_BAR);
        mShowWeatherBar.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_WEATHER, 0) == 1);
        mShowWeatherBar.setOnPreferenceChangeListener(this);
        if (!isLockClockInstalled) {
            mShowWeatherBar.setSummary(getResources().getString(R.string.lock_clock_missing_summary));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShowQuickAccessBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_QAB,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowBrightnessSliderBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_BRIGHTNESS_SLIDER,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowWifiBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_WIFI_BAR,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowMobileBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_MOBILE_BAR,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowBatteryStatusBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_BATTERY_STATUS_BAR,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowWeatherBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_WEATHER,
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
