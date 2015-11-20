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

import android.os.Bundle;
import android.preference.PreferenceCategory;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class Advanced extends SettingsPreferenceFragment {
    private static final String PREF_CAT_LOCK_CLOCK =
            "advanced_cat_lock_clock";
    private static final String PREF_LOCK_CLOCK_MISSING =
            "lock_clock_missing";
    private static final String PREF_LOCK_CLOCK_CLOCK =
            "lock_clock_clock_section";
    private static final String PREF_LOCK_CLOCK_WEATHER =
            "lock_clock_weather_section";
    private static final String PREF_LOCK_CLOCK_CALENDAR =
            "lock_clock_calendar_section";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.advanced);

        PreferenceCategory catLockClock =
                (PreferenceCategory) findPreference(PREF_CAT_LOCK_CLOCK);
        // Remove the lock clock preferences if lock clock is not installed
        // and show an info preference instead
        if (!Utils.isPackageInstalled(getActivity(), "com.cyanogenmod.lockclock")) {
            catLockClock.removePreference(findPreference(PREF_LOCK_CLOCK_CLOCK));
            catLockClock.removePreference(findPreference(PREF_LOCK_CLOCK_WEATHER));
            catLockClock.removePreference(findPreference(PREF_LOCK_CLOCK_CALENDAR));
        } else {
            catLockClock.removePreference(findPreference(PREF_LOCK_CLOCK_MISSING));
        }
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.ADVANCED;
    }

}
