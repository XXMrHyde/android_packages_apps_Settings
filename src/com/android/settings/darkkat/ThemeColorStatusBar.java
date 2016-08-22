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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.StatusBarColorHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class ThemeColorStatusBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_TEXT_COLOR =
            "theme_color_status_bar_text_color";
    private static final String PREF_ICON_COLOR =
            "theme_color_status_bar_icon_color";
    private static final String PREF_TEXT_COLOR_DARK_MODE =
            "theme_color_status_bar_text_color_dark_mode";
    private static final String PREF_ICON_COLOR_DARK_MODE =
            "theme_color_status_bar_icon_color_dark_mode";
    private static final String PREF_BATTERY_TEXT_COLOR =
            "theme_color_status_bar_battery_text_color";
    private static final String PREF_BATTERY_TEXT_COLOR_DARK_MODE =
            "theme_color_status_bar_battery_text_color_dark_mode";

    private static final int LIGHT_MODE_COLOR_SINGLE_TONE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT              = 0xff33b5e5;
    private static final int DARK_MODE_COLOR_SINGLE_TONE  = 0x99000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mTextColorDarkMode;
    private ColorPickerPreference mIconColorDarkMode;
    private ColorPickerPreference mBatteryTextColor;
    private ColorPickerPreference mBatteryTextColorDarkMode;

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

        addPreferencesFromResource(R.xml.theme_color_status_bar);
        mResolver = getContentResolver();

        int intColor;
        String hexColor;

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = StatusBarColorHelper.getTextColor(getActivity());
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setResetColors(LIGHT_MODE_COLOR_SINGLE_TONE, HOLO_BLUE_LIGHT);
        mTextColor.setOnPreferenceChangeListener(this);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        intColor = StatusBarColorHelper.getIconColor(getActivity());
        mIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setResetColors(LIGHT_MODE_COLOR_SINGLE_TONE, HOLO_BLUE_LIGHT);
        mIconColor.setOnPreferenceChangeListener(this);

        mTextColorDarkMode =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR_DARK_MODE);
        intColor = StatusBarColorHelper.getTextColorDarkMode(getActivity());
        mTextColorDarkMode.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColorDarkMode.setSummary(hexColor);
        mTextColorDarkMode.setResetColors(DARK_MODE_COLOR_SINGLE_TONE, DARK_MODE_COLOR_SINGLE_TONE);
        mTextColorDarkMode.setOnPreferenceChangeListener(this);

        mIconColorDarkMode =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR_DARK_MODE);
        intColor = StatusBarColorHelper.getIconColorDarkMode(getActivity());
        mIconColorDarkMode.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColorDarkMode.setSummary(hexColor);
        mIconColorDarkMode.setResetColors(DARK_MODE_COLOR_SINGLE_TONE, DARK_MODE_COLOR_SINGLE_TONE);
        mIconColorDarkMode.setOnPreferenceChangeListener(this);

        mBatteryTextColor =
                (ColorPickerPreference) findPreference(PREF_BATTERY_TEXT_COLOR);
        intColor = StatusBarColorHelper.getBatteryTextColor(getActivity());
        mBatteryTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryTextColor.setSummary(hexColor);
        mBatteryTextColor.setResetColors(LIGHT_MODE_COLOR_SINGLE_TONE, LIGHT_MODE_COLOR_SINGLE_TONE);
        mBatteryTextColor.setOnPreferenceChangeListener(this);

        mBatteryTextColorDarkMode =
                (ColorPickerPreference) findPreference(PREF_BATTERY_TEXT_COLOR_DARK_MODE);
        intColor = StatusBarColorHelper.getBatteryTextColorDarkMode(getActivity());
        mBatteryTextColorDarkMode.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryTextColorDarkMode.setSummary(hexColor);
        mBatteryTextColorDarkMode.setResetColors(DARK_MODE_COLOR_SINGLE_TONE,
                DARK_MODE_COLOR_SINGLE_TONE);
        mBatteryTextColorDarkMode.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_ICON_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mTextColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_TEXT_COLOR_DARK_MODE, intHex);
            refreshSettings();
            return true;
        } else if (preference == mIconColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_ICON_COLOR_DARK_MODE, intHex);
            refreshSettings();
            return true;
        } else if (preference == mBatteryTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mBatteryTextColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR_DARK_MODE, intHex);
            refreshSettings();
            return true;
        }
        return false;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        ThemeColorStatusBar getOwner() {
            return (ThemeColorStatusBar) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TEXT_COLOR,
                                    LIGHT_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_ICON_COLOR,
                                    LIGHT_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TEXT_COLOR_DARK_MODE,
                                    DARK_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_ICON_COLOR_DARK_MODE,
                                    DARK_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    LIGHT_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR_DARK_MODE,
                                    DARK_MODE_COLOR_SINGLE_TONE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TEXT_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_TEXT_COLOR_DARK_MODE,
                                    DARK_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_ICON_COLOR_DARK_MODE,
                                    DARK_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    LIGHT_MODE_COLOR_SINGLE_TONE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR_DARK_MODE,
                                    DARK_MODE_COLOR_SINGLE_TONE);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.THEME;
    }
}