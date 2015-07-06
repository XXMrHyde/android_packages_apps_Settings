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
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarNotifStatusAreaSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_COLORS =
            "notif_status_area_cat_colors";
    private static final String PREF_ICONS_COLOR_MODE =
            "notif_status_area_icons_color_mode";
    private static final String PREF_SHOW_TICKER =
            "notif_status_area_show_ticker";
    private static final String PREF_SHOW_COUNT =
            "notif_status_area_show_count";
    private static final String PREF_ICON_COLOR =
            "notif_status_area_icon_color";
    private static final String PREF_NOTIF_TEXT_COLOR =
            "notif_status_area_notif_text_color";
    private static final String PREF_COUNT_ICON_COLOR =
            "notif_status_area_count_icon_color";
    private static final String PREF_COUNT_TEXT_COLOR =
            "notif_status_area_count_text_color";

    private static final int WHITE = 0xffffffff;
    private static final int DEEP_ORANGE_600 = 0xfff4511e;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mIconsColorMode;
    private SwitchPreference mShowTicker;
    private SwitchPreference mShowCount;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mNotifTextColor;
    private ColorPickerPreference mCountIconColor;
    private ColorPickerPreference mCountTextColor;

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

        addPreferencesFromResource(R.xml.status_bar_notif_status_area_settings);

        mResolver = getActivity().getContentResolver();
        int intColor = WHITE;
        String hexColor = String.format("#%08x", (0xffffffff & intColor));

        boolean colorizeIcons = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICONS_COLOR_MODE, 1) != 0;
        boolean showTicker = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_TICKER, 0) == 1;
        boolean showCount = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_NOTIF_COUNT, 0) == 1;

        mIconsColorMode =
                (ListPreference) findPreference(PREF_ICONS_COLOR_MODE);
        int iconsColorMode = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICONS_COLOR_MODE, 1);
        mIconsColorMode.setValue(String.valueOf(iconsColorMode));
        mIconsColorMode.setSummary(mIconsColorMode.getEntry());
        mIconsColorMode.setOnPreferenceChangeListener(this);

        mShowTicker =
                (SwitchPreference) findPreference(PREF_SHOW_TICKER);
        mShowTicker.setChecked(showTicker);
        mShowTicker.setOnPreferenceChangeListener(this);

        mShowCount =
                (SwitchPreference) findPreference(PREF_SHOW_COUNT);
        mShowCount.setChecked(showCount);
        mShowCount.setOnPreferenceChangeListener(this);

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        if (colorizeIcons) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICON_COLOR,
                    WHITE); 
            mIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mIconColor.setSummary(hexColor);
            mIconColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mIconColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(mIconColor);
        }
        mNotifTextColor =
                (ColorPickerPreference) findPreference(PREF_NOTIF_TEXT_COLOR);
        if (showTicker) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_TEXT_COLOR,
                    WHITE); 
            mNotifTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNotifTextColor.setSummary(hexColor);
            mNotifTextColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mNotifTextColor.setOnPreferenceChangeListener(this);
        } else {
            // Remove uneeded preferences if ticker is disabled
            catColors.removePreference(mNotifTextColor);
        }

        mCountIconColor =
                (ColorPickerPreference) findPreference(PREF_COUNT_ICON_COLOR);
        mCountTextColor =
                (ColorPickerPreference) findPreference(PREF_COUNT_TEXT_COLOR);
        if (showCount) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT_ICON_COLOR,
                    DEEP_ORANGE_600); 
            mCountIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCountIconColor.setSummary(hexColor);
            mCountIconColor.setDefaultColors(DEEP_ORANGE_600, HOLO_BLUE_LIGHT);
            mCountIconColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT_TEXT_COLOR,
                    WHITE); 
            mCountTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCountTextColor.setSummary(hexColor);
            mCountTextColor.setDefaultColors(WHITE, WHITE);
            mCountTextColor.setOnPreferenceChangeListener(this);
        } else {
            // Remove uneeded preferences if notification count is disabled
            catColors.removePreference(mCountIconColor);
            catColors.removePreference(mCountTextColor);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_menu_reset) // use the KitKat backup icon
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
        boolean value;
        int intHex;
        String hex;

        if (preference == mIconsColorMode) {
            int iconsColorMode = Integer.valueOf((String) newValue);
            int index = mIconsColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICONS_COLOR_MODE,
                    iconsColorMode);
            preference.setSummary(mIconsColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowTicker) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_TICKER, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowCount) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_NOTIF_COUNT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNotifTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCountIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCountTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT_TEXT_COLOR, intHex);
            preference.setSummary(hex);
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

        StatusBarNotifStatusAreaSettings getOwner() {
            return (StatusBarNotifStatusAreaSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICONS_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_SHOW_TICKER, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_SHOW_NOTIF_COUNT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT_ICON_COLOR,
                                    DEEP_ORANGE_600);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT_TEXT_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICONS_COLOR_MODE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_SHOW_TICKER, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_SHOW_NOTIF_COUNT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_SYSTEM_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_TEXT_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIF_COUNT_TEXT_COLOR,
                                    WHITE);
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
}
