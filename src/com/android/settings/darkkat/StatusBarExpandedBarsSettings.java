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
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.util.darkkat.SBEPanelColorHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Locale;

public class StatusBarExpandedBarsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_COLORS =
            "expanded_bars_cat_colors";
    private static final String PREF_BG_COLOR =
            "expanded_bars_background_color";
    private static final String PREF_ICON_COLOR =
            "expanded_bars_icon_color";
    private static final String PREF_RIPPLE_COLOR =
            "expanded_bars_ripple_color";
    private static final String PREF_TEXT_COLOR =
            "expanded_bars_text_color";

    private static final int SYSTEMUI_PRIMARY =
            0xff263238;
    private static final int DARKKAT_BLUE_GREY =
            0xff1b1f23;
    private static final int WHITE =
            0xffffffff;
    private static final int TRANSLUCENT_WHITE =
            0x33ffffff;
    private static final int HOLO_BLUE_LIGHT =
            0xff33b5e5;
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

        addPreferencesFromResource(R.xml.status_bar_expanded_bars_settings);
        mResolver = getContentResolver();

        int intColor;
        String hexColor;

        final boolean supportsMobileData = DeviceUtils.deviceSupportsMobileData(getActivity());
        final boolean isLockClockInstalled =
                Utils.isPackageInstalled(getActivity(), "com.cyanogenmod.lockclock");
        final boolean showQuickAccessBar = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_QAB, 1) == 1;
        final boolean showBrightnessSliderBar = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_BRIGHTNESS_SLIDER, 1) == 1;
        final boolean showWifiBar = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_WIFI_BAR, 0) == 1;
        final boolean showMobileBar = supportsMobileData && Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_MOBILE_BAR, 0) == 1;
        final boolean showBatteryStatusBar = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_BATTERY_STATUS_BAR, 0) == 1;
        final boolean showWeatherBar = isLockClockInstalled && Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_WEATHER, 0) == 1;

        final boolean disableAdvancedSettings = !showQuickAccessBar
                && !showWeatherBar
                && !showBatteryStatusBar;
        final boolean removeRippleColorPicker = !showQuickAccessBar
                && !showBrightnessSliderBar
                && !showWeatherBar;
        final boolean removeTextColorPicker = !showWifiBar
                && !showMobileBar
                && !showBatteryStatusBar
                && !showWeatherBar;
        final boolean allBarsHidden = !showQuickAccessBar
                && !showBrightnessSliderBar
                && !showWifiBar
                && !showMobileBar
                && !showBatteryStatusBar
                && !showWeatherBar;

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        Preference advancedSettings =
                findPreference("expanded_bars_advanced_settings");
        if (disableAdvancedSettings) {
            if (allBarsHidden) {
                advancedSettings.setSummary(
                        getResources().getString(R.string.expanded_bars_no_bars_title));
            } else {
                advancedSettings.setSummary(
                        getResources().getString(R.string.expanded_bars_no_advanced_settings_summary));
            }
        }
        advancedSettings.setEnabled(!disableAdvancedSettings);

        if (!removeRippleColorPicker) {
            mRippleColor =
                    (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
            intColor = SBEPanelColorHelper.getRippleColor(getActivity()); 
            mRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRippleColor.setSummary(hexColor);
            mRippleColor.setDefaultColors(TRANSLUCENT_WHITE, TRANSLUCENT_HOLO_BLUE_LIGHT);
            mRippleColor.setOnPreferenceChangeListener(this);

            catColors.removePreference(findPreference("expanded_bars_ripple_color_hidden"));
        } else {
            catColors.removePreference(findPreference(PREF_RIPPLE_COLOR));
            if (allBarsHidden) {
                catColors.removePreference(findPreference("expanded_bars_ripple_color_hidden"));
            }
        }
        if (!removeTextColorPicker) {
            mTextColor =
                    (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_TEXT_COLOR,
                    WHITE); 
            mTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setSummary(hexColor);
            mTextColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mTextColor.setOnPreferenceChangeListener(this);

            catColors.removePreference(findPreference("expanded_bars_text_color_hidden"));
        } else {
            catColors.removePreference(findPreference(PREF_TEXT_COLOR));
            if (allBarsHidden) {
                catColors.removePreference(findPreference("expanded_bars_text_color_hidden"));
            }
        }
        if (!allBarsHidden) {
            catColors.removePreference(findPreference("expanded_bars_no_bars"));

            mBackgroundColor =
                    (ColorPickerPreference) findPreference(PREF_BG_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_BACKGROUND_COLOR,
                    SYSTEMUI_PRIMARY); 
            mBackgroundColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBackgroundColor.setSummary(hexColor);
            mBackgroundColor.setDefaultColors(SYSTEMUI_PRIMARY, DARKKAT_BLUE_GREY);
            mBackgroundColor.setOnPreferenceChangeListener(this);

            mIconColor =
                    (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_ICON_COLOR,
                    WHITE); 
            mIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mIconColor.setSummary(hexColor);
            mIconColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mIconColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_BG_COLOR));
            catColors.removePreference(findPreference(PREF_ICON_COLOR));
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSettings();
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
                    Settings.System.STATUS_BAR_EXPANDED_BACKGROUND_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_RIPPLE_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_TEXT_COLOR, intHex);
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

        StatusBarExpandedBarsSettings getOwner() {
            return (StatusBarExpandedBarsSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_EXPANDED_BACKGROUND_COLOR,
                                    SYSTEMUI_PRIMARY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_RIPPLE_COLOR,
                                    TRANSLUCENT_WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_TEXT_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_BACKGROUND_COLOR,
                                    DARKKAT_BLUE_GREY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_RIPPLE_COLOR,
                                    TRANSLUCENT_HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_TEXT_COLOR,
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
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
