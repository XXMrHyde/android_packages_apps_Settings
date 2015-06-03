/*
 * Copyright (C) 2014 DarkKat
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
import android.graphics.Color;
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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarBatteryBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_INDICATOR =
            "battery_bar_indicator";
    private static final String PREF_THICKNESS =
            "battery_bar_thickness";
    private static final String PREF_CHARGE_ANIMATION_SPEED =
            "battery_bar_charge_animation_speed";
    private static final String PREF_COLOR =
            "battery_bar_color";

    private static final int WHITE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private static final int HIDDEN = 0;

    private ListPreference mIndicator;
    private ListPreference mThickness;
    private ListPreference mChargeAnimationSpeed;
    private ColorPickerPreference mColor;

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

        addPreferencesFromResource(R.xml.status_bar_battery_bar_settings);
        mResolver = getActivity().getContentResolver();

        mIndicator =
                (ListPreference) findPreference(PREF_INDICATOR);
        int indicator = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_BATTERY_BAR_INDICATOR, 0);
        mIndicator.setValue(String.valueOf(indicator));
        mIndicator.setSummary(mIndicator.getEntry());
        mIndicator.setOnPreferenceChangeListener(this);

        if (indicator == HIDDEN) {
            removePreference(PREF_THICKNESS);
            removePreference(PREF_CHARGE_ANIMATION_SPEED);
            removePreference(PREF_COLOR);
        } else {
            mThickness =
                    (ListPreference) findPreference(PREF_THICKNESS);
            int thickness = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_BATTERY_BAR_THICKNESS, 1);
            mThickness.setValue(String.valueOf(thickness));
            mThickness.setSummary(mThickness.getEntry());
            mThickness.setOnPreferenceChangeListener(this);

            mChargeAnimationSpeed =
                    (ListPreference) findPreference(PREF_CHARGE_ANIMATION_SPEED);
            int chargeAnimationSpeed = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_BATTERY_BAR_CHARGING_ANIMATION_SPEED, 0);
            mChargeAnimationSpeed.setValue(String.valueOf(chargeAnimationSpeed));
            mChargeAnimationSpeed.setSummary(mChargeAnimationSpeed.getEntry());
            mChargeAnimationSpeed.setOnPreferenceChangeListener(this);

            mColor =
                    (ColorPickerPreference) findPreference(PREF_COLOR);
            int intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_BAR_COLOR, WHITE);
            mColor.setNewPreviewColor(intColor);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColor.setSummary(hexColor);
            mColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mColor.setOnPreferenceChangeListener(this);
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
        int intValue;
        int index;
        int intHex;
        String hex;

        if (preference == mIndicator) {
            intValue = Integer.valueOf((String) newValue);
            index = mIndicator.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_BAR_INDICATOR, intValue);
            mIndicator.setSummary(mIndicator.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mThickness) {
            intValue = Integer.valueOf((String) newValue);
            index = mThickness.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_BAR_THICKNESS, intValue);
            mThickness.setSummary(mThickness.getEntries()[index]);
            return true;
        } else if (preference == mChargeAnimationSpeed) {
            intValue = Integer.valueOf((String) newValue);
            index = mChargeAnimationSpeed.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_BAR_CHARGING_ANIMATION_SPEED, intValue);
            mChargeAnimationSpeed.setSummary(mChargeAnimationSpeed.getEntries()[index]);
            return true;
        } else if (preference == mColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_BAR_COLOR, intHex);
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

        StatusBarBatteryBarSettings getOwner() {
            return (StatusBarBatteryBarSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_BATTERY_BAR_INDICATOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_BAR_THICKNESS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_BAR_CHARGING_ANIMATION_SPEED, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_BAR_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                             Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_BAR_INDICATOR, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_BAR_THICKNESS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_BAR_CHARGING_ANIMATION_SPEED, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_BAR_COLOR,
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
