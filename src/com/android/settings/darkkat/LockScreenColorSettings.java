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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockScreenColorSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_COLORS =
            "lock_screen_colors_cat_colors";
    private static final String PREF_WEATHER_COLORIZE_ALL_ICONS =
            "colors_weather_colorize_all_icons";
    private static final String PREF_BUTTONS_BAR_ICON_COLOR_MODE =
            "colors_buttons_bar_icon_color_mode";
    private static final String PREF_BUTTONS_BAR_RIPPLE_COLOR_MODE =
            "colors_buttons_bar_ripple_color_mode";
    private static final String PREF_BUTTONS_DEFAULT_COLORIZE_CUSTOM_ICONS =
            "colors_buttons_default_colorize_custom_icons";
    private static final String PREF_TEXT_COLOR =
            "colors_text_color";
    private static final String PREF_ICON_COLOR =
            "colors_icon_color";
    private static final String PREF_BUTTONS_BAR_RIPPLE_COLOR =
            "colors_buttons_bar_ripple_color";

    private static final int WHITE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mWeatherColorizeAllIcons;
    private ListPreference mButtonsBarIconColorMode;
    private ListPreference mButtonsBarRippleColorMode;
    private SwitchPreference mButtonsDefaultColorizeCustomIcons;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mButtonsBarRippleColor;

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

        addPreferencesFromResource(R.xml.lock_screen_color_settings);
        mResolver = getActivity().getContentResolver();

        boolean colorizeButtonsBarRipple = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, 2) == 1;

        int intColor;
        String hexColor;
        int intValue;

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        mWeatherColorizeAllIcons =
                (SwitchPreference) findPreference(PREF_WEATHER_COLORIZE_ALL_ICONS);
        mWeatherColorizeAllIcons.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_WEATHER_COLORIZE_ALL_ICONS, 0) == 1);
        mWeatherColorizeAllIcons.setOnPreferenceChangeListener(this);

        mButtonsBarIconColorMode =
                (ListPreference) findPreference(PREF_BUTTONS_BAR_ICON_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTONS_BAR_ICON_COLOR_MODE, 0);
        mButtonsBarIconColorMode.setValue(String.valueOf(intValue));
        mButtonsBarIconColorMode.setSummary(mButtonsBarIconColorMode.getEntry());
        mButtonsBarIconColorMode.setOnPreferenceChangeListener(this);

        mButtonsBarRippleColorMode =
                (ListPreference) findPreference(PREF_BUTTONS_BAR_RIPPLE_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, 2);
        mButtonsBarRippleColorMode.setValue(String.valueOf(intValue));
        mButtonsBarRippleColorMode.setSummary(mButtonsBarRippleColorMode.getEntry());
        mButtonsBarRippleColorMode.setOnPreferenceChangeListener(this);

        mButtonsDefaultColorizeCustomIcons =
                (SwitchPreference) findPreference(PREF_BUTTONS_DEFAULT_COLORIZE_CUSTOM_ICONS);
        mButtonsDefaultColorizeCustomIcons.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_SHORTCUTS_COLORIZE_CUSTOM_ICONS, 0) == 1);
        mButtonsDefaultColorizeCustomIcons.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_TEXT_COLOR,
                WHITE); 
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mTextColor.setOnPreferenceChangeListener(this);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_ICON_COLOR,
                WHITE); 
        mIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mIconColor.setOnPreferenceChangeListener(this);

        if (colorizeButtonsBarRipple) {
            mButtonsBarRippleColor =
                    (ColorPickerPreference) findPreference(PREF_BUTTONS_BAR_RIPPLE_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR, WHITE); 
            mButtonsBarRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mButtonsBarRippleColor.setSummary(hexColor);
            mButtonsBarRippleColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mButtonsBarRippleColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_BUTTONS_BAR_RIPPLE_COLOR));
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
        String hex;
        int intHex;
        boolean value;
        int intValue;
        int index;

        if (preference == mWeatherColorizeAllIcons) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_WEATHER_COLORIZE_ALL_ICONS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mButtonsBarIconColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mButtonsBarIconColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                  Settings.System.LOCK_SCREEN_BUTTONS_BAR_ICON_COLOR_MODE, intValue);
            preference.setSummary(mButtonsBarIconColorMode.getEntries()[index]);
            return true;
        } else if (preference == mButtonsBarRippleColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mButtonsBarRippleColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, intValue);
            preference.setSummary(mButtonsBarRippleColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mButtonsDefaultColorizeCustomIcons) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHORTCUTS_COLORIZE_CUSTOM_ICONS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mButtonsBarRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR, intHex);
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

        LockScreenColorSettings getOwner() {
            return (LockScreenColorSettings) getTargetFragment();
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
                                    Settings.System.LOCK_SCREEN_WEATHER_COLORIZE_ALL_ICONS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_ICON_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_SHORTCUTS_COLORIZE_CUSTOM_ICONS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_WEATHER_COLORIZE_ALL_ICONS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_ICON_COLOR_MODE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_SHORTCUTS_COLORIZE_CUSTOM_ICONS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BUTTONS_BAR_RIPPLE_COLOR,
                                    HOLO_BLUE_LIGHT);
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
