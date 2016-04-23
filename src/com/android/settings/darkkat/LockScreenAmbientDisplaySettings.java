/*
 * Copyright (C) 2016 DarkKat
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.LockScreenButtonBarHelper;
import com.android.internal.util.slim.ActionConfig;

import com.android.settings.fusion.SeekBarPreference;
import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;

public class LockScreenAmbientDisplaySettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CATEGORY_CURRENT_VALUES =
            "ambient_display_current_values_category";
    private static final String PREF_ENABLE_DOZE =
            "ambient_display_enable_doze";
    private static final String PREF_MODE =
            "ambient_display_mode";
    private static final String PREF_SHOW_BATTERY =
            "ambient_display_show_battery";
    private static final String PREF_SHOW_BUTTON_BAR =
            "ambient_display_show_button_bar";
    private static final String PREF_ENABLE_SCHEDULE =
            "ambient_display_enable_pulse_notification_schedule";
    private static final String PREF_OVERWRITE_VALUES =
            "ambient_display_overwrite_values";
    private static final String PREF_BRIGHTNESS =
            "ambient_display_brightness";
    private static final String PREF_PULSE_IN_NOTIFICATION =
            "ambient_display_pulse_in_notification";
    private static final String PREF_PULSE_IN_PICKUP =
            "ambient_display_pulse_in_pickup";
    private static final String PREF_PULSE_VISIBLE =
            "ambient_display_pulse_visible";
    private static final String PREF_PULSE_OUT =
            "ambient_display_pulse_out";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private static final int MODE_NOTIFICATION        = 1;
    private static final int MODE_PICKUP              = 2;

    private static final int DEFAULT_PULSE_IN_NOTIFICATION_ANDROID = 900;
    private static final int DEFAULT_PULSE_IN_NOTIFICATION_DARKKAT = 1500;
    private static final int DEFAULT_PULSE_IN_PICKUP_ANDROID       = 300;
    private static final int DEFAULT_PULSE_IN_PICKUP_DARKKAT       = 600;
    private static final int DEFAULT_PULSE_VISIBLE_ANDROID         = 3000;
    private static final int DEFAULT_PULSE_VISIBLE_DARKKAT         = 4000;
    private static final int DEFAULT_PULSE_OUT_ANDROID             = 600;
    private static final int DEFAULT_PULSE_OUT_DARKKAT             = 900;

    private static final String DEFAULT_PULSE_NOTIFICATION_SCHEDULE_ANDROID =
            "10s,30s,60s";
    // MUST be empty, (disabled) !
    private static final String DEFAULT_PULSE_NOTIFICATION_SCHEDULE_DARKKAT =
            "";

    private SwitchPreference mEnableDoze;
    private ListPreference mMode;
    private SwitchPreference mShowBattery;
    private SwitchPreference mShowButtonBar;
    private SwitchPreference mEnableSchedule;
    private SwitchPreference mOverwriteValues;
    private SeekBarPreference mBrightness;
    private ListPreference mPulseInNotification;
    private ListPreference mPulseInPickup;
    private ListPreference mPulseVisible;
    private ListPreference mPulseOut;

    private int mBrightnessConfig;

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

        addPreferencesFromResource(R.xml.lock_screen_ambient_display_settings);

        mResolver = getContentResolver();

        final boolean isDozeEnabled = Settings.Secure.getInt(mResolver,
                Settings.Secure.DOZE_ENABLED, 1) == 1;
        mBrightnessConfig = getResources().getInteger(
                com.android.internal.R.integer.config_screenBrightnessDoze);

        PreferenceCategory catCurrentValues =
                (PreferenceCategory) findPreference(PREF_CATEGORY_CURRENT_VALUES);

        mEnableDoze = (SwitchPreference) findPreference(PREF_ENABLE_DOZE);
        mEnableDoze.setChecked(isDozeEnabled);
        mEnableDoze.setOnPreferenceChangeListener(this);

        if (isDozeEnabled) {
            final boolean overwriteValues = Settings.System.getInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_OVERWRITE_VALUES, 0) == 1;

            mMode = (ListPreference) findPreference(PREF_MODE);
            final int mode = Settings.System.getInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_MODE, 0);
            mMode.setValue(String.valueOf(mode));
            mMode.setSummary(mMode.getEntry());
            mMode.setOnPreferenceChangeListener(this);

            mShowBattery = (SwitchPreference) findPreference(PREF_SHOW_BATTERY);
            mShowBattery.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_SHOW_BATTERY, 1) == 1);
            mShowBattery.setOnPreferenceChangeListener(this);

            mShowButtonBar = (SwitchPreference) findPreference(PREF_SHOW_BUTTON_BAR);
            mShowButtonBar.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_SHOW_BUTTON_BAR, 0) == 1);
            if (isButtonBarEnabled()) {
                mShowButtonBar.setOnPreferenceChangeListener(this);
            } else {
                mShowButtonBar.setSummary(R.string.ambient_display_show_button_bar_disabled_summary);
                mShowButtonBar.setEnabled(false);
            }

            mEnableSchedule = (SwitchPreference) findPreference(PREF_ENABLE_SCHEDULE);
            mEnableSchedule.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_ENABLE_PULSE_NOTIFICATION_SCHEDULE, 1) == 1);
            mEnableSchedule.setOnPreferenceChangeListener(this);

            mOverwriteValues = (SwitchPreference) findPreference(PREF_OVERWRITE_VALUES);
            mOverwriteValues.setChecked(overwriteValues);
            mOverwriteValues.setOnPreferenceChangeListener(this);

            if (overwriteValues) {
                mBrightness = (SeekBarPreference) findPreference(PREF_BRIGHTNESS);
                final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                final int maxBrightness = pm.getMaximumScreenBrightnessSetting();
                int currentBrightness = Settings.System.getInt(getContentResolver(),
                        Settings.System.AMBIENT_DISPLAY_BRIGHTNESS, mBrightnessConfig);
                mBrightness.setValue(currentBrightness / 1);
                mBrightness.setOnPreferenceChangeListener(this);

                if (mode != MODE_PICKUP) {
                    mPulseInNotification = (ListPreference) findPreference(PREF_PULSE_IN_NOTIFICATION);
                    final int pulseInNotification = Settings.System.getInt(mResolver,
                            Settings.System.AMBIENT_DISPLAY_PULSE_IN_NOTIFICATION,
                            DEFAULT_PULSE_IN_NOTIFICATION_ANDROID);
                    mPulseInNotification.setValue(String.valueOf(pulseInNotification));
                    mPulseInNotification.setSummary(mPulseInNotification.getEntry());
                    mPulseInNotification.setOnPreferenceChangeListener(this);
                } else {
                    catCurrentValues.removePreference(findPreference(PREF_PULSE_IN_NOTIFICATION));
                }

                if (mode != MODE_NOTIFICATION) {
                    mPulseInPickup = (ListPreference) findPreference(PREF_PULSE_IN_PICKUP);
                    final int pulseInPickup = Settings.System.getInt(mResolver,
                            Settings.System.AMBIENT_DISPLAY_PULSE_IN_PICKUP,
                            DEFAULT_PULSE_IN_PICKUP_ANDROID);
                    mPulseInPickup.setValue(String.valueOf(pulseInPickup));
                    mPulseInPickup.setSummary(mPulseInPickup.getEntry());
                    mPulseInPickup.setOnPreferenceChangeListener(this);
                } else {
                    catCurrentValues.removePreference(findPreference(PREF_PULSE_IN_PICKUP));
                }

                mPulseVisible = (ListPreference) findPreference(PREF_PULSE_VISIBLE);
                final int pulseVisible = Settings.System.getInt(mResolver,
                        Settings.System.AMBIENT_DISPLAY_PULSE_VISIBLE,
                        DEFAULT_PULSE_VISIBLE_ANDROID);
                mPulseVisible.setValue(String.valueOf(pulseVisible));
                mPulseVisible.setSummary(mPulseVisible.getEntry());
                mPulseVisible.setOnPreferenceChangeListener(this);

                mPulseOut = (ListPreference) findPreference(PREF_PULSE_OUT);
                final int pulseOut = Settings.System.getInt(mResolver,
                        Settings.System.AMBIENT_DISPLAY_PULSE_OUT, DEFAULT_PULSE_OUT_ANDROID);
                mPulseOut.setValue(String.valueOf(pulseOut));
                mPulseOut.setSummary(mPulseOut.getEntry());
                mPulseOut.setOnPreferenceChangeListener(this);
            } else {
                catCurrentValues.removePreference(findPreference(PREF_BRIGHTNESS));
                catCurrentValues.removePreference(findPreference(PREF_PULSE_IN_NOTIFICATION));
                catCurrentValues.removePreference(findPreference(PREF_PULSE_IN_PICKUP));
                catCurrentValues.removePreference(findPreference(PREF_PULSE_VISIBLE));
                catCurrentValues.removePreference(findPreference(PREF_PULSE_OUT));
                removePreference(PREF_CATEGORY_CURRENT_VALUES);
            }
        } else {
            removePreference(PREF_SHOW_BATTERY);
            removePreference(PREF_SHOW_BUTTON_BAR);
            removePreference(PREF_MODE);
            removePreference(PREF_ENABLE_SCHEDULE);
            removePreference(PREF_OVERWRITE_VALUES);
            catCurrentValues.removePreference(findPreference(PREF_BRIGHTNESS));
            catCurrentValues.removePreference(findPreference(PREF_PULSE_IN_NOTIFICATION));
            catCurrentValues.removePreference(findPreference(PREF_PULSE_IN_PICKUP));
            catCurrentValues.removePreference(findPreference(PREF_PULSE_VISIBLE));
            catCurrentValues.removePreference(findPreference(PREF_PULSE_OUT));
            removePreference(PREF_CATEGORY_CURRENT_VALUES);
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
        boolean value;
        int intValue;
        int index;

        if (preference == mEnableDoze) {
            value = (Boolean) newValue;
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.DOZE_ENABLED, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_MODE,
                    intValue);
            mMode.setSummary(mMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowBattery) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_SHOW_BATTERY, value ? 1 : 0);
            return true;
        } else if (preference == mShowButtonBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_SHOW_BUTTON_BAR, value ? 1 : 0);
            return true;
        } else if (preference == mEnableSchedule) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_ENABLE_PULSE_NOTIFICATION_SCHEDULE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mOverwriteValues) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_OVERWRITE_VALUES, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBrightness) {
            intValue =  (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_BRIGHTNESS, intValue);
            return true;
        } else if (preference == mPulseInNotification) {
            intValue = Integer.valueOf((String) newValue);
            index = mPulseInNotification.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_PULSE_IN_NOTIFICATION,
                    intValue);
            mPulseInNotification.setSummary(mPulseInNotification.getEntries()[index]);
            return true;
        } else if (preference == mPulseInPickup) {
            intValue = Integer.valueOf((String) newValue);
            index = mPulseInPickup.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_PULSE_IN_PICKUP,
                    intValue);
            mPulseInPickup.setSummary(mPulseInPickup.getEntries()[index]);
            return true;
        } else if (preference == mPulseVisible) {
            intValue = Integer.valueOf((String) newValue);
            index = mPulseVisible.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_PULSE_VISIBLE,
                    intValue);
            mPulseVisible.setSummary(mPulseVisible.getEntries()[index]);
            return true;
        } else if (preference == mPulseOut) {
            intValue = Integer.valueOf((String) newValue);
            index = mPulseOut.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.AMBIENT_DISPLAY_PULSE_OUT,
                    intValue);
            mPulseOut.setSummary(mPulseOut.getEntries()[index]);
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

        LockScreenAmbientDisplaySettings getOwner() {
            return (LockScreenAmbientDisplaySettings) getTargetFragment();
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
                            Settings.Secure.putInt(getOwner().mResolver,
                                    Settings.Secure.DOZE_ENABLED, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_SHOW_BATTERY, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_SHOW_BUTTON_BAR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_ENABLE_PULSE_NOTIFICATION_SCHEDULE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_OVERWRITE_VALUES, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BRIGHTNESS,
                                    getOwner().mBrightnessConfig);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_PULSE_IN_NOTIFICATION,
                                    DEFAULT_PULSE_IN_NOTIFICATION_ANDROID);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_PULSE_IN_PICKUP,
                                    DEFAULT_PULSE_IN_PICKUP_ANDROID);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_PULSE_VISIBLE,
                                    DEFAULT_PULSE_VISIBLE_ANDROID);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_PULSE_OUT,
                                    DEFAULT_PULSE_OUT_ANDROID);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.Secure.putInt(getOwner().mResolver,
                                    Settings.Secure.DOZE_ENABLED, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_SHOW_BATTERY, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_SHOW_BUTTON_BAR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_ENABLE_PULSE_NOTIFICATION_SCHEDULE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_OVERWRITE_VALUES, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_BRIGHTNESS,
                                    getOwner().mBrightnessConfig);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_PULSE_IN_NOTIFICATION,
                                    DEFAULT_PULSE_IN_NOTIFICATION_DARKKAT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_PULSE_IN_PICKUP,
                                    DEFAULT_PULSE_IN_PICKUP_DARKKAT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_PULSE_VISIBLE,
                                    DEFAULT_PULSE_VISIBLE_DARKKAT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.AMBIENT_DISPLAY_PULSE_OUT,
                                    DEFAULT_PULSE_OUT_DARKKAT);
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

    private boolean isButtonBarEnabled() {
        ArrayList<ActionConfig> actionConfigs =
                LockScreenButtonBarHelper.getButtonBarConfig(getActivity());
        return actionConfigs.size() != 0;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.LOCK_SCREEN;
    }
}
