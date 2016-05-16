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
import android.preference.PreferenceScreen;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.WeatherHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class Statusbar extends SettingsPreferenceFragment {
    private static final String PREF_WEATHER_SERVICE_MISSING =
            "weather_service_missing";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        final boolean hasMultiUserSwitch = getResources().getBoolean(
                R.bool.config_has_status_bar_multi_user_switch);

        if (!hasMultiUserSwitch) {
            Preference multiUserSwitch = findPreference("status_bar_multi_user_switch_settings");
            multiUserSwitch.setTitle(getResources().getString(
                    R.string.status_bar_user_icon_settings_title));
            multiUserSwitch.setSummary(getResources().getString(
                    R.string.status_bar_user_icon_settings_summary));
        }

        // remove greeting setting untill reimplemented
        removePreference("status_bar_greeting_settings");

        if (!DeviceUtils.deviceSupportsMobileData(getActivity())) {
            removePreference("status_bar_carrier_label_settings");
        }

        // Remove the weather settings preference if weather service is not installed or disabled
        // and show an info preference instead
        final int weatherServiceAvailability = WeatherHelper.getWeatherServiceAvailability(getActivity());
        if (weatherServiceAvailability != WeatherHelper.PACKAGE_ENABLED) {
            removePreference("status_bar_weather_settings");
            if (weatherServiceAvailability == WeatherHelper.PACKAGE_DISABLED) {
                Preference weatherServiceMissing = findPreference(PREF_WEATHER_SERVICE_MISSING);
                final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                        ? R.string.weather_service_disabled_summary
                        : R.string.weather_service_disabled_tablet_summary);
                weatherServiceMissing.setTitle(getResources().getString(R.string.weather_service_disabled_title));
                weatherServiceMissing.setSummary(summary);
            }
        } else {
            removePreference(PREF_WEATHER_SERVICE_MISSING);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR;
    }
}
