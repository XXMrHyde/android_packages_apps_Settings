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

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarNetworkTrafficSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_CAT_STYLE =
            "network_traffic_cat_style";
    private static final String PREF_CAT_VISIBILITY =
            "network_traffic_cat_visibility";
    private static final String PREF_CAT_COLORS =
            "network_traffic_cat_colors";
    private static final String PREF_SHOW =
            "network_traffic_show";
    private static final String PREF_SHOW_ON_LOCK_SCREEN =
            "network_traffic_show_on_lock_screen";
    private static final String PREF_ACTIVITY_DIRECTION =
            "network_traffic_activity_direction";
    private static final String PREF_TYPE =
            "network_traffic_type";
    private static final String PREF_BIT_BYTE =
            "network_traffic_bit_byte";
    private static final String PREF_HIDE_TRAFFIC =
            "network_traffic_hide_traffic";
    private static final String PREF_THRESHOLD_BIT =
            "network_traffic_threshold_bit";
    private static final String PREF_THRESHOLD_BYTE =
            "network_traffic_threshold_byte";
    private static final String PREF_ICON_AS_INDICATOR =
            "network_traffic_icon_as_indicator";
    private static final String PREF_TEXT_COLOR =
            "network_traffic_text_color";
    private static final String PREF_TEXT_COLOR_DARK_MODE =
            "network_traffic_text_color_dark_mode";
    private static final String PREF_ICON_COLOR =
            "network_traffic_icon_color";
    private static final String PREF_ICON_COLOR_DARK_MODE =
            "network_traffic_icon_color_dark_mode";

    private static final int DIRECTION_UP_DOWN = 2;

    private static final int TYPE_TEXT      = 0;
    private static final int TYPE_ICON      = 1;
    private static final int TYPE_TEXT_ICON = 2;

    private static final int DEFAULT_THRESHOLD = 0;

    private static final int WHITE           = 0xffffffff;
    private static final int BLACK           = 0xff000000;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mShow;
    private SwitchPreference mShowOnLockScreen;
    private ListPreference mActivityDirection;
    private ListPreference mType;
    private SwitchPreference mBitByte;
    private SwitchPreference mHideTraffic;
    private ListPreference mThresholdBit;
    private ListPreference mThresholdByte;
    private SwitchPreference mIconAsIndicator;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mTextColorDarkMode;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mIconColorDarkMode;

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

        addPreferencesFromResource(R.xml.status_bar_network_traffic_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        final boolean show = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NETWORK_TRAFFIC_SHOW, 0) == 1;
        final boolean showOnLockScreen = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NETWORK_TRAFFIC_SHOW_ON_LOCK_SCREEN, 0) == 1;
        final boolean isTrafficEnabled = show || showOnLockScreen;

        PreferenceCategory catStyle =
                (PreferenceCategory) findPreference(PREF_CAT_STYLE);
        PreferenceCategory catVisibility =
                (PreferenceCategory) findPreference(PREF_CAT_VISIBILITY);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        mShow = (SwitchPreference) findPreference(PREF_SHOW);
        mShow.setChecked(show);
        mShow.setOnPreferenceChangeListener(this);

        mShowOnLockScreen = (SwitchPreference) findPreference(PREF_SHOW_ON_LOCK_SCREEN);
        mShowOnLockScreen.setChecked(showOnLockScreen);
        mShowOnLockScreen.setOnPreferenceChangeListener(this);

        if (isTrafficEnabled) {
            final int activityDirection = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ACTIVITY_DIRECTION,
                    DIRECTION_UP_DOWN);
            final int type = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TYPE, TYPE_TEXT_ICON);
            final boolean showText  = type == TYPE_TEXT || type == TYPE_TEXT_ICON;
            final boolean showIcon = type == TYPE_ICON || type == TYPE_TEXT_ICON;
            final boolean isBit = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_NETWORK_TRAFFIC_BIT_BYTE, 0) == 1;
            final boolean hideTraffic = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_HIDE_TRAFFIC, 1) == 1;
            final int threshold = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_THRESHOLD,
                    DEFAULT_THRESHOLD);

            mActivityDirection =
                    (ListPreference) findPreference(PREF_ACTIVITY_DIRECTION);
            mActivityDirection.setValue(String.valueOf(activityDirection));
            mActivityDirection.setSummary(mActivityDirection.getEntry());
            mActivityDirection.setOnPreferenceChangeListener(this);

            mType = (ListPreference) findPreference(PREF_TYPE);
            mType.setValue(String.valueOf(type));
            mType.setSummary(mType.getEntry());
            mType.setOnPreferenceChangeListener(this);

            mHideTraffic =
                    (SwitchPreference) findPreference(PREF_HIDE_TRAFFIC);
            mHideTraffic.setChecked(hideTraffic);
            mHideTraffic.setOnPreferenceChangeListener(this);

            if (hideTraffic) {
                if (isBit) {
                    mThresholdBit = (ListPreference) findPreference(PREF_THRESHOLD_BIT);
                    mThresholdBit.setValue(String.valueOf(threshold));
                    mThresholdBit.setOnPreferenceChangeListener(this);
                    catVisibility.removePreference(findPreference(PREF_THRESHOLD_BYTE));
                } else {
                    mThresholdByte = (ListPreference) findPreference(PREF_THRESHOLD_BYTE);
                    mThresholdByte.setValue(String.valueOf(threshold));
                    mThresholdByte.setOnPreferenceChangeListener(this);
                    catVisibility.removePreference(findPreference(PREF_THRESHOLD_BIT));
                }
                updateThresholdSummary(threshold, isBit ? mThresholdBit : mThresholdByte);
                if (showIcon) {
                    mIconAsIndicator =
                            (SwitchPreference) findPreference(PREF_ICON_AS_INDICATOR);
                    mIconAsIndicator.setChecked(Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_AS_INDICATOR, 1) == 1);
                    mIconAsIndicator.setOnPreferenceChangeListener(this);
                } else {
                    catVisibility.removePreference(findPreference(PREF_ICON_AS_INDICATOR));
                }
            } else {
                catVisibility.removePreference(findPreference(PREF_THRESHOLD_BIT));
                catVisibility.removePreference(findPreference(PREF_THRESHOLD_BYTE));
                catVisibility.removePreference(findPreference(PREF_ICON_AS_INDICATOR));
            }

            if (showText) {
                mBitByte =
                        (SwitchPreference) findPreference(PREF_BIT_BYTE);
                mBitByte.setChecked(isBit);
                mBitByte.setOnPreferenceChangeListener(this);

                mTextColor =
                        (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TEXT_COLOR, WHITE);
                mTextColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mTextColor.setSummary(hexColor);
                mTextColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
                mTextColor.setOnPreferenceChangeListener(this);

                if (show) {
                    mTextColorDarkMode =
                            (ColorPickerPreference) findPreference(PREF_TEXT_COLOR_DARK_MODE);
                    intColor = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TEXT_COLOR_DARK_MODE,
                            BLACK);
                    mTextColorDarkMode.setNewPreviewColor(intColor);
                    hexColor = String.format("#%08x", (0xffffffff & intColor));
                    mTextColorDarkMode.setSummary(hexColor);
                    mTextColorDarkMode.setDefaultColors(BLACK, BLACK);
                    mTextColorDarkMode.setOnPreferenceChangeListener(this);
                } else {
                    catColors.removePreference(findPreference(PREF_TEXT_COLOR_DARK_MODE));
                }
            } else {
                catStyle.removePreference(findPreference(PREF_BIT_BYTE));
                catColors.removePreference(findPreference(PREF_TEXT_COLOR));
                catColors.removePreference(findPreference(PREF_TEXT_COLOR_DARK_MODE));
            }

            if (showIcon) {
                mIconColor =
                        (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_COLOR, WHITE);
                mIconColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mIconColor.setSummary(hexColor);
                mIconColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
                mIconColor.setOnPreferenceChangeListener(this);

                if (show) {
                    mIconColorDarkMode =
                            (ColorPickerPreference) findPreference(PREF_ICON_COLOR_DARK_MODE);
                    intColor = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_COLOR_DARK_MODE,
                            BLACK);
                    mIconColorDarkMode.setNewPreviewColor(intColor);
                    hexColor = String.format("#%08x", (0xffffffff & intColor));
                    mIconColorDarkMode.setSummary(hexColor);
                    mIconColorDarkMode.setDefaultColors(BLACK, BLACK);
                    mIconColorDarkMode.setOnPreferenceChangeListener(this);
                } else {
                    catColors.removePreference(findPreference(PREF_ICON_COLOR_DARK_MODE));
                }
            } else {
                catColors.removePreference(findPreference(PREF_ICON_COLOR));
                catColors.removePreference(findPreference(PREF_ICON_COLOR_DARK_MODE));
            }
        } else {
            catStyle.removePreference(findPreference(PREF_ACTIVITY_DIRECTION));
            catStyle.removePreference(findPreference(PREF_TYPE));
            catStyle.removePreference(findPreference(PREF_BIT_BYTE));
            catVisibility.removePreference(findPreference(PREF_HIDE_TRAFFIC));
            catVisibility.removePreference(findPreference(PREF_THRESHOLD_BIT));
            catVisibility.removePreference(findPreference(PREF_THRESHOLD_BYTE));
            catVisibility.removePreference(findPreference(PREF_ICON_AS_INDICATOR));
            catColors.removePreference(findPreference(PREF_TEXT_COLOR));
            catColors.removePreference(findPreference(PREF_TEXT_COLOR_DARK_MODE));
            catColors.removePreference(findPreference(PREF_ICON_COLOR));
            catColors.removePreference(findPreference(PREF_ICON_COLOR_DARK_MODE));
            removePreference(PREF_CAT_STYLE);
            removePreference(PREF_CAT_VISIBILITY);
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
        int intValue;
        int index;
        int intHex;
        String hex;

        if (preference == mShow) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_SHOW,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowOnLockScreen) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_SHOW_ON_LOCK_SCREEN,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mActivityDirection) {
            intValue = Integer.valueOf((String) newValue);
            index = mActivityDirection.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ACTIVITY_DIRECTION,
                    intValue);
            mActivityDirection.setSummary(mActivityDirection.getEntries()[index]);
            return true;
        } else if (preference == mType) {
            intValue = Integer.valueOf((String) newValue);
            index = mType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TYPE, intValue);
            mType.setSummary(mType.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mBitByte) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_BIT_BYTE,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mHideTraffic) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_HIDE_TRAFFIC,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mThresholdBit) {
            intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_THRESHOLD, intValue);
            refreshSettings();
            return true;
        } else if (preference == mThresholdByte) {
            intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_THRESHOLD, intValue);
            refreshSettings();
            return true;
        } else if (preference == mIconAsIndicator) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_AS_INDICATOR,
                    value ? 1 : 0);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TEXT_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference ==  mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference ==  mIconColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
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

        StatusBarNetworkTrafficSettings getOwner() {
            return (StatusBarNetworkTrafficSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_SHOW, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_SHOW_ON_LOCK_SCREEN, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ACTIVITY_DIRECTION,
                                    DIRECTION_UP_DOWN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TYPE, TYPE_TEXT_ICON);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_BIT_BYTE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_HIDE_TRAFFIC, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_THRESHOLD,
                                    DEFAULT_THRESHOLD);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_AS_INDICATOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TEXT_COLOR_DARK_MODE,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_COLOR_DARK_MODE,
                                    BLACK);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_SHOW, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_SHOW_ON_LOCK_SCREEN, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ACTIVITY_DIRECTION,
                                    DIRECTION_UP_DOWN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TYPE, TYPE_TEXT_ICON);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_BIT_BYTE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_HIDE_TRAFFIC, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_THRESHOLD, 10);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_AS_INDICATOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TEXT_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_TEXT_COLOR_DARK_MODE,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_TRAFFIC_ICON_COLOR_DARK_MODE,
                                    BLACK);
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

    private void updateThresholdSummary(int threshold, ListPreference lp) {
        if (threshold == DEFAULT_THRESHOLD) {
            lp.setSummary(getResources().getString(
                        R.string.network_traffic_threshold_no_traffic_summary));
        } else {
            lp.setSummary(getResources().getString(
                    R.string.network_traffic_threshold_summary, lp.getEntry()));
        }
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR;
    }
}
