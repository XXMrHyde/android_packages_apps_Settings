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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.WeatherHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class Advanced extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String PREF_CAT_RECENTS =
            "advanced_cat_recents";
    private static final String PREF_USE_SLIM_RECENTS =
            "use_slim_recents";
    private static final String PREF_CAT_LOCK_CLOCK =
            "advanced_cat_lock_clock";
    private static final String PREF_LOCK_CLOCK_MISSING =
            "lock_clock_missing";

    private SwitchPreference mUseSlimRecents;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.advanced);

        mResolver = getContentResolver();

        boolean useSlimRecents = Settings.System.getInt(mResolver,
                    Settings.System.USE_SLIM_RECENTS, 0) == 1;
        mUseSlimRecents = (SwitchPreference) findPreference(PREF_USE_SLIM_RECENTS);
        mUseSlimRecents.setChecked(useSlimRecents);
        mUseSlimRecents.setOnPreferenceChangeListener(this);

        PreferenceCategory catRecents =
                (PreferenceCategory) findPreference(PREF_CAT_RECENTS);
        if (!useSlimRecents) {
            catRecents.removePreference(findPreference("slim_recents_settings"));
        }

        PreferenceCategory catLockClock =
                (PreferenceCategory) findPreference(PREF_CAT_LOCK_CLOCK);
        // Remove the lock clock preferences if lock clock is not installed or disabled
        // and show an info preference instead
        final int lockClockAvailability = WeatherHelper.getLockClockAvailability(getActivity());
        if (lockClockAvailability != WeatherHelper.LOCK_CLOCK_ENABLED) {
            catLockClock.removePreference(findPreference("lock_clock_clock_section"));
            catLockClock.removePreference(findPreference("lock_clock_weather_section"));
            catLockClock.removePreference(findPreference("lock_clock_calendar_section"));
            if (lockClockAvailability == WeatherHelper.LOCK_CLOCK_DISABLED) {
                Preference lockClockMissing = findPreference(PREF_LOCK_CLOCK_MISSING);
                final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                        ? R.string.lock_clock_disabled_summary
                        : R.string.lock_clock_disabled_tablet_summary);
                lockClockMissing.setTitle(getResources().getString(R.string.lock_clock_disabled_title));
                lockClockMissing.setSummary(summary);
            }
        } else {
            catLockClock.removePreference(findPreference(PREF_LOCK_CLOCK_MISSING));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUseSlimRecents) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.USE_SLIM_RECENTS,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.ADVANCED;
    }

}
