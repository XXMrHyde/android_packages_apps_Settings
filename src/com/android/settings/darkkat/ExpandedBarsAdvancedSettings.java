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

import android.os.Bundle;
import android.preference.Preference;
import android.provider.Settings;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class ExpandedBarsAdvancedSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.expanded_bars_advanced_settings);

        final boolean showQuickAccessBar = Settings.System.getInt(getContentResolver(),
               Settings.System.STATUS_BAR_EXPANDED_SHOW_QAB, 1) == 1;
        final boolean showWeatherBar = Settings.System.getInt(getContentResolver(),
               Settings.System.STATUS_BAR_EXPANDED_SHOW_WEATHER, 0) == 1;
        final boolean showBatteryStatusBar = Settings.System.getInt(getContentResolver(),
               Settings.System.STATUS_BAR_EXPANDED_SHOW_BATTERY_STATUS_BAR, 0) == 1;
        final boolean isLockClockInstalled =
                Utils.isPackageInstalled(getActivity(), "com.cyanogenmod.lockclock");

        Preference advancedQuickAccessBarSettings =
                findPreference("advanced_quick_access_bar_settings");
        Preference advancedBatteryStatusBarSettings =
                findPreference("advanced_battery_status_bar_settings");
        Preference advancedWeatherBarSettings =
                findPreference("advanced_weather_bar_settings");
        if (!showQuickAccessBar) {
            advancedQuickAccessBarSettings.setSummary(
                    getResources().getString(R.string.advanced_quick_access_bar_settings_disabled_summary));
        }
        advancedQuickAccessBarSettings.setEnabled(showQuickAccessBar);

        if (!showBatteryStatusBar) {
            advancedBatteryStatusBarSettings.setSummary(
                    getResources().getString(R.string.advanced_battery_status_bar_settings_disabled_summary));
        }
        advancedBatteryStatusBarSettings.setEnabled(showBatteryStatusBar);

        if (!showWeatherBar) {
            advancedWeatherBarSettings.setSummary(
                    getResources().getString(R.string.advanced_weather_bar_settings_disabled_summary));
        } else if (!isLockClockInstalled) {
            advancedWeatherBarSettings.setSummary(
                    getResources().getString(R.string.lock_clock_missing_summary));
        }
        advancedWeatherBarSettings.setEnabled(showWeatherBar && isLockClockInstalled);
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
