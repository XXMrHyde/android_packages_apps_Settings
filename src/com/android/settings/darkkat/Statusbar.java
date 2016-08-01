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

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Statusbar extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        if (!DeviceUtils.deviceSupportsMobileData(getActivity())) {
            findPreference("status_bar_network_names_settings").setTitle(
                    R.string.status_bar_network_names_settings_wifi_only_title);
            findPreference("status_bar_network_names_settings").setSummary(
                    R.string.status_bar_network_names_settings_wifi_only_summary);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR;
    }
}
