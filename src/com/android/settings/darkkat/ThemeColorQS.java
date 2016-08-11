/*
 * Copyright (C) 2016 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.StatusBarExpandedColorHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class ThemeColorQS extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_BACKGROUND_COLOR = "qs_background_color";
    private static final String PREF_ACCENT_COLOR     = "qs_accent_color";
    private static final String PREF_ICON_COLOR       = "qs_icon_color";
    private static final String PREF_RIPPLE_COLOR     = "qs_ripple_color";
    private static final String PREF_TEXT_COLOR       = "qs_text_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mBackgroundColor;
    private ColorPickerPreference mAccentColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mRippleColor;
    private ColorPickerPreference mTextColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.theme_color_qs);
        mResolver = getContentResolver();

        int intColor;
        String hexColor;

        mBackgroundColor = (ColorPickerPreference) findPreference(PREF_BACKGROUND_COLOR);
        intColor = StatusBarExpandedColorHelper.getQSBackgroundColor(getActivity());
        mBackgroundColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBackgroundColor.setSummary(hexColor);
        mBackgroundColor.setResetColors(StatusBarExpandedColorHelper.SYSTEMUI_PRIMARY,
                StatusBarExpandedColorHelper.DARKKAT_BLUE_GREY);
        mBackgroundColor.setOnPreferenceChangeListener(this);

        mAccentColor = (ColorPickerPreference) findPreference(PREF_ACCENT_COLOR);
        intColor = StatusBarExpandedColorHelper.getQSAccentColor(getActivity());
        mAccentColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mAccentColor.setSummary(hexColor);
        mAccentColor.setResetColors(StatusBarExpandedColorHelper.DEEP_TEAL_200,
                StatusBarExpandedColorHelper.DEEP_TEAL_500);
        mAccentColor.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = StatusBarExpandedColorHelper.getQSTextColor(getActivity());
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setResetColors(StatusBarExpandedColorHelper.WHITE,
                StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
        mTextColor.setOnPreferenceChangeListener(this);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        intColor = StatusBarExpandedColorHelper.getQSIconColor(getActivity()); 
        mIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setResetColors(StatusBarExpandedColorHelper.WHITE,
                StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
        mIconColor.setOnPreferenceChangeListener(this);

        mRippleColor =
                (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
        intColor = StatusBarExpandedColorHelper.getQSRippleColor(getActivity()); 
        mRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRippleColor.setSummary(hexColor);
        mRippleColor.setResetColors(StatusBarExpandedColorHelper.WHITE,
                StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
        mRippleColor.setOnPreferenceChangeListener(this);

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

        if (preference == mBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_BACKGROUND_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mAccentColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_ACCENT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_ICON_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_RIPPLE_COLOR, intHex);
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

        ThemeColorQS getOwner() {
            return (ThemeColorQS) getTargetFragment();
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
                                    Settings.System.QS_BACKGROUND_COLOR,
                                    StatusBarExpandedColorHelper.SYSTEMUI_PRIMARY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_ACCENT_COLOR,
                                    StatusBarExpandedColorHelper.DEEP_TEAL_200);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_ICON_COLOR, StatusBarExpandedColorHelper.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_RIPPLE_COLOR, StatusBarExpandedColorHelper.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_TEXT_COLOR, StatusBarExpandedColorHelper.WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_BACKGROUND_COLOR,
                                    StatusBarExpandedColorHelper.DARKKAT_BLUE_GREY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_ACCENT_COLOR,
                                    StatusBarExpandedColorHelper.DEEP_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_ICON_COLOR,
                                            StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_RIPPLE_COLOR,
                                            StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_TEXT_COLOR,
                                            StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
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
