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
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Locale;

public class StatusBarExpandedSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_SMART_QUICK_PULLDOWN =
            "status_bar_expanded_cat_smart_quick_pulldown";
    private static final String PREF_SMART_QUICK_PULLDOWN_TYPE =
            "smart_quick_pulldown_type";
    private static final String PREF_SMART_QUICK_PULLDOWN_AREA =
            "smart_quick_pulldown_area";

    private ListPreference mSmartQuickPulldownType;
    private ListPreference mSmartQuickPulldownArea;

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

        final int smartQuickPulldownType = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_SMART_QUICK_PULLDOWN_TYPE, 4);

        mSmartQuickPulldownType =
                (ListPreference) findPreference(PREF_SMART_QUICK_PULLDOWN_TYPE);
        mSmartQuickPulldownType.setValue(String.valueOf(smartQuickPulldownType));
        updateSmartQuickPulldownTypeSummary(smartQuickPulldownType);
        mSmartQuickPulldownType.setOnPreferenceChangeListener(this);

        PreferenceCategory catSmartQuickPulldown =
                (PreferenceCategory) findPreference(PREF_CAT_SMART_QUICK_PULLDOWN);
        if (smartQuickPulldownType != 4) {
            mSmartQuickPulldownArea =
                    (ListPreference) findPreference(PREF_SMART_QUICK_PULLDOWN_AREA);
            int smartQuickPulldownArea = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_EXPANDED_SMART_QUICK_PULLDOWN_AREA, 3);
            mSmartQuickPulldownArea.setValue(String.valueOf(smartQuickPulldownArea));
            if (smartQuickPulldownArea == 1 || smartQuickPulldownArea == 3) {
                mSmartQuickPulldownArea.setSummary(mSmartQuickPulldownArea.getEntry());
            } else {
                updateSmartQuickPulldownAreaSummary(smartQuickPulldownArea);
            }
            mSmartQuickPulldownArea.setOnPreferenceChangeListener(this);
        } else {
            catSmartQuickPulldown.removePreference(findPreference(PREF_SMART_QUICK_PULLDOWN_AREA));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        if (preference == mSmartQuickPulldownType) {
            intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SMART_QUICK_PULLDOWN_TYPE,
                    intValue);
            updateSmartQuickPulldownTypeSummary(intValue);
            refreshSettings();
            return true;
        } else if (preference == mSmartQuickPulldownArea) {
            intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_SMART_QUICK_PULLDOWN_AREA,
                    intValue);
            if (intValue == 1 || intValue == 3) {
                int index = mSmartQuickPulldownArea.findIndexOfValue((String) newValue);
                mSmartQuickPulldownArea.setSummary(mSmartQuickPulldownArea.getEntries()[index]);
            } else {
                updateSmartQuickPulldownAreaSummary(intValue);
            }
            return true;
        }
        return false;
    }

    private void updateSmartQuickPulldownTypeSummary(int value) {
        Resources res = getResources();
        int summaryResId = 0;

        if (value == 0) {
            summaryResId = R.string.smart_quick_pulldown_type_always_summary;
        } else if (value == 1) {
            summaryResId = R.string.smart_quick_pulldown_type_dismissable_summary;
        } else if (value == 2) {
            summaryResId = R.string.smart_quick_pulldown_type_persistent_summary;
        } else if (value == 3) {
            summaryResId = R.string.smart_quick_pulldown_type_no_notifications_summary;
        } else if (value == 4) {
            summaryResId = R.string.smart_quick_pulldown_type_never_summary;
        }
        if (summaryResId > 0) {
            mSmartQuickPulldownType.setSummary(res.getString(summaryResId));
        }
    }

    private void updateSmartQuickPulldownAreaSummary(int value) {
        Resources res = getResources();

        Locale l = Locale.getDefault();
        boolean isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL;
        int resId = value == 0
                ? (isRtl ? R.string.position_right_title : R.string.position_left_title)
                : (isRtl ? R.string.position_left_title : R.string.position_right_title);
        mSmartQuickPulldownArea.setSummary(res.getString(resId));
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR_EXPANDED;
    }
}
