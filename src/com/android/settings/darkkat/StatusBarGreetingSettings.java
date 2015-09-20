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
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.util.darkkat.GreetingTextHelper;

import com.android.settings.R;
import com.android.settings.fusion.SeekBarPreference;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarGreetingSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String PREF_SHOW_LABEL =
            "greeting_show_label";
    private static final String PREF_CUSTOM_LABEL =
            "greeting_custom_label";
    private static final String PREF_TIMEOUT =
            "greeting_timeout";
    private static final String PREF_COLOR =
            "greeting_color";

    private static final int HIDDEN = 2;

    private static final int WHITE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private ListPreference mShowLabel;
    private EditTextPreference mCustomLabel;
    private SeekBarPreference mTimeOut;
    private ColorPickerPreference mColor;

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

        mResolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.status_bar_greeting_settings);

        mShowLabel =
                (ListPreference) findPreference(PREF_SHOW_LABEL);
        int showLabel = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_SHOW_LABEL, 1);
        mShowLabel.setValue(String.valueOf(showLabel));
        mShowLabel.setOnPreferenceChangeListener(this);

        if (showLabel != HIDDEN) {
            mCustomLabel = (EditTextPreference) findPreference(PREF_CUSTOM_LABEL);
            mCustomLabel.getEditText().setHint(
                    GreetingTextHelper.getDefaultGreetingText(getActivity()));
            mCustomLabel.setDialogMessage(getString(R.string.weather_hide_panel_custom_summary,
                    GreetingTextHelper.getDefaultGreetingText(getActivity())));
            mCustomLabel.setOnPreferenceChangeListener(this);

            mTimeOut =
                    (SeekBarPreference) findPreference(PREF_TIMEOUT);
            int timeout = Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_GREETING_TIMEOUT, 400);
            mTimeOut.setValue(timeout / 1);
            mTimeOut.setOnPreferenceChangeListener(this);

            mColor =
                    (ColorPickerPreference) findPreference(PREF_COLOR);
            int intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_COLOR,
                    WHITE); 
            mColor.setNewPreviewColor(intColor);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColor.setSummary(hexColor);
            mColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_CUSTOM_LABEL);
            removePreference(PREF_TIMEOUT);
            removePreference(PREF_COLOR);
        }

        updateShowLabelSummary(showLabel);
        updateCustomLabelPreference();

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mShowLabel) {
            int showLabel = Integer.valueOf((String) newValue);
            int index = mShowLabel.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_SHOW_LABEL, showLabel);
            updateShowLabelSummary(index);
            refreshSettings();
            return true;
        } else if (preference == mCustomLabel) {
            String label = (String) newValue;
            Settings.System.putString(mResolver,
                    Settings.System.STATUS_BAR_GREETING_CUSTOM_LABEL, label);
            updateCustomLabelPreference();
        } else if (preference == mTimeOut) {
            int timeout = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_TIMEOUT, timeout * 1);
            return true;
        } else if (preference == mColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void updateShowLabelSummary(int index) {
        int resId;

        if (index == 0) {
            resId = R.string.greeting_show_label_always_summary;
        } else if (index == 1) {
            resId = R.string.greeting_show_label_once_summary;
        } else {
            resId = R.string.greeting_show_label_never_summary;
        }
        mShowLabel.setSummary(getResources().getString(resId));
    }

    private void updateCustomLabelPreference() {
        String customLabelText = Settings.System.getString(mResolver,
                Settings.System.STATUS_BAR_GREETING_CUSTOM_LABEL);
        if (customLabelText == null) {
            customLabelText = "";
        }
        mCustomLabel.setText(customLabelText);
        mCustomLabel.setSummary(customLabelText.isEmpty() 
                ? GreetingTextHelper.getDefaultGreetingText(getActivity()) : customLabelText);
    }
}
