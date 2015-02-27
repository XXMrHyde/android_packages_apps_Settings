/*
 * Copyright (C) 2015 DarkKat
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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.darkkat.qs.QSTiles;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarExpandedQsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_QS_QUICK_PULLDOWN =
            "qs_quick_pulldown";
    private static final String PREF_QS_ORDER =
            "qs_order";
    private static final String PREF_QS_MAIN_TILES =
            "qs_main_tiles";
    private static final String PREF_QS_LOCATION_ADVANCED =
            "qs_location_advanced";
    private static final String PREF_QS_SHOW_BRIGHTNESS_SLIDER =
            "qs_show_brightness_slider";
    private static final String PREF_QS_BACKGROUND_COLOR =
            "qs_background_color";
    private static final String PREF_QS_ICON_COLOR =
            "qs_icon_color";
    private static final String PREF_QS_TEXT_COLOR =
            "qs_text_color";

    private static final int DEFAULT_BACKGROUND_COLOR = 0xff263238;
    private static final int WHITE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mQSQuickPulldown;
    private Preference mQSTiles;
    private CheckBoxPreference mQSMainTiles;
    private CheckBoxPreference mQSLocationAdvanced;
    private CheckBoxPreference mQSShowBrightnessSlider;
    private ColorPickerPreference mQSBackgroundColor;
    private ColorPickerPreference mQSIconColor;
    private ColorPickerPreference mQSTextColor;

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

        addPreferencesFromResource(R.xml.status_bar_expanded_qs_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mQSQuickPulldown =
                (CheckBoxPreference) findPreference(PREF_QS_QUICK_PULLDOWN);
        mQSQuickPulldown.setChecked(Settings.System.getInt(mResolver,
                Settings.System.QS_QUICK_PULLDOWN, 0) == 1);
        mQSQuickPulldown.setOnPreferenceChangeListener(this);

        mQSTiles = findPreference(PREF_QS_ORDER);

        mQSMainTiles =
                (CheckBoxPreference) findPreference(PREF_QS_MAIN_TILES);
        mQSMainTiles.setChecked(Settings.System.getInt(mResolver,
                Settings.System.QS_USE_MAIN_TILES, 1) == 1);
        mQSMainTiles.setOnPreferenceChangeListener(this);

        mQSLocationAdvanced =
                (CheckBoxPreference) findPreference(PREF_QS_LOCATION_ADVANCED);
        mQSLocationAdvanced.setChecked(Settings.System.getInt(mResolver,
                Settings.System.QS_LOCATION_ADVANCED, 0) == 1);
        mQSLocationAdvanced.setOnPreferenceChangeListener(this);

        mQSShowBrightnessSlider =
                (CheckBoxPreference) findPreference(PREF_QS_SHOW_BRIGHTNESS_SLIDER);
        mQSShowBrightnessSlider.setChecked(Settings.System.getInt(mResolver,
                Settings.System.QS_SHOW_BRIGHTNESS_SLIDER, 1) == 1);
        mQSShowBrightnessSlider.setOnPreferenceChangeListener(this);

        mQSBackgroundColor =
                (ColorPickerPreference) findPreference(PREF_QS_BACKGROUND_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.QS_BACKGROUND_COLOR,
                DEFAULT_BACKGROUND_COLOR); 
        mQSBackgroundColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQSBackgroundColor.setSummary(hexColor);
        mQSBackgroundColor.setAlphaSliderEnabled(true);
        mQSBackgroundColor.setOnPreferenceChangeListener(this);

        mQSIconColor =
                (ColorPickerPreference) findPreference(PREF_QS_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.QS_ICON_COLOR, WHITE); 
        mQSIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQSIconColor.setSummary(hexColor);
        mQSIconColor.setOnPreferenceChangeListener(this);

        mQSTextColor =
                (ColorPickerPreference) findPreference(PREF_QS_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.QS_TEXT_COLOR, WHITE); 
        mQSTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQSTextColor.setSummary(hexColor);
        mQSTextColor.setOnPreferenceChangeListener(this);

        updateQsTileCount();

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateQsTileCount();
    }

    private void updateQsTileCount() {
        int qsTileCount = QSTiles.determineTileCount(getActivity());
        mQSTiles.setSummary(getResources().getQuantityString(R.plurals.qs_tiles_summary,
                    qsTileCount, qsTileCount));
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
        String hex;
        int intHex;

        if (preference == mQSQuickPulldown) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.QS_QUICK_PULLDOWN, value ? 1 : 0);
            return true;
        } else if (preference == mQSMainTiles) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.QS_USE_MAIN_TILES, value ? 1 : 0);
            return true;
        } else if (preference == mQSLocationAdvanced) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.QS_LOCATION_ADVANCED, value ? 1 : 0);
            return true;
        } else if (preference == mQSShowBrightnessSlider) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.QS_SHOW_BRIGHTNESS_SLIDER, value ? 1 : 0);
            return true;
        } else if (preference == mQSBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_BACKGROUND_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.QS_TEXT_COLOR, intHex);
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

        StatusBarExpandedQsSettings getOwner() {
            return (StatusBarExpandedQsSettings) getTargetFragment();
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
                                    Settings.System.QS_QUICK_PULLDOWN, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_USE_MAIN_TILES, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_LOCATION_ADVANCED, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_SHOW_BRIGHTNESS_SLIDER, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_BACKGROUND_COLOR,
                                    DEFAULT_BACKGROUND_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_ICON_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_TEXT_COLOR, WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_QUICK_PULLDOWN, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_USE_MAIN_TILES, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_LOCATION_ADVANCED, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_SHOW_BRIGHTNESS_SLIDER, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_BACKGROUND_COLOR,
                                    0xff1b1f23);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_TEXT_COLOR,
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
