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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class NavigationBarButtonAdvanced extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_CAT_MENU_BUTTON =
            "navigation_bar_cat_menu_button";
    private static final String PREF_CAT_COLORS =
            "navigation_bar_cat_colors";
    private static final String PREF_SHOW_IME_ARROWS =
            "navigation_bar_show_ime_arrows";
    private static final String PREF_MENU_VISIBILITY =
            "navigation_bar_menu_visibility";
    private static final String PREF_MENU_LOCATION =
            "navigation_bar_menu_location";
    private static final String PREF_ICON_COLOR_MODE =
            "navigation_bar_icon_color_mode";
    private static final String PREF_RIPPLE_COLOR_MODE =
            "navigation_bar_button_ripple_color_mode";
    private static final String PREF_ICON_COLOR =
            "navigation_bar_icon_color";
    private static final String PREF_RIPPLE_COLOR =
            "navigation_bar_button_ripple_color";

    private static final int WHITE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mShowImeArrows;
    private ListPreference mMenuVisibility;
    private ListPreference mMenuLocation;
    private ListPreference mIconColorMode;
    private ListPreference mRippleColorMode;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mRippleColor;

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

        addPreferencesFromResource(R.xml.navigation_bar_button_advanced);

        mResolver = getActivity().getContentResolver();
        int intValue;
        int intColor;
        String hexColor;

        boolean isMenuButtonVisible = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, 0) != 2;
        boolean colorizeIcon = Settings.System.getInt(mResolver,
               Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, 0) != 0;
        boolean colorizeRipple = Settings.System.getInt(mResolver,
               Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, 2) == 1;

        PreferenceCategory catMenuButton =
                (PreferenceCategory) findPreference(PREF_CAT_MENU_BUTTON);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        mShowImeArrows = (SwitchPreference) findPreference(PREF_SHOW_IME_ARROWS);
        mShowImeArrows.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, 0) == 1);
        mShowImeArrows.setOnPreferenceChangeListener(this);

        mMenuVisibility =
                (ListPreference) findPreference(PREF_MENU_VISIBILITY);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, 0);
        mMenuVisibility.setValue(String.valueOf(intValue));
        mMenuVisibility.setSummary(mMenuVisibility.getEntry());
        mMenuVisibility.setOnPreferenceChangeListener(this);

        if (isMenuButtonVisible) {
            mMenuLocation =
                    (ListPreference) findPreference(PREF_MENU_LOCATION);
            intValue = Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_MENU_LOCATION, 0);
            mMenuLocation.setValue(String.valueOf(intValue));
            mMenuLocation.setSummary(mMenuLocation.getEntry());
            mMenuLocation.setOnPreferenceChangeListener(this);
        } else {
            catMenuButton.removePreference(findPreference(PREF_MENU_LOCATION));
        }

        mIconColorMode =
                (ListPreference) findPreference(PREF_ICON_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, 0);
        mIconColorMode.setValue(String.valueOf(intValue));
        mIconColorMode.setSummary(mIconColorMode.getEntry());
        mIconColorMode.setOnPreferenceChangeListener(this);

        mRippleColorMode =
                (ListPreference) findPreference(PREF_RIPPLE_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, 2);
        mRippleColorMode.setValue(String.valueOf(intValue));
        mRippleColorMode.setSummary(mRippleColorMode.getEntry());
        mRippleColorMode.setOnPreferenceChangeListener(this);

        if (colorizeIcon) {
            mIconColor =
                    (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_ICON_COLOR, WHITE); 
            mIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mIconColor.setSummary(hexColor);
            mIconColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mIconColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_ICON_COLOR));
        }

        if (colorizeRipple) {
            mRippleColor =
                    (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR, WHITE); 
            mRippleColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRippleColor.setSummary(hexColor);
            mRippleColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mRippleColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_RIPPLE_COLOR));
        }

        if (!colorizeIcon && !colorizeRipple) {
            removePreference(PREF_CAT_COLORS);
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
        boolean value;
        int intValue;
        int index;
        String hex;
        int intHex;

        if (preference == mShowImeArrows) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mMenuVisibility) {
            intValue = Integer.valueOf((String) newValue);
            index = mMenuVisibility.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, intValue);
            preference.setSummary(mMenuVisibility.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mMenuLocation) {
            intValue = Integer.valueOf((String) newValue);
            index = mMenuLocation.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_LOCATION, intValue);
            preference.setSummary(mMenuLocation.getEntries()[index]);
            return true;
        } else if (preference == mIconColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mIconColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, intValue);
            preference.setSummary(mIconColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mRippleColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mIconColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, intValue);
            preference.setSummary(mRippleColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR, intHex);
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

        NavigationBarButtonAdvanced getOwner() {
            return (NavigationBarButtonAdvanced) getTargetFragment();
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
                                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_LOCATION, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_VISIBILITY, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_LOCATION, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR_MODE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_BUTTON_RIPPLE_COLOR,
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
