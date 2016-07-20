/*
 * Copyright (C) 2008 The Android Open Source Project
 *
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

package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import com.android.internal.logging.MetricsLogger;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceInfoSettings extends SettingsPreferenceFragment implements Indexable {

    private static final String PREF_REGULATORY_INFO = "regulatory_info";
    private static final String PREF_ABOUT_HARDWARE  = "about_hardware";
    private static final String PREF_ABOUT_SOFTWARE  = "about_software";
    private static final String PREF_ABOUT_DARKKAT   = "about_darkkat";

    private static final String PROP_DARKKAT_BUILD_VERSION = "ro.dk.build.version";
    private static final String PROP_DARKKAT_BUILD_TYPE    = "ro.dk.build.type";

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DEVICEINFO;
    }

    @Override
    protected int getHelpResource() {
        return R.string.help_uri_about;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.device_info_settings);

        String summarySoftware = Build.VERSION.RELEASE + " (" + Build.ID + ")";
        String summaryDarkkat = getPropValue(PROP_DARKKAT_BUILD_VERSION)
                + " (" + getPropValue(PROP_DARKKAT_BUILD_TYPE) + ")";

        // Remove regulatory information if none present.
        final Intent intent = new Intent(Settings.ACTION_SHOW_REGULATORY_INFO);
        if (getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
            Preference pref = findPreference(PREF_REGULATORY_INFO);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }

        setStringSummary(PREF_ABOUT_HARDWARE, Build.MODEL);
        setStringSummary(PREF_ABOUT_SOFTWARE, summarySoftware);
        if (Utils.isWifiOnly(getActivity())) {
            findPreference(PREF_ABOUT_SOFTWARE).setTitle(
                    getResources().getString(R.string.about_software_wifi_only_title));
        }
        setStringSummary(PREF_ABOUT_DARKKAT, summaryDarkkat);

    }

    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }

    private String getPropValue(String property) {
        return SystemProperties.get(property);
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

        @Override
        public List<SearchIndexableResource> getXmlResourcesToIndex(
                Context context, boolean enabled) {
            final SearchIndexableResource sir = new SearchIndexableResource(context);
            sir.xmlResId = R.xml.device_info_settings;
            return Arrays.asList(sir);
        }

        @Override
        public List<String> getNonIndexableKeys(Context context) {
            final List<String> keys = new ArrayList<String>();
            return keys;
        }
    };
}

