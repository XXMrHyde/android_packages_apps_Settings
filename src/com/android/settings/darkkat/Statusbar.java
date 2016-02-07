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

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Statusbar extends SettingsPreferenceFragment {

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
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR;
    }
}
