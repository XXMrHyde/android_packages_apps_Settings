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
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarWeatherSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_CAT_OPTIONS =
            "weather_cat_options";
    private static final String PREF_CAT_NOTIFICATION_ICONS =
            "weather_cat_notification_icons";
    private static final String PREF_CAT_COLORS =
            "weather_cat_colors";
    private static final String PREF_SHOW =
            "weather_show";
    private static final String PREF_TYPE =
            "weather_type";
    private static final String PREF_HIDE =
            "weather_hide";
    private static final String PREF_NUMBER_OF_NOTIFICATION_ICONS =
            "weather_number_of_notification_icons";
    private static final String PREF_TEXT_COLOR =
            "weather_text_color";
    private static final String PREF_TEXT_COLOR_DARK_MODE =
            "weather_text_color_dark_mode";
    private static final String PREF_ICON_COLOR =
            "weather_icon_color";
    private static final String PREF_ICON_COLOR_DARK_MODE =
            "weather_icon_color_dark_mode";

    private static final int TYPE_TEXT      = 0;
    private static final int TYPE_ICON      = 1;
    private static final int TYPE_TEXT_ICON = 2;

    private static final int WHITE           = 0xffffffff;
    private static final int BLACK           = 0xff000000;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mShow;
    private ListPreference mType;
    private SwitchPreference mHide;
    private ListPreference mNumberOfNotificationIcons;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mTextColorDarkMode;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mIconColorDarkMode;

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

        addPreferencesFromResource(R.xml.status_bar_weather_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        final boolean show = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_WEATHER_SHOW, 0) == 1;

        PreferenceCategory catOptions =
                (PreferenceCategory) findPreference(PREF_CAT_OPTIONS);
        PreferenceCategory catNotificationIcons =
                (PreferenceCategory) findPreference(PREF_CAT_NOTIFICATION_ICONS);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        mShow = (SwitchPreference) findPreference(PREF_SHOW);
        mShow.setChecked(show);
        mShow.setOnPreferenceChangeListener(this);

        if (show) {
            final int type = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_TYPE, TYPE_TEXT_ICON);
            final boolean showText  = type == TYPE_TEXT || type == TYPE_TEXT_ICON;
            final boolean showIcon = type == TYPE_ICON || type == TYPE_TEXT_ICON;
            final boolean hide = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_WEATHER_HIDE, 1) == 1;

            mType = (ListPreference) findPreference(PREF_TYPE);
            mType.setValue(String.valueOf(type));
            mType.setSummary(mType.getEntry());
            mType.setOnPreferenceChangeListener(this);

            mHide =
                    (SwitchPreference) findPreference(PREF_HIDE);
            mHide.setChecked(hide);
            mHide.setOnPreferenceChangeListener(this);
            if (hide) {
                mNumberOfNotificationIcons =
                        (ListPreference) findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS);
                int numberOfNotificationIcons = Settings.System.getInt(mResolver,
                       Settings.System.STATUS_BAR_WEATHER_NUMBER_OF_NOTIFICATION_ICONS, 1);
                mNumberOfNotificationIcons.setValue(String.valueOf(numberOfNotificationIcons));
                mNumberOfNotificationIcons.setSummary(mNumberOfNotificationIcons.getEntry());
                mNumberOfNotificationIcons.setOnPreferenceChangeListener(this);
            } else {
                catNotificationIcons.removePreference(findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS));
            }

            if (showText) {
                mTextColor =
                        (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_WEATHER_TEXT_COLOR, WHITE);
                mTextColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mTextColor.setSummary(hexColor);
                mTextColor.setResetColors(WHITE, HOLO_BLUE_LIGHT);
                mTextColor.setOnPreferenceChangeListener(this);

                if (show) {
                    mTextColorDarkMode =
                            (ColorPickerPreference) findPreference(PREF_TEXT_COLOR_DARK_MODE);
                    intColor = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_WEATHER_TEXT_COLOR_DARK_MODE,
                            BLACK);
                    mTextColorDarkMode.setNewPreviewColor(intColor);
                    hexColor = String.format("#%08x", (0xffffffff & intColor));
                    mTextColorDarkMode.setSummary(hexColor);
                    mTextColorDarkMode.setResetColors(BLACK, BLACK);
                    mTextColorDarkMode.setOnPreferenceChangeListener(this);
                } else {
                    catColors.removePreference(findPreference(PREF_TEXT_COLOR_DARK_MODE));
                }
            } else {
                catColors.removePreference(findPreference(PREF_TEXT_COLOR));
                catColors.removePreference(findPreference(PREF_TEXT_COLOR_DARK_MODE));
            }

            if (showIcon) {
                mIconColor =
                        (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_WEATHER_ICON_COLOR, WHITE);
                mIconColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mIconColor.setSummary(hexColor);
                mIconColor.setResetColors(WHITE, HOLO_BLUE_LIGHT);
                mIconColor.setOnPreferenceChangeListener(this);

                if (show) {
                    mIconColorDarkMode =
                            (ColorPickerPreference) findPreference(PREF_ICON_COLOR_DARK_MODE);
                    intColor = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_WEATHER_ICON_COLOR_DARK_MODE,
                            BLACK);
                    mIconColorDarkMode.setNewPreviewColor(intColor);
                    hexColor = String.format("#%08x", (0xffffffff & intColor));
                    mIconColorDarkMode.setSummary(hexColor);
                    mIconColorDarkMode.setResetColors(BLACK, BLACK);
                    mIconColorDarkMode.setOnPreferenceChangeListener(this);
                } else {
                    catColors.removePreference(findPreference(PREF_ICON_COLOR_DARK_MODE));
                }
            } else {
                catColors.removePreference(findPreference(PREF_ICON_COLOR));
                catColors.removePreference(findPreference(PREF_ICON_COLOR_DARK_MODE));
            }
        } else {
            catOptions.removePreference(findPreference(PREF_TYPE));
            catNotificationIcons.removePreference(findPreference(PREF_HIDE));
            catNotificationIcons.removePreference(findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS));
            removePreference(PREF_CAT_NOTIFICATION_ICONS);
            catColors.removePreference(findPreference(PREF_TEXT_COLOR));
            catColors.removePreference(findPreference(PREF_TEXT_COLOR_DARK_MODE));
            catColors.removePreference(findPreference(PREF_ICON_COLOR));
            catColors.removePreference(findPreference(PREF_ICON_COLOR_DARK_MODE));
            removePreference(PREF_CAT_OPTIONS);
            removePreference(PREF_CAT_COLORS);
        }

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
        boolean value;
        int intValue;
        int index;
        int intHex;
        String hex;

        if (preference == mShow) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_SHOW,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mType) {
            intValue = Integer.valueOf((String) newValue);
            index = mType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_TYPE, intValue);
            mType.setSummary(mType.getEntries()[index]);
            refreshSettings();
            return true;

        } else if (preference == mHide) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_HIDE,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mNumberOfNotificationIcons) {
            intValue = Integer.valueOf((String) newValue);
            index = mNumberOfNotificationIcons.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_NUMBER_OF_NOTIFICATION_ICONS,
                    intValue);
            preference.setSummary(mNumberOfNotificationIcons.getEntries()[index]);
            return true;

        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_TEXT_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference ==  mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference ==  mIconColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_WEATHER_ICON_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
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

        StatusBarWeatherSettings getOwner() {
            return (StatusBarWeatherSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_WEATHER_SHOW, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_TYPE, TYPE_TEXT_ICON);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_TEXT_COLOR_DARK_MODE,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_ICON_COLOR_DARK_MODE,
                                    BLACK);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_SHOW, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_TYPE, TYPE_TEXT_ICON);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS, 4);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_TEXT_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_TEXT_COLOR_DARK_MODE,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_WEATHER_ICON_COLOR_DARK_MODE,
                                    BLACK);
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
        return InstrumentedFragment.STATUSBAR;
    }
}
