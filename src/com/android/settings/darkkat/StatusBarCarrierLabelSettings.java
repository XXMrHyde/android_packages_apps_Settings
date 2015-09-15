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
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarCarrierLabelSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String PREF_CARRIER_LABEL_USE_CUSTOM =
            "carrier_label_use_custom";
    private static final String PREF_CARRIER_LABEL_CUSTOM_LABEL =
            "carrier_label_custom_label";
    private static final String PREF_CARRIER_LABEL_SHOW =
            "carrier_label_show";
    private static final String PREF_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN =
            "carrier_label_show_on_lock_screen";
    private static final String PREF_HIDE_LABEL =
            "carrier_label_hide_label";
    private static final String PREF_NUMBER_OF_NOTIFICATION_ICONS =
            "carrier_label_number_of_notification_icons";
    private static final String PREF_COLOR =
            "carrier_label_color";

    private static final int WHITE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private SwitchPreference mUseCustom;
    private EditTextPreference mCustomLabel;
    private SwitchPreference mShow;
    private SwitchPreference mShowOnLockScreen;
    private SwitchPreference mHideLabel;
    private ListPreference mNumberOfNotificationIcons;
    private ColorPickerPreference mColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.status_bar_carrier_label_settings);

        mUseCustom = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_USE_CUSTOM);
        mUseCustom.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_USE_CUSTOM, 0) == 1);
        mUseCustom.setOnPreferenceChangeListener(this);

        mCustomLabel = (EditTextPreference) findPreference(PREF_CARRIER_LABEL_CUSTOM_LABEL);
        mCustomLabel.getEditText().setHint(getResources().getString(
                com.android.internal.R.string.default_custom_label));
        mCustomLabel.setOnPreferenceChangeListener(this);

        mShow = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_SHOW);
        mShow.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW, 0) == 1);
        mShow.setOnPreferenceChangeListener(this);

        mShowOnLockScreen = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN);
        mShowOnLockScreen.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN, 1) == 1);
        mShowOnLockScreen.setOnPreferenceChangeListener(this);

        mHideLabel =
                (SwitchPreference) findPreference(PREF_HIDE_LABEL);
        mHideLabel.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL, 1) == 1);
        mHideLabel.setOnPreferenceChangeListener(this);

        mNumberOfNotificationIcons =
                (ListPreference) findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS);
        int numberOfNotificationIcons = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS, 1);
        mNumberOfNotificationIcons.setValue(String.valueOf(numberOfNotificationIcons));
        mNumberOfNotificationIcons.setSummary(mNumberOfNotificationIcons.getEntry());
        mNumberOfNotificationIcons.setOnPreferenceChangeListener(this);

        mColor =
                (ColorPickerPreference) findPreference(PREF_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR,
                WHITE); 
        mColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mColor.setSummary(hexColor);
        mColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mColor.setOnPreferenceChangeListener(this);

        updateCustomLabelPreference();

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mUseCustom) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_USE_CUSTOM,
                    value ? 1 : 0);
            return true;
        } else if (preference == mCustomLabel) {
            String label = (String) newValue;
            Settings.System.putString(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_CUSTOM_LABEL, label);
            updateCustomLabelPreference();
        } else if (preference == mShow) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowOnLockScreen) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN,
                    value ? 1 : 0);
            return true;
        } else if (preference == mHideLabel) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL,
                    value ? 1 : 0);
            return true;
        } else if (preference == mNumberOfNotificationIcons) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mNumberOfNotificationIcons.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS,
                    intValue);
            preference.setSummary(mNumberOfNotificationIcons.getEntries()[index]);
            return true;
        } else if (preference == mColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void updateCustomLabelPreference() {
        String customLabelText = Settings.System.getString(mResolver,
                Settings.System.STATUS_BAR_CARRIER_LABEL_CUSTOM_LABEL);
        String customLabelDefaultSummary = getResources().getString(
                    com.android.internal.R.string.default_custom_label);
        if (customLabelText == null) {
            customLabelText = "";
        }
        mCustomLabel.setText(customLabelText);
        mCustomLabel.setSummary(
                customLabelText.isEmpty() ? customLabelDefaultSummary : customLabelText);
    }
}
