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

public class PowerMenuSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_ADVANCED_REBOOT =
            "power_menu_show_advanced_reboot";
    private static final String PREF_BACKGROUND_COLOR =
            "power_menu_background_color";
    private static final String PREF_ICON_NORMAL_COLOR =
            "power_menu_icon_normal_color";
    private static final String PREF_ICON_ENABLED_SELECTED_COLOR =
            "power_menu_icon_enabled_selected_color";
    private static final String PREF_RIPPLE_COLOR =
            "power_menu_ripple_color";
    private static final String PREF_TEXT_COLOR =
            "power_menu_text_color";

    private static final int WHITE =
            0xffffffff;
    private static final int DARKKAT_BLUE_GREY =
            0xff1b1f23;
    private static final int TRANSLUCENT_BLACK =
            0x8a000000;
    private static final int HOLO_BLUE_LIGHT =
            0xff33b5e5;
    private static final int MATERIAL_TEAL_500 =
            0xff009688;
    private static final int BLACK =
            0xff000000;


    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mShowAdvancedReboot;
    private ColorPickerPreference mBackgroundColor;
    private ColorPickerPreference mIconNormalColor;
    private ColorPickerPreference mIconEnabledSelectedColor;
    private ColorPickerPreference mRippleColor;
    private ColorPickerPreference mTextColor;

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

        addPreferencesFromResource(R.xml.power_menu_settings);

        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mShowAdvancedReboot =
                (SwitchPreference) findPreference(PREF_SHOW_ADVANCED_REBOOT);
        mShowAdvancedReboot.setChecked((Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_SHOW_ADVANCED_REBOOT, 0) == 1));
        mShowAdvancedReboot.setOnPreferenceChangeListener(this);

        mBackgroundColor =
                (ColorPickerPreference) findPreference(PREF_BACKGROUND_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_BACKGROUND_COLOR,
                DARKKAT_BLUE_GREY); 
        mBackgroundColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBackgroundColor.setSummary(hexColor);
        mBackgroundColor.setDefaultColors(DARKKAT_BLUE_GREY, DARKKAT_BLUE_GREY);
        mBackgroundColor.setOnPreferenceChangeListener(this);

        mIconNormalColor =
                (ColorPickerPreference) findPreference(PREF_ICON_NORMAL_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_ICON_NORMAL_COLOR,
                WHITE); 
        mIconNormalColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconNormalColor.setSummary(hexColor);
        mIconNormalColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mIconNormalColor.setOnPreferenceChangeListener(this);

        mIconEnabledSelectedColor =
                (ColorPickerPreference) findPreference(PREF_ICON_ENABLED_SELECTED_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_ICON_ENABLED_SELECTED_COLOR,
                MATERIAL_TEAL_500); 
        mIconEnabledSelectedColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconEnabledSelectedColor.setSummary(hexColor);
        mIconEnabledSelectedColor.setDefaultColors(MATERIAL_TEAL_500, MATERIAL_TEAL_500);
        mIconEnabledSelectedColor.setOnPreferenceChangeListener(this);

        mRippleColor =
                (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
        intColor = PowerMenuColorHelper.getRippleColor(getActivity());
        mRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRippleColor.setSummary(hexColor);
        mRippleColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mRippleColor.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_TEXT_COLOR,
                WHITE); 
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mTextColor.setOnPreferenceChangeListener(this);

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

        if (preference == mShowAdvancedReboot) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_SHOW_ADVANCED_REBOOT, value ? 1 : 0);
            return true;
        } else if (preference == mBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_BACKGROUND_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconNormalColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_ICON_NORMAL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconEnabledSelectedColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_ICON_ENABLED_SELECTED_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_RIPPLE_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_TEXT_COLOR, intHex);
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

        PowerMenuSettings getOwner() {
            return (PowerMenuSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_colors_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_BACKGROUND_COLOR,
                                    DARKKAT_BLUE_GREY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_NORMAL_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_ENABLED_SELECTED_COLOR,
                                    MATERIAL_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_RIPPLE_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_TEXT_COLOR, WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_BACKGROUND_COLOR,
                                    DARKKAT_BLUE_GREY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_NORMAL_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_ENABLED_SELECTED_COLOR,
                                    MATERIAL_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_RIPPLE_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_TEXT_COLOR, HOLO_BLUE_LIGHT);
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
        return InstrumentedFragment.BUTTONS;
    }
}
