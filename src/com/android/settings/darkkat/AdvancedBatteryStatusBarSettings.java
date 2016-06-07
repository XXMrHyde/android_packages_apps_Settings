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

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class AdvancedBatteryStatusBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_CIRCLE_DOTTED =
            "battery_status_bar_cat_circle_dotted";
    private static final String PREF_ICON_INDICATOR =
            "battery_status_bar_icon_indicator";
    private static final String PREF_SHOW_TEXT =
            "battery_status_bar_show_text";
    private static final String PREF_CIRCLE_DOT_INTERVAL =
            "battery_status_bar_circle_dot_interval";
    private static final String PREF_CIRCLE_DOT_LENGTH =
            "battery_status_bar_circle_dot_length";
    private static final String PREF_CUT_OUT_TEXT =
            "battery_status_bar_cut_out_text";
    private static final String PREF_SHOW_BATTERY_BAR =
            "battery_status_bar_show_battery_bar";
    private static final String PREF_SHOW_CHARGE_ANIMATION =
            "battery_status_bar_show_charge_animation";
    private static final String PREF_TEXT_COLOR =
            "battery_status_bar_text_color";

    private static final int WHITE           = 0xffffffff;
    private static final int BLACK           = 0xff000000;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ListPreference mIconIndicator;
    private SwitchPreference mShowText;
    private ListPreference mCircleDotInterval;
    private ListPreference mCircleDotLength;
    private SwitchPreference mCutOutText;
    private SwitchPreference mShowBatteryBar;
    private SwitchPreference mShowChargeAnimation;
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

        addPreferencesFromResource(R.xml.advanced_battery_status_bar_settings);
        mResolver = getActivity().getContentResolver();

        final boolean isBatteryIconCircle = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_ICON_INDICATOR, 0) == 2;
        final boolean showText = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_TEXT, 0) == 1;
        final boolean showCircleDotted = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_INTERVAL, 0) != 0;

        PreferenceCategory catCircleDotted =
                (PreferenceCategory) findPreference(PREF_CAT_CIRCLE_DOTTED);

        mIconIndicator = (ListPreference) findPreference(PREF_ICON_INDICATOR);
        int iconIndicator = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_ICON_INDICATOR, 0);
        mIconIndicator.setValue(String.valueOf(iconIndicator));
        mIconIndicator.setSummary(mIconIndicator.getEntry());
        mIconIndicator.setOnPreferenceChangeListener(this);

        int intColor;
        String hexColor;

        mShowText = (SwitchPreference) findPreference(PREF_SHOW_TEXT);
        mShowText.setChecked(showText);
        mShowText.setOnPreferenceChangeListener(this);

        if (isBatteryIconCircle) {
            mCircleDotInterval = (ListPreference) findPreference(PREF_CIRCLE_DOT_INTERVAL);
            int circleDotInterval = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_INTERVAL, 0);
            mCircleDotInterval.setValue(String.valueOf(circleDotInterval));
            mCircleDotInterval.setOnPreferenceChangeListener(this);
            updateCircleDotIntervalSummary(circleDotInterval);

            if (showCircleDotted) {
                mCircleDotLength = (ListPreference) findPreference(PREF_CIRCLE_DOT_LENGTH);
                int circleDotLength = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_LENGTH, 0);
                mCircleDotLength.setValue(String.valueOf(circleDotLength));
                mCircleDotLength.setSummary(mCircleDotLength.getEntry());
                mCircleDotLength.setOnPreferenceChangeListener(this);
            } else {
                catCircleDotted.removePreference(findPreference(PREF_CIRCLE_DOT_LENGTH));
            }
        } else {
            catCircleDotted.removePreference(findPreference(PREF_CIRCLE_DOT_INTERVAL));
            catCircleDotted.removePreference(findPreference(PREF_CIRCLE_DOT_LENGTH));
            removePreference(PREF_CAT_CIRCLE_DOTTED);
        }

        mCutOutText = (SwitchPreference) findPreference(PREF_CUT_OUT_TEXT);
        mCutOutText.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CUT_OUT_TEXT, 1) == 1);
        mCutOutText.setOnPreferenceChangeListener(this);

        mShowBatteryBar = (SwitchPreference) findPreference(PREF_SHOW_BATTERY_BAR);
        mShowBatteryBar.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_BATTERY_BAR, 0) == 1);
        mShowBatteryBar.setOnPreferenceChangeListener(this);

        mShowChargeAnimation = (SwitchPreference) findPreference(PREF_SHOW_CHARGE_ANIMATION);
        mShowChargeAnimation.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_CHARGE_ANIMATION, 0) == 1);
        mShowChargeAnimation.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_TEXT_COLOR, WHITE);
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setResetColors(WHITE, WHITE);
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

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;
        int intValue;
        int index;
        String hex;
        int intHex;

        if (preference == mIconIndicator) {
            intValue = Integer.valueOf((String) newValue);
            index = mIconIndicator.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_ICON_INDICATOR,
                    intValue);
            mIconIndicator.setSummary(mIconIndicator.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowText) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_TEXT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mCircleDotInterval) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotInterval.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_INTERVAL,
                    intValue);
            refreshSettings();
            return true;
        } else if (preference == mCircleDotLength) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotLength.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_LENGTH,
                    intValue);
            mCircleDotLength.setSummary(mCircleDotLength.getEntries()[index]);
            return true;
        } else if (preference == mCutOutText) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CUT_OUT_TEXT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowBatteryBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_BATTERY_BAR,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowChargeAnimation) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_CHARGE_ANIMATION,
                    value ? 1 : 0);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_TEXT_COLOR, intHex);
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

        AdvancedBatteryStatusBarSettings getOwner() {
            return (AdvancedBatteryStatusBarSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_ICON_INDICATOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_TEXT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_INTERVAL, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_LENGTH, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CUT_OUT_TEXT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_BATTERY_BAR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_CHARGE_ANIMATION, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_TEXT_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_ICON_INDICATOR, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_TEXT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_INTERVAL, 4);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CIRCLE_DOT_LENGTH, 5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_CUT_OUT_TEXT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_BATTERY_BAR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_SHOW_CHARGE_ANIMATION, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BARS_BATTERY_TEXT_COLOR,
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

    private void updateCircleDotIntervalSummary(int circleDotInterval) {
        CharSequence summary;
        if (circleDotInterval != 0) {
            summary = mCircleDotInterval.getEntry();
        } else {
            summary = getResources().getString(R.string.battery_status_circle_dot_no_dot_summary);
        }
        mCircleDotInterval.setSummary(summary);
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
