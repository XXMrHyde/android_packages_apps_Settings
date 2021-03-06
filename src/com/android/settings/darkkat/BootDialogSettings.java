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

import com.android.internal.util.darkkat.PowerMenuColorHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class BootDialogSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_COLORS =
            "boot_dialog_cat_colors";
    private static final String PREF_SHOW_PROGRESS_DIALOG =
            "boot_dialog_show_progress_dialog";
    private static final String PREF_APP_TEXT_COLOR_MODE =
            "boot_dialog_app_text_color_mode";
    private static final String PREF_BACKGROUND_COLOR =
            "boot_dialog_background_color";
    private static final String PREF_TEXT_COLOR =
            "boot_dialog_text_color";
    private static final String PREF_APP_TEXT_COLOR =
            "boot_dialog_app_text_color";

    private static final int WHITE =
            0xffffffff;
    private static final int DARKKAT_BLUE_GREY =
            0xff1b1f23;
    private static final int HOLO_BLUE_LIGHT =
            0xff33b5e5;
    private static final int MATERIAL_TEAL_500 =
            0xff009688;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mShowProgressDialog;
    private ListPreference mAppTextColorMode;
    private ColorPickerPreference mBackgroundColor;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mAppTextColor;


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

        addPreferencesFromResource(R.xml.boot_dialog_settings);

        mResolver = getContentResolver();

        final int appTextColorMode = Settings.System.getInt(mResolver,
                Settings.System.BOOT_DIALOG_APP_TEXT_COLOR_MODE, 0);
        final boolean useCuatomAppTextColor = appTextColorMode == 0;

        int intColor;
        String hexColor;

        mShowProgressDialog =
                (SwitchPreference) findPreference(PREF_SHOW_PROGRESS_DIALOG);
        mShowProgressDialog.setChecked((Settings.System.getInt(mResolver,
                Settings.System.BOOT_DIALOG_SHOW_PROGRESS_DIALOG, 1) == 1));
        mShowProgressDialog.setOnPreferenceChangeListener(this);

        mAppTextColorMode =
                (ListPreference) findPreference(PREF_APP_TEXT_COLOR_MODE);
        mAppTextColorMode.setValue(String.valueOf(appTextColorMode));
        mAppTextColorMode.setSummary(mAppTextColorMode.getEntry());
        mAppTextColorMode.setOnPreferenceChangeListener(this);

        mBackgroundColor =
                (ColorPickerPreference) findPreference(PREF_BACKGROUND_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.BOOT_DIALOG_BACKGROUND_COLOR,
                DARKKAT_BLUE_GREY); 
        mBackgroundColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBackgroundColor.setSummary(hexColor);
        mBackgroundColor.setResetColors(DARKKAT_BLUE_GREY, DARKKAT_BLUE_GREY);
        mBackgroundColor.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.BOOT_DIALOG_TEXT_COLOR, WHITE); 
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setResetColors(WHITE, HOLO_BLUE_LIGHT);
        mTextColor.setOnPreferenceChangeListener(this);

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        if (useCuatomAppTextColor) {
            mAppTextColor =
                    (ColorPickerPreference) findPreference(PREF_APP_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.BOOT_DIALOG_APP_TEXT_COLOR,
            MATERIAL_TEAL_500); 
            mAppTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mAppTextColor.setSummary(hexColor);
            mAppTextColor.setResetColors(MATERIAL_TEAL_500, MATERIAL_TEAL_500);
            mAppTextColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_APP_TEXT_COLOR));
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mShowProgressDialog) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.BOOT_DIALOG_SHOW_PROGRESS_DIALOG, value ? 1 : 0);
            return true;
        } else if (preference == mAppTextColorMode) {
            int appTextColorMode = Integer.valueOf((String) newValue);
            int index = mAppTextColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.BOOT_DIALOG_APP_TEXT_COLOR_MODE, appTextColorMode);
            preference.setSummary(mAppTextColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.BOOT_DIALOG_BACKGROUND_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.BOOT_DIALOG_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mAppTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.BOOT_DIALOG_APP_TEXT_COLOR, intHex);
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

        BootDialogSettings getOwner() {
            return (BootDialogSettings) getTargetFragment();
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
                                    Settings.System.BOOT_DIALOG_SHOW_PROGRESS_DIALOG, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_APP_TEXT_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_BACKGROUND_COLOR,
                                    DARKKAT_BLUE_GREY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_TEXT_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_APP_TEXT_COLOR,
                                    MATERIAL_TEAL_500);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_SHOW_PROGRESS_DIALOG, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_APP_TEXT_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_BACKGROUND_COLOR,
                                    DARKKAT_BLUE_GREY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_TEXT_COLOR, HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.BOOT_DIALOG_APP_TEXT_COLOR,
                                    MATERIAL_TEAL_500);
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
        return InstrumentedFragment.ADVANCED;
    }
}
