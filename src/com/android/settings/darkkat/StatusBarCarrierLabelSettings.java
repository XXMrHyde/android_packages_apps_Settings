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
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
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

public class StatusBarCarrierLabelSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_NOTIFICATION_ICONS =
            "carrier_label_cat_notification_icons";
    private static final String PREF_CAT_COLORS =
            "carrier_label_cat_colors";
    private static final String PREF_CARRIER_LABEL_SHOW =
            "carrier_label_show";
    private static final String PREF_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN =
            "carrier_label_show_on_lock_screen";
    private static final String PREF_CARRIER_LABEL_USE_CUSTOM =
            "carrier_label_use_custom";
    private static final String PREF_CARRIER_LABEL_CUSTOM_LABEL =
            "carrier_label_custom_label";
    private static final String PREF_HIDE_LABEL =
            "carrier_label_hide_label";
    private static final String PREF_NUMBER_OF_NOTIFICATION_ICONS =
            "carrier_label_number_of_notification_icons";
    private static final String PREF_COLOR =
            "carrier_label_color";
    private static final String PREF_COLOR_DARK_MODE =
            "carrier_label_color_dark_mode";

    private static final int WHITE = 0xffffffff;
    private static final int TRANSLUCENT_BLACK = 0x99000000;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final String EMPTY_STRING = "";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mShow;
    private SwitchPreference mShowOnLockScreen;
    private SwitchPreference mUseCustom;
    private EditTextPreference mCustomLabel;
    private SwitchPreference mHideLabel;
    private ListPreference mNumberOfNotificationIcons;
    private ColorPickerPreference mColor;
    private ColorPickerPreference mColorDarkMode;

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

        addPreferencesFromResource(R.xml.status_bar_carrier_label_settings);
        mResolver = getActivity().getContentResolver();

        final boolean show = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW, 0) == 1;
        final boolean showOnLockScreen = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN, 1) == 1;
        final boolean useCustom = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_USE_CUSTOM, 0) == 1;
        final boolean hideLabel = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL, 1) == 1;
        final boolean isHidden = !show && !showOnLockScreen;

        PreferenceCategory catNotificationIcons =
                (PreferenceCategory) findPreference(PREF_CAT_NOTIFICATION_ICONS);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        mShow = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_SHOW);
        mShow.setChecked(show);
        mShow.setOnPreferenceChangeListener(this);

        mShowOnLockScreen = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN);
        mShowOnLockScreen.setChecked(showOnLockScreen);
        mShowOnLockScreen.setOnPreferenceChangeListener(this);

        if (!isHidden) {
            int intColor;
            String hexColor;

            mUseCustom = (SwitchPreference) findPreference(PREF_CARRIER_LABEL_USE_CUSTOM);
            mUseCustom.setChecked(useCustom);
            mUseCustom.setOnPreferenceChangeListener(this);

            if (useCustom) {
                mCustomLabel = (EditTextPreference) findPreference(PREF_CARRIER_LABEL_CUSTOM_LABEL);
                mCustomLabel.getEditText().setHint(getResources().getString(
                        com.android.internal.R.string.default_custom_label));
                mCustomLabel.setOnPreferenceChangeListener(this);
                updateCustomLabelPreference();
            } else {
                removePreference(PREF_CARRIER_LABEL_CUSTOM_LABEL);
            }

            if (show) {
                mHideLabel =
                        (SwitchPreference) findPreference(PREF_HIDE_LABEL);
                mHideLabel.setChecked(hideLabel);
                mHideLabel.setOnPreferenceChangeListener(this);
                if (hideLabel) {
                    mNumberOfNotificationIcons =
                            (ListPreference) findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS);
                    int numberOfNotificationIcons = Settings.System.getInt(mResolver,
                           Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS, 1);
                    mNumberOfNotificationIcons.setValue(String.valueOf(numberOfNotificationIcons));
                    mNumberOfNotificationIcons.setSummary(mNumberOfNotificationIcons.getEntry());
                    mNumberOfNotificationIcons.setOnPreferenceChangeListener(this);
                } else {
                    catNotificationIcons.removePreference(findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS));
                }
            } else {
                catNotificationIcons.removePreference(findPreference(PREF_HIDE_LABEL));
                catNotificationIcons.removePreference(findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS));
                removePreference(PREF_CAT_NOTIFICATION_ICONS);
            }

            mColor =
                    (ColorPickerPreference) findPreference(PREF_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR,
                    WHITE); 
            mColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColor.setSummary(hexColor);
            mColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mColor.setOnPreferenceChangeListener(this);

            mColorDarkMode =
                    (ColorPickerPreference) findPreference(PREF_COLOR_DARK_MODE);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR_DARK_MODE,
                    TRANSLUCENT_BLACK); 
            mColorDarkMode.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColorDarkMode.setSummary(hexColor);
            mColorDarkMode.setDefaultColors(TRANSLUCENT_BLACK, TRANSLUCENT_BLACK);
            mColorDarkMode.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_CARRIER_LABEL_USE_CUSTOM);
            removePreference(PREF_CARRIER_LABEL_CUSTOM_LABEL);
            catNotificationIcons.removePreference(findPreference(PREF_HIDE_LABEL));
            catNotificationIcons.removePreference(findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS));
            catColors.removePreference(findPreference(PREF_COLOR));
            catColors.removePreference(findPreference(PREF_COLOR_DARK_MODE));
            removePreference(PREF_CAT_NOTIFICATION_ICONS);
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
        String hex;
        int intHex;

        if (preference == mShow) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowOnLockScreen) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mUseCustom) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_USE_CUSTOM,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mCustomLabel) {
            String label = (String) newValue;
            Settings.System.putString(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_CUSTOM_LABEL, label);
            updateCustomLabelPreference();
        } else if (preference == mHideLabel) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL,
                    value ? 1 : 0);
            refreshSettings();
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
            hex = ColorPickerPreference.convertToARGB(
                 Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                 Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                 Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                 Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR_DARK_MODE, intHex);
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
            customLabelText = EMPTY_STRING;
        }
        mCustomLabel.setText(customLabelText);
        mCustomLabel.setSummary(
                customLabelText.isEmpty() ? customLabelDefaultSummary : customLabelText);
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

        StatusBarCarrierLabelSettings getOwner() {
            return (StatusBarCarrierLabelSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_USE_CUSTOM, 0);
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_CUSTOM_LABEL, EMPTY_STRING);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR_DARK_MODE, TRANSLUCENT_BLACK);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_SHOW_ON_LOCK_SCREEN, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_USE_CUSTOM, 1);
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_CUSTOM_LABEL, EMPTY_STRING);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_HIDE_LABEL, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_NUMBER_OF_NOTIFICATION_ICONS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR, HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CARRIER_LABEL_COLOR_DARK_MODE,
                                    TRANSLUCENT_BLACK);
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
