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

import android.os.Bundle;
import android.preference.Preference;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.WeatherHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class ThemeColorSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.theme_color_settings);

        final boolean isWeatherServiceAvailable =
                WeatherHelper.isWeatherServiceAvailable(getActivity());
        final int weatherServiceAvailability = WeatherHelper.getWeatherServiceAvailability(getActivity());

        Preference customizeDetailedWeather =
                findPreference("theme_color_customize_detailed_weather");

        if (weatherServiceAvailability == WeatherHelper.PACKAGE_DISABLED) {
            final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                    ? R.string.weather_service_disabled_summary
                    : R.string.weather_service_disabled_tablet_summary);
            customizeDetailedWeather.setSummary(summary);
        } else if (weatherServiceAvailability == WeatherHelper.PACKAGE_MISSING) {
            customizeDetailedWeather.setSummary(
                    getResources().getString(R.string.weather_service_missing_summary));
        }
        customizeDetailedWeather.setEnabled(isWeatherServiceAvailable);
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.THEME;
    }
}
