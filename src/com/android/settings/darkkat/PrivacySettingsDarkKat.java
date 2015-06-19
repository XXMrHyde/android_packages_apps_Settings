/*
 * Copyright (C) 2014 DarkKat
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

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceScreen;

import com.android.internal.telephony.util.BlacklistUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PrivacySettingsDarkKat extends SettingsPreferenceFragment {
    private static final String KEY_BLACKLIST = "blacklist";

    private PreferenceScreen mBlacklist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.privacy_settings_darkkat);

        // Add package manager to check if features are available
        PackageManager pm = getActivity().getPackageManager();
        // Determine options based on device telephony support
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            // No telephony, remove dependent options
            removePreference(KEY_BLACKLIST);
        } else {
            mBlacklist = (PreferenceScreen) findPreference(KEY_BLACKLIST);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateBlacklistSummary();
    }

    private void updateBlacklistSummary() {
        if (mBlacklist != null) {
            if (BlacklistUtils.isBlacklistEnabled(getActivity())) {
                mBlacklist.setSummary(R.string.blacklist_summary);
            } else {
                mBlacklist.setSummary(R.string.blacklist_summary_disabled);
            }
        }
    }
}
