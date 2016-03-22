/*
 * Copyright (C) 2015 SlimRoms Project
 *
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

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Gravity;

import com.android.settings.fusion.SeekBarPreference;
import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class SlimRecentsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SCALE =
            "slim_recents_scale";
    private static final String PREF_EXPANDED_MODE =
            "slim_recents_expanded_mode";
    private static final String PREF_LEFTY_MODE =
            "slim_recents_lefty_mode";
    private static final String PREF_POPUP_THEME_MODE =
            "slim_recents_popup_theme_mode";
    private static final String PREF_ONLY_SHOW_RUNNING_TASKS =
            "slim_recents_only_show_running_tasks";
    private static final String PREF_SHOW_TOPMOST =
            "slim_recents_show_topmost";
    private static final String PREF_MAX_APPS =
            "slim_recents_max_apps";

    private ListPreference mScale;
    private ListPreference mPopupThemeMode;
    private SwitchPreference mLeftyMode;
    private ListPreference mExpandedMode;
    private SwitchPreference mOnlyShowRunningTasks;
    private SwitchPreference mShowTopmost;
    private SeekBarPreference mMaxApps;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.slim_recents_settings);
        mResolver = getActivity().getContentResolver();

        mScale =
                (ListPreference) findPreference(PREF_SCALE);
        int scale = Settings.System.getInt(mResolver,
               Settings.System.SLIM_RECENTS_SCALE_FACTOR, 100);
        mScale.setValue(String.valueOf(scale));
        mScale.setSummary(mScale.getEntry());
        mScale.setOnPreferenceChangeListener(this);

        mExpandedMode =
                (ListPreference) findPreference(PREF_EXPANDED_MODE);
        int expandedMode = Settings.System.getInt(mResolver,
               Settings.System.SLIM_RECENTS_EXPANDED_MODE, 0);
        mExpandedMode.setValue(String.valueOf(expandedMode));
        mExpandedMode.setSummary(mExpandedMode.getEntry());
        mExpandedMode.setOnPreferenceChangeListener(this);

        mLeftyMode =
                (SwitchPreference) findPreference(PREF_LEFTY_MODE);
        mLeftyMode.setChecked(Settings.System.getInt(mResolver,
                Settings.System.SLIM_RECENTS_GRAVITY, Gravity.RIGHT) == Gravity.LEFT);
        mLeftyMode.setOnPreferenceChangeListener(this);

        mPopupThemeMode =
                (ListPreference) findPreference(PREF_POPUP_THEME_MODE);
        int popupThemeMode = Settings.System.getInt(mResolver,
               Settings.System.SLIM_RECENTS_POPUP_THEME_MODE, 1);
        mPopupThemeMode.setValue(String.valueOf(popupThemeMode));
        mPopupThemeMode.setSummary(mPopupThemeMode.getEntry());
        mPopupThemeMode.setOnPreferenceChangeListener(this);

        mOnlyShowRunningTasks =
                (SwitchPreference) findPreference(PREF_ONLY_SHOW_RUNNING_TASKS);
        mOnlyShowRunningTasks.setChecked(Settings.System.getInt(mResolver,
                Settings.System.SLIM_RECENTS_ONLY_SHOW_RUNNING_TASKS, 0) == 1);
        mOnlyShowRunningTasks.setOnPreferenceChangeListener(this);

        mShowTopmost = (SwitchPreference) findPreference(PREF_SHOW_TOPMOST);
        mShowTopmost.setChecked(Settings.System.getInt(mResolver,
                Settings.System.SLIM_RECENTS_SHOW_TOPMOST, 0) == 1);
        mShowTopmost.setOnPreferenceChangeListener(this);

        mMaxApps = (SeekBarPreference) findPreference(PREF_MAX_APPS);
        int maxApps = Settings.System.getInt(mResolver,
               Settings.System.SLIM_RECENTS_MAX_APPS,
                ActivityManager.getMaxRecentTasksStatic());
        mMaxApps.setValue(maxApps / 1);
        mMaxApps.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;
        int intValue;
        int index;

        if (preference == mScale) {
            intValue = Integer.valueOf((String) newValue);
            index = mScale.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_SCALE_FACTOR, intValue);
            mScale.setSummary(mScale.getEntries()[index]);
            return true;
        } else if (preference == mExpandedMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mExpandedMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_EXPANDED_MODE, intValue);
            mExpandedMode.setSummary(mExpandedMode.getEntries()[index]);
            return true;
        } else if (preference == mLeftyMode) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_GRAVITY,
                    value ? Gravity.LEFT : Gravity.RIGHT);
            return true;
        } else if (preference == mPopupThemeMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mExpandedMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_POPUP_THEME_MODE, intValue);
            mPopupThemeMode.setSummary(mPopupThemeMode.getEntries()[index]);
            return true;
        } else if (preference == mOnlyShowRunningTasks) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_ONLY_SHOW_RUNNING_TASKS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowTopmost) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_SHOW_TOPMOST,
                    value ? 1 : 0);
            return true;
        } else if (preference == mMaxApps) {
            intValue =  (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SLIM_RECENTS_MAX_APPS, intValue * 1);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.ADVANCED;
    }
}
