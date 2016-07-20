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
import android.os.SystemProperties;

import com.android.internal.logging.MetricsLogger;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class DeviceInfoDarkkat extends SettingsPreferenceFragment {

    private static final String PREF_DARKKAT_BUILD_VERSION = "darkkat_build_version";
    private static final String PREF_DARKKAT_BUILD_TYPE    = "darkkat_build_type";
    private static final String PREF_DARKKAT_BUILD_DATE    = "darkkat_build_date";

    private static final String PROP_DARKKAT_BUILD_VERSION = "ro.dk.build.version";
    private static final String PROP_DARKKAT_BUILD_TYPE    = "ro.dk.build.type";
    private static final String PROP_DARKKAT_BUILD_DATE    = "ro.build.date";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.device_info_darkkat);

        setValueSummary(PREF_DARKKAT_BUILD_VERSION, PROP_DARKKAT_BUILD_VERSION);
        setValueSummary(PREF_DARKKAT_BUILD_TYPE, PROP_DARKKAT_BUILD_TYPE);
        setValueSummary(PREF_DARKKAT_BUILD_DATE, PROP_DARKKAT_BUILD_DATE);
    }

    private void setValueSummary(String preference, String property) {
        try {
            findPreference(preference).setSummary(
                    SystemProperties.get(property,
                            getResources().getString(R.string.device_info_default)));
        } catch (RuntimeException e) {
            // No recovery
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DEVICEINFO;
    }
}
