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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusNetworkNamesSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_CARRIER =
            "network_names_cat_carrier";
    private static final String PREF_CAT_NOTIFICATION_ICONS =
            "network_names_cat_notification_icons";
    private static final String PREF_SHOW_CARRIER =
            "network_names_show_carrier";
    private static final String PREF_SHOW_CARRIER_ON_LOCK_SCREEN =
            "network_names_show_carrier_on_lock_screen";
    private static final String PREF_SHOW_WIFI =
            "network_names_show_wifi";
    private static final String PREF_SHOW_WIFI_ON_LOCK_SCREEN =
            "network_names_show_wifi_on_lock_screen";
    private static final String PREF_HIDE =
            "network_names_hide";
    private static final String PREF_NUMBER_OF_NOTIFICATION_ICONS =
            "network_names_number_of_notification_icons";

    private SwitchPreference mShowCarrier;
    private SwitchPreference mShowCarrierOnLockScreen;
    private SwitchPreference mShowWifi;
    private SwitchPreference mShowWifiOnLockScreen;
    private SwitchPreference mHide;
    private ListPreference mNumberOfNotificationIcons;

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

        addPreferencesFromResource(R.xml.status_bar_network_names_settings);
        mResolver = getContentResolver();

        boolean deviceSupportsMobileData = DeviceUtils.deviceSupportsMobileData(getActivity());
        boolean showCarrier;
        if (deviceSupportsMobileData) {
            showCarrier = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NETWORK_NAMES_SHOW_CARRIER, 0) == 1;
        } else {
            showCarrier = false;
        }
        final boolean showWifi = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NETWORK_NAMES_SHOW_WIFI, 0) == 1;
        final boolean showCarrierOrWifi = showCarrier || showWifi;

        PreferenceCategory catCarrier =
                (PreferenceCategory) findPreference(PREF_CAT_CARRIER);
        PreferenceCategory catNotificationIcons =
                (PreferenceCategory) findPreference(PREF_CAT_NOTIFICATION_ICONS);

        if (deviceSupportsMobileData) {
            mShowCarrier = (SwitchPreference) findPreference(PREF_SHOW_CARRIER);
            mShowCarrier.setChecked(showCarrier);
            mShowCarrier.setOnPreferenceChangeListener(this);

            mShowCarrierOnLockScreen =
                    (SwitchPreference) findPreference(PREF_SHOW_CARRIER_ON_LOCK_SCREEN);
            mShowCarrierOnLockScreen.setChecked(Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_NETWORK_NAMES_SHOW_CARRIER_ON_LOCK_SCREEN, 1) == 1);
            mShowCarrierOnLockScreen.setOnPreferenceChangeListener(this);
        } else {
            catCarrier.removePreference(findPreference(PREF_SHOW_CARRIER));
            catCarrier.removePreference(findPreference(PREF_SHOW_CARRIER_ON_LOCK_SCREEN));
            removePreference(PREF_CAT_CARRIER);
        }

        mShowWifi = (SwitchPreference) findPreference(PREF_SHOW_WIFI);
        mShowWifi.setChecked(showWifi);
        mShowWifi.setOnPreferenceChangeListener(this);

        mShowWifiOnLockScreen = (SwitchPreference) findPreference(PREF_SHOW_WIFI_ON_LOCK_SCREEN);
        mShowWifiOnLockScreen.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NETWORK_NAMES_SHOW_WIFI_ON_LOCK_SCREEN, 0) == 1);
        mShowWifiOnLockScreen.setOnPreferenceChangeListener(this);

        if (showCarrierOrWifi) {
            boolean hide = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_NETWORK_NAMES_HIDE, 1) == 1;
            mHide = (SwitchPreference) findPreference(PREF_HIDE);
            mHide.setChecked(hide);
            if (!deviceSupportsMobileData) {
                mHide.setSummary(
                        R.string.network_names_hide_wifi_only_summary);
            }
            mHide.setOnPreferenceChangeListener(this);
            if (hide) {
                mNumberOfNotificationIcons =
                        (ListPreference) findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS);
                int numberOfNotificationIcons = Settings.System.getInt(mResolver,
                       Settings.System.STATUS_BAR_NETWORK_NAMES_NUMBER_OF_NOTIFICATION_ICONS, 1);
                mNumberOfNotificationIcons.setValue(String.valueOf(numberOfNotificationIcons));
                mNumberOfNotificationIcons.setSummary(mNumberOfNotificationIcons.getEntry());
                mNumberOfNotificationIcons.setOnPreferenceChangeListener(this);
            } else {
                catNotificationIcons.removePreference(findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS));
            }
        } else {
            catNotificationIcons.removePreference(findPreference(PREF_HIDE));
            catNotificationIcons.removePreference(findPreference(PREF_NUMBER_OF_NOTIFICATION_ICONS));
            removePreference(PREF_CAT_NOTIFICATION_ICONS);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mShowCarrier) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_NAMES_SHOW_CARRIER, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowCarrierOnLockScreen) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_NAMES_SHOW_CARRIER_ON_LOCK_SCREEN,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowWifi) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_NAMES_SHOW_WIFI, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowWifiOnLockScreen) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_NAMES_SHOW_WIFI_ON_LOCK_SCREEN,
                    value ? 1 : 0);
            return true;
        } else if (preference == mHide) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_NAMES_HIDE, value ? 1 : 0);
            return true;
        } else if (preference == mNumberOfNotificationIcons) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mNumberOfNotificationIcons.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_NAMES_NUMBER_OF_NOTIFICATION_ICONS,
                    intValue);
            preference.setSummary(mNumberOfNotificationIcons.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR;
    }
}
