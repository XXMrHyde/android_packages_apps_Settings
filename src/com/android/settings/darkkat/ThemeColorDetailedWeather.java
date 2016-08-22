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
import android.content.Intent;
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

import com.android.internal.util.darkkat.DetailedWeatherHelper;
import com.android.internal.util.darkkat.WeatherHelper;
import com.android.internal.util.darkkat.WeatherServiceControllerImpl;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class ThemeColorDetailedWeather extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_CAT_BG_COLORS =
            "theme_color_detailed_weather_cat_background_colors";
    private static final String PREF_CAT_TEXT_COLORS =
            "theme_color_detailed_weather_cat_text_colors";
    private static final String PREF_CAT_ICON_COLORS =
            "theme_color_detailed_weather_cat_icon_colors";
    private static final String PREF_CAT_RIPPLE_COLORS =
            "theme_color_detailed_weather_cat_ripple_colors";
    private static final String PREF_THEME =
            "theme_color_detailed_weather_theme";
    private static final String PREF_CUSTOMIZE_COLORS =
            "theme_color_detailed_weather_customize_colors";
    private static final String PREF_STATUS_BAR_BG_COLOR =
            "theme_color_detailed_weather_status_bar_bg_color";
    private static final String PREF_ACTION_BAR_BG_COLOR =
            "theme_color_detailed_weather_action_bar_bg_color";
    private static final String PREF_CONTENT_BG_COLOR =
            "theme_color_detailed_weather_content_bg_color";
    private static final String PREF_CARDS_BG_COLOR =
            "theme_color_detailed_weather_cards_bg_color";
    private static final String PREF_ACTION_BAR_TEXT_COLOR =
            "theme_color_detailed_weather_action_bar_text_color";
    private static final String PREF_CARDS_TEXT_COLOR =
            "theme_color_detailed_weather_cards_text_color";
    private static final String PREF_ACTION_BAR_ICON_COLOR =
            "theme_color_detailed_weather_action_bar_icon_color";
    private static final String PREF_CARDS_ICON_COLOR =
            "theme_color_detailed_weather_cards_icon_color";
    private static final String PREF_ACTION_BAR_RIPPLE_COLOR =
            "theme_color_detailed_weather_action_bar_ripple_color";
    private static final String PREF_CARDS_RIPPLE_COLOR =
            "theme_color_detailed_weather_cards_ripple_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int MENU_SHOW  = 2;
    private static final int DLG_RESET  = 0;

    private ListPreference mTheme;
    private SwitchPreference mCustomizeColors;
    private ColorPickerPreference mStatusBarBgColor;
    private ColorPickerPreference mActionBarBgColor;
    private ColorPickerPreference mContentBgColor;
    private ColorPickerPreference mCardsBgColor;
    private ColorPickerPreference mActionBarTextColor;
    private ColorPickerPreference mCardsTextColor;
    private ColorPickerPreference mActionBarIconColor;
    private ColorPickerPreference mCardsIconColor;
    private ColorPickerPreference mActionBarRippleColor;
    private ColorPickerPreference mCardsRippleColor;

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

        addPreferencesFromResource(R.xml.theme_color_detailed_weather);
        mResolver = getContentResolver();

        PreferenceCategory catBgColors =
                (PreferenceCategory) findPreference(PREF_CAT_BG_COLORS);
        PreferenceCategory catTextColors =
                (PreferenceCategory) findPreference(PREF_CAT_TEXT_COLORS);
        PreferenceCategory catIconColors =
                (PreferenceCategory) findPreference(PREF_CAT_ICON_COLORS);
        PreferenceCategory catRippleColors =
                (PreferenceCategory) findPreference(PREF_CAT_RIPPLE_COLORS);

        final boolean customizeColors = Settings.System.getInt(mResolver,
                Settings.System.DETAILED_WEATHER_CUSTOMIZE_COLORS, 0) == 1;

        mTheme = (ListPreference) findPreference(PREF_THEME);
        final int theme = Settings.System.getInt(mResolver,
                Settings.System.DETAILED_WEATHER_THEME, 0);
        mTheme.setValue(String.valueOf(theme));
        mTheme.setSummary(mTheme.getEntry());
        mTheme.setOnPreferenceChangeListener(this);

        mCustomizeColors = (SwitchPreference) findPreference(PREF_CUSTOMIZE_COLORS);
        mCustomizeColors.setChecked(customizeColors);
        mCustomizeColors.setOnPreferenceChangeListener(this);

        if (customizeColors) {
            int intColor;
            String hexColor;
            int defaultColor;

            mStatusBarBgColor =
                    (ColorPickerPreference) findPreference(PREF_STATUS_BAR_BG_COLOR);
            intColor = DetailedWeatherHelper.getStatusBarBgColor(getActivity());
            mStatusBarBgColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mStatusBarBgColor.setSummary(hexColor);
            mStatusBarBgColor.setResetColor(DetailedWeatherHelper.MATERIAL_BLUE_700);
            mStatusBarBgColor.setResetColorTitle(R.string.reset_theme_default_title);
            mStatusBarBgColor.setOnPreferenceChangeListener(this);

            mActionBarBgColor =
                    (ColorPickerPreference) findPreference(PREF_ACTION_BAR_BG_COLOR);
            intColor = DetailedWeatherHelper.getActionBarBgColor(getActivity());
            mActionBarBgColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mActionBarBgColor.setSummary(hexColor);
            mActionBarBgColor.setResetColor(DetailedWeatherHelper.MATERIAL_BLUE_500);
            mActionBarBgColor.setResetColorTitle(R.string.reset_theme_default_title);
            mActionBarBgColor.setOnPreferenceChangeListener(this);

            mContentBgColor =
                    (ColorPickerPreference) findPreference(PREF_CONTENT_BG_COLOR);
            intColor = DetailedWeatherHelper.getContentBgColor(getActivity());
            mContentBgColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mContentBgColor.setSummary(hexColor);
            defaultColor = getThemeDefaultColor(DetailedWeatherHelper.INDEX_CONTENT_BG_COLOR);
            mContentBgColor.setResetColor(defaultColor);
            mContentBgColor.setResetColorTitle(R.string.reset_theme_default_title);
            mContentBgColor.setOnPreferenceChangeListener(this);

            mCardsBgColor =
                    (ColorPickerPreference) findPreference(PREF_CARDS_BG_COLOR);
            intColor = DetailedWeatherHelper.getCardsBgColor(getActivity());
            mCardsBgColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardsBgColor.setSummary(hexColor);
            defaultColor = getThemeDefaultColor(DetailedWeatherHelper.INDEX_CARDS_BG_COLOR);
            mCardsBgColor.setResetColor(defaultColor);
            mCardsBgColor.setResetColorTitle(R.string.reset_theme_default_title);
            mCardsBgColor.setOnPreferenceChangeListener(this);

            mActionBarTextColor =
                    (ColorPickerPreference) findPreference(PREF_ACTION_BAR_TEXT_COLOR);
            intColor = DetailedWeatherHelper.getActionBarTextColor(getActivity(), true);
            mActionBarTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mActionBarTextColor.setSummary(hexColor);
            mActionBarTextColor.setResetColor(DetailedWeatherHelper.WHITE);
            mActionBarTextColor.setResetColorTitle(R.string.reset_theme_default_title);
            mActionBarTextColor.setOnPreferenceChangeListener(this);

            mCardsTextColor =
                    (ColorPickerPreference) findPreference(PREF_CARDS_TEXT_COLOR);
            intColor = DetailedWeatherHelper.getCardsTextColor(getActivity(), true);
            mCardsTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardsTextColor.setSummary(hexColor);
            defaultColor = getThemeDefaultColor(DetailedWeatherHelper.INDEX_CARDS_TEXT_COLOR);
            mCardsTextColor.setResetColor(defaultColor);
            mCardsTextColor.setResetColorTitle(R.string.reset_theme_default_title);
            mCardsTextColor.setOnPreferenceChangeListener(this);

            mActionBarIconColor =
                    (ColorPickerPreference) findPreference(PREF_ACTION_BAR_ICON_COLOR);
            intColor = DetailedWeatherHelper.getActionBarIconColor(getActivity());
            mActionBarIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mActionBarIconColor.setSummary(hexColor);
            mActionBarIconColor.setResetColor(DetailedWeatherHelper.WHITE);
            mActionBarIconColor.setResetColorTitle(R.string.reset_theme_default_title);
            mActionBarIconColor.setOnPreferenceChangeListener(this);

            mCardsIconColor =
                    (ColorPickerPreference) findPreference(PREF_CARDS_ICON_COLOR);
            intColor = DetailedWeatherHelper.getCardsIconColor(getActivity());
            mCardsIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardsIconColor.setSummary(hexColor);
            defaultColor = getThemeDefaultColor(DetailedWeatherHelper.INDEX_CARDS_ICON_COLOR);
            mCardsIconColor.setResetColor(defaultColor);
            mCardsIconColor.setResetColorTitle(R.string.reset_theme_default_title);
            mCardsIconColor.setOnPreferenceChangeListener(this);

            mActionBarRippleColor =
                    (ColorPickerPreference) findPreference(PREF_ACTION_BAR_RIPPLE_COLOR);
            intColor = DetailedWeatherHelper.getActionBarRippleColor(getActivity());
            mActionBarRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mActionBarRippleColor.setSummary(hexColor);
            defaultColor = getThemeDefaultColor(DetailedWeatherHelper.INDEX_AB_RIPPLE_COLOR);
            mActionBarRippleColor.setResetColor(defaultColor);
            mActionBarRippleColor.setResetColorTitle(R.string.reset_theme_default_title);
            mActionBarRippleColor.setOnPreferenceChangeListener(this);

            mCardsRippleColor =
                    (ColorPickerPreference) findPreference(PREF_CARDS_RIPPLE_COLOR);
            intColor = DetailedWeatherHelper.getCardsRippleColor(getActivity());
            mCardsRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mCardsRippleColor.setSummary(hexColor);
            defaultColor = getThemeDefaultColor(DetailedWeatherHelper.INDEX_CARDS_RIPPLE_COLOR);
            mCardsRippleColor.setResetColor(defaultColor);
            mCardsRippleColor.setResetColorTitle(R.string.reset_theme_default_title);
            mCardsRippleColor.setOnPreferenceChangeListener(this);
        } else {
            catBgColors.removePreference(findPreference(PREF_STATUS_BAR_BG_COLOR));
            catBgColors.removePreference(findPreference(PREF_ACTION_BAR_BG_COLOR));
            catBgColors.removePreference(findPreference(PREF_CONTENT_BG_COLOR));
            catBgColors.removePreference(findPreference(PREF_CARDS_BG_COLOR));
            catTextColors.removePreference(findPreference(PREF_ACTION_BAR_TEXT_COLOR));
            catTextColors.removePreference(findPreference(PREF_CARDS_TEXT_COLOR));
            catIconColors.removePreference(findPreference(PREF_ACTION_BAR_ICON_COLOR));
            catIconColors.removePreference(findPreference(PREF_CARDS_ICON_COLOR));
            catRippleColors.removePreference(findPreference(PREF_ACTION_BAR_RIPPLE_COLOR));
            catRippleColors.removePreference(findPreference(PREF_CARDS_RIPPLE_COLOR));
            removePreference(PREF_CAT_BG_COLORS);
            removePreference(PREF_CAT_TEXT_COLORS);
            removePreference(PREF_CAT_ICON_COLORS);
            removePreference(PREF_CAT_RIPPLE_COLORS);
        }
        setHasOptionsMenu(true);
    }

    private int getThemeDefaultColor(int index) {
        return DetailedWeatherHelper.DEFAULT_COLORS[
                DetailedWeatherHelper.getTheme(getActivity())][index];
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, MENU_SHOW, 1, R.string.action_show_detailed_weather_view_title)
                .setIcon(R.drawable.ic_action_show_detailed_weather_view)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
            case MENU_SHOW:
                Bundle b = new Bundle();
                b.putInt(DetailedWeatherHelper.DAY_INDEX, 0);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(WeatherServiceControllerImpl.COMPONENT_DETAILED_WEATHER);
                intent.putExtras(b);
                startActivity(intent);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intHex;
        String hex;

        if (preference == mTheme) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mTheme.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_THEME, intValue);
            mTheme.setSummary(mTheme.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mCustomizeColors) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CUSTOMIZE_COLORS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStatusBarBgColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_STATUS_BAR_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mActionBarBgColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACTION_BAR_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mContentBgColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CONTENT_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardsBgColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CARDS_BG_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mActionBarTextColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACTION_BAR_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardsTextColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CARDS_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mActionBarIconColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACTION_BAR_ICON_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardsIconColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CARDS_ICON_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mActionBarRippleColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_ACTION_BAR_RIPPLE_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mCardsRippleColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.DETAILED_WEATHER_CARDS_RIPPLE_COLOR, intHex);
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

        ThemeColorDetailedWeather getOwner() {
            return (ThemeColorDetailedWeather) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_theme_default_colors_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_STATUS_BAR_BG_COLOR,
                                    DetailedWeatherHelper.MATERIAL_BLUE_700);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACTION_BAR_BG_COLOR,
                                    DetailedWeatherHelper.MATERIAL_BLUE_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CONTENT_BG_COLOR,
                                    getOwner().getThemeDefaultColor(
                                    DetailedWeatherHelper.INDEX_CONTENT_BG_COLOR));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CARDS_BG_COLOR,
                                    getOwner().getThemeDefaultColor(
                                    DetailedWeatherHelper.INDEX_CARDS_BG_COLOR));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACTION_BAR_TEXT_COLOR,
                                    DetailedWeatherHelper.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CARDS_TEXT_COLOR,
                                    getOwner().getThemeDefaultColor(
                                    DetailedWeatherHelper.INDEX_CARDS_TEXT_COLOR));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACTION_BAR_ICON_COLOR,
                                    DetailedWeatherHelper.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CARDS_ICON_COLOR,
                                    getOwner().getThemeDefaultColor(
                                    DetailedWeatherHelper.INDEX_CARDS_ICON_COLOR));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_ACTION_BAR_RIPPLE_COLOR,
                                    getOwner().getThemeDefaultColor(
                                    DetailedWeatherHelper.INDEX_AB_RIPPLE_COLOR));
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DETAILED_WEATHER_CARDS_RIPPLE_COLOR,
                                    getOwner().getThemeDefaultColor(
                                    DetailedWeatherHelper.INDEX_CARDS_RIPPLE_COLOR));
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
