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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockScreenButtonSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_BUTTON_BAR_LAUNCH_TYPE =
            "button_bar_launch_type";
    private static final String PREF_BUTTON_BAR_SHOW_BACKGROUND =
            "button_bar_show_background";
    private static final String PREF_BUTTON_BAR_COLORIZE_RIPPLE =
            "button_bar_colorize_ripple_effect";
    private static final String PREF_HIDE_BUTTON_BAR =
            "button_bar_hide_bar";
    private static final String PREF_BOTTOM_LEFT =
            "bottom_buttons_left";
    private static final String PREF_BOTTOM_RIGHT =
            "bottom_buttons_right";

    private ListPreference mButtonBarLaunchType;
    private SwitchPreference mButtonBarShowBackground;
    private SwitchPreference mButtonBarColorizeRipple;
    private SwitchPreference mHideButtonBar;
    private ListPreference mBottomLeft;
    private SwitchPreference mBottomRight;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.lock_screen_button_settings);

        mResolver = getActivity().getContentResolver();

        mButtonBarLaunchType =
                (ListPreference) findPreference(PREF_BUTTON_BAR_LAUNCH_TYPE);
        int launchType = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTON_BAR_LAUNCH_TYPE, 2);
        mButtonBarLaunchType.setValue(String.valueOf(launchType));
        mButtonBarLaunchType.setSummary(mButtonBarLaunchType.getEntry());
        mButtonBarLaunchType.setOnPreferenceChangeListener(this);

        mButtonBarShowBackground =
                (SwitchPreference) findPreference(PREF_BUTTON_BAR_SHOW_BACKGROUND);
        mButtonBarShowBackground.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTON_BAR_SHOW_BACKGROUND, 1) == 1);
        mButtonBarShowBackground.setOnPreferenceChangeListener(this);

        mButtonBarColorizeRipple =
                (SwitchPreference) findPreference(PREF_BUTTON_BAR_COLORIZE_RIPPLE);
        mButtonBarColorizeRipple.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTON_BAR_COLORIZE_RIPPLE, 0) == 1);
        mButtonBarColorizeRipple.setOnPreferenceChangeListener(this);

        mHideButtonBar =
                (SwitchPreference) findPreference(PREF_HIDE_BUTTON_BAR);
        mHideButtonBar.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTON_BAR_HIDE_BAR, 1) == 1);
        mHideButtonBar.setOnPreferenceChangeListener(this);

        mBottomLeft =
                (ListPreference) findPreference(PREF_BOTTOM_LEFT);
        int bottomLeft = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BOTTOM_BUTTON_LEFT, 1);
        mBottomLeft.setValue(String.valueOf(bottomLeft));
        mBottomLeft.setSummary(mBottomLeft.getEntry());
        mBottomLeft.setOnPreferenceChangeListener(this);

        mBottomRight =
                (SwitchPreference) findPreference(PREF_BOTTOM_RIGHT);
        mBottomRight.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BOTTOM_BUTTON_RIGHT, 1) == 1);
        mBottomRight.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        int index;
        boolean value;

        if (preference == mButtonBarLaunchType) {
            intValue = Integer.valueOf((String) newValue);
            index = mButtonBarLaunchType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTON_BAR_LAUNCH_TYPE, intValue);
            preference.setSummary(mButtonBarLaunchType.getEntries()[index]);
            return true;
        } else if (preference == mButtonBarShowBackground) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTON_BAR_SHOW_BACKGROUND,
                    value ? 1 : 0);
            return true;
        } else if (preference == mButtonBarColorizeRipple) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTON_BAR_COLORIZE_RIPPLE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mHideButtonBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTON_BAR_HIDE_BAR,
                    value ? 1 : 0);
            return true;
        } else if (preference == mBottomLeft) {
            intValue = Integer.valueOf((String) newValue);
            index = mBottomLeft.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BOTTOM_BUTTON_LEFT, intValue);
            preference.setSummary(mBottomLeft.getEntries()[index]);
            return true;
        } else if (preference == mBottomRight) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BOTTOM_BUTTON_RIGHT,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.LOCK_SCREEN;
    }
}
