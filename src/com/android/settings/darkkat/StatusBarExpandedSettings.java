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
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
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

import com.android.internal.util.darkkat.SBEPanelColorHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Locale;

public class StatusBarExpandedSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_QAB =
            "status_bar_expanded_cat_qab";
    private static final String PREF_CAT_WEATHER =
            "status_bar_expanded_cat_weather";
    private static final String PREF_CAT_COLORS =
            "status_bar_expanded_cat_colors";
    private static final String PREF_QUICK_PULLDOWN =
            "status_bar_expanded_quick_pulldown";
    private static final String PREF_SHOW_BRIGHTNESS_SLIDER =
            "status_bar_expanded_show_brightness_slider";
    private static final String PREF_SHOW_QAB =
            "status_bar_expanded_show_qab";
    private static final String PREF_SHOW_WEATHER =
            "status_bar_expanded_show_weather";
    private static final String PREF_LOCK_CLOCK_MISSING =
            "status_bar_expanded_lock_clock_missing";
    private static final String PREF_BG_COLOR =
            "status_bar_expanded_background_color";
    private static final String PREF_ICON_COLOR =
            "status_bar_expanded_icon_color";
    private static final String PREF_RIPPLE_COLOR =
            "status_bar_expanded_ripple_color";
    private static final String PREF_TEXT_COLOR =
            "status_bar_expanded_text_color";

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

    private ListPreference mQuickPulldown;
    private SwitchPreference mShowBrightnessSlider;
    private SwitchPreference mShowQab;
    private SwitchPreference mShowWeather;
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

        addPreferencesFromResource(R.xml.status_bar_expanded_settings);
        mResolver = getContentResolver();

        int intColor;
        String hexColor;

        boolean showBrightnessSlider = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_BRIGHTNESS_SLIDER, 1) == 1;
        boolean showQab = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_QAB, 1) == 1;
        boolean showWeather = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SHOW_WEATHER, 0) == 1;
        boolean isLockClockInstalled = Utils.isPackageInstalled(getActivity(), "com.cyanogenmod.lockclock");

        mQuickPulldown =
                (ListPreference) findPreference(PREF_QUICK_PULLDOWN);
        int quickPulldown = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_QUICK_PULLDOWN, 0);
        mQuickPulldown.setValue(String.valueOf(quickPulldown));
        updateQuickPulldownSummary(quickPulldown);
        mQuickPulldown.setOnPreferenceChangeListener(this);

        mShowBrightnessSlider =
                (SwitchPreference) findPreference(PREF_SHOW_BRIGHTNESS_SLIDER);
        mShowBrightnessSlider.setChecked(showBrightnessSlider);
        mShowBrightnessSlider.setOnPreferenceChangeListener(this);

        mShowQab =
                (SwitchPreference) findPreference(PREF_SHOW_QAB);
        mShowQab.setChecked(showQab);
        mShowQab.setOnPreferenceChangeListener(this);

        mShowWeather =
                (SwitchPreference) findPreference(PREF_SHOW_WEATHER);
        mShowWeather.setChecked(showWeather);
        mShowWeather.setOnPreferenceChangeListener(this);

        PreferenceCategory catQab =
                (PreferenceCategory) findPreference(PREF_CAT_QAB);
        PreferenceCategory catWeather =
                (PreferenceCategory) findPreference(PREF_CAT_WEATHER);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        if (!showQab) {
            catQab.removePreference(findPreference("status_bar_expanded_qab_buttons"));
        }

        if (!showWeather || !isLockClockInstalled) {
            catWeather.removePreference(findPreference("status_bar_expanded_weather"));
        }
        if (isLockClockInstalled) {
            catWeather.removePreference(findPreference("status_bar_expanded_lock_clock_missing"));
        }

        if (showBrightnessSlider || showQab || showWeather) {
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

            mRippleColor =
                    (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
            intColor = SBEPanelColorHelper.getRippleColor(getActivity()); 
            mRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRippleColor.setSummary(hexColor);
            mRippleColor.setDefaultColors(TRANSLUCENT_WHITE, TRANSLUCENT_HOLO_BLUE_LIGHT);
            mRippleColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_BG_COLOR));
            catColors.removePreference(findPreference(PREF_ICON_COLOR));
            catColors.removePreference(findPreference(PREF_RIPPLE_COLOR));
        }

        if (showWeather && isLockClockInstalled) {
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
        } else {
            catColors.removePreference(findPreference(PREF_TEXT_COLOR));
        }

        if (!showBrightnessSlider && !showQab && (!showWeather || !isLockClockInstalled)) {
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

        if (preference == mQuickPulldown) {
            int intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_QUICK_PULLDOWN,
                    intValue);
            updateQuickPulldownSummary(intValue);
            return true;
        } else if (preference == mShowBrightnessSlider) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_BRIGHTNESS_SLIDER,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowQab) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_QAB,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowWeather) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SHOW_WEATHER,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBackgroundColor) {
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

        StatusBarExpandedSettings getOwner() {
            return (StatusBarExpandedSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_EXPANDED_QUICK_PULLDOWN, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_SHOW_BRIGHTNESS_SLIDER, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_SHOW_QAB, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_SHOW_WEATHER, 0);
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
                                    Settings.System.STATUS_BAR_EXPANDED_QUICK_PULLDOWN, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_SHOW_BRIGHTNESS_SLIDER, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_SHOW_QAB, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_SHOW_WEATHER, 1);
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

    private void updateQuickPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.disabled_title));
        } else {
            Locale l = Locale.getDefault();
            boolean isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL;
            String direction = res.getString(value == 2
                    ? (isRtl
                            ? R.string.status_bar_expanded_quick_pulldown_summary_right
                            : R.string.status_bar_expanded_quick_pulldown_summary_left)
                    : (isRtl
                            ? R.string.status_bar_expanded_quick_pulldown_summary_left
                            : R.string.status_bar_expanded_quick_pulldown_summary_right));
            mQuickPulldown.setSummary(res.getString(R.string.status_bar_expanded_quick_pulldown_summary, direction));
        }
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
