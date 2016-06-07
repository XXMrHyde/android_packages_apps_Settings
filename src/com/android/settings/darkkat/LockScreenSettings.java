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
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.LockScreenColorHelper;
import com.android.internal.util.darkkat.WeatherHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_WEATHER_SERVICE_MISSING =
            "weather_service_missing";
    private static final String PREF_BACKGROUND_COLOR =
            "lock_screen_background_color";
    private static final String PREF_ICON_COLOR =
            "lock_screen_icon_color";
    private static final String PREF_RIPPLE_COLOR =
            "lock_screen_ripple_color";
    private static final String PREF_TEXT_COLOR =
            "lock_screen_text_color";

    private static final int TRANSLUCENT_DARKKAT_BLUE_GREY =
            0xd41b1f23;
    private static final int TRANSLUCENT_BG_WHITE =
            0x4dffffff;
    private static final int WHITE =
            0xffffffff;
    private static final int HOLO_BLUE_LIGHT =
            0xff33b5e5;
    private static final int TRANSLUCENT_WHITE =
            0x33ffffff;
    private static final int TRANSLUCENT_HOLO_BLUE_LIGHT =
            0x3333b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mBackgroundColor;
    private ColorPickerPreference mIconColor;
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

        addPreferencesFromResource(R.xml.lock_screen_settings);
        mResolver = getContentResolver();

        int intColor;
        String hexColor;

        // Remove the weather settings preference if the weather service is not installed or disabled
        // and show an info preference instead
        final int weatherServiceAvailability = WeatherHelper.getWeatherServiceAvailability(getActivity());
        if (weatherServiceAvailability != WeatherHelper.PACKAGE_ENABLED) {
            removePreference("lock_screen_weather_settings");
            if (weatherServiceAvailability == WeatherHelper.PACKAGE_DISABLED) {
                Preference weatherServiceMissing = findPreference(PREF_WEATHER_SERVICE_MISSING);
                final CharSequence summary = getResources().getString(DeviceUtils.isPhone(getActivity())
                        ? R.string.weather_service_disabled_summary
                        : R.string.weather_service_disabled_tablet_summary);
                weatherServiceMissing.setTitle(getResources().getString(R.string.weather_service_disabled_title));
                weatherServiceMissing.setSummary(summary);
            }
        } else {
            removePreference(PREF_WEATHER_SERVICE_MISSING);
        }

        if (!isDozeAvailable()) {
            removePreference("lock_screen_ambient_display");
        }

        mBackgroundColor =
                (ColorPickerPreference) findPreference(PREF_BACKGROUND_COLOR);
        intColor = LockScreenColorHelper.getBackgroundColor(getActivity());
        mBackgroundColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBackgroundColor.setSummary(hexColor);
        mBackgroundColor.setResetColors(TRANSLUCENT_BG_WHITE, TRANSLUCENT_DARKKAT_BLUE_GREY);
        mBackgroundColor.setOnPreferenceChangeListener(this);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_ICON_COLOR, WHITE);
        mIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setResetColors(WHITE, HOLO_BLUE_LIGHT);
        mIconColor.setOnPreferenceChangeListener(this);

        mRippleColor =
                (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
        intColor = LockScreenColorHelper.getRippleColor(getActivity());
        mRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRippleColor.setSummary(hexColor);
        mRippleColor.setResetColors(TRANSLUCENT_WHITE, TRANSLUCENT_HOLO_BLUE_LIGHT);
        mRippleColor.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_TEXT_COLOR, WHITE);
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setResetColors(WHITE, HOLO_BLUE_LIGHT);
        mTextColor.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    private boolean isDozeAvailable() {
        String name = Build.IS_DEBUGGABLE ? SystemProperties.get("debug.doze.component") : null;
        if (TextUtils.isEmpty(name)) {
            name = getActivity().getResources().getString(
                    com.android.internal.R.string.config_dozeComponent);
        }
        return !TextUtils.isEmpty(name);
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
                    Settings.System.LOCK_SCREEN_BACKGROUND_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_RIPPLE_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_TEXT_COLOR, intHex);
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

        LockScreenSettings getOwner() {
            return (LockScreenSettings) getTargetFragment();
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
                                    Settings.System.LOCK_SCREEN_BACKGROUND_COLOR,
                                    TRANSLUCENT_BG_WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_RIPPLE_COLOR,
                                    TRANSLUCENT_WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_BACKGROUND_COLOR,
                                    TRANSLUCENT_DARKKAT_BLUE_GREY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_RIPPLE_COLOR,
                                    TRANSLUCENT_HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCK_SCREEN_TEXT_COLOR,
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

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.LOCK_SCREEN;
    }
}
