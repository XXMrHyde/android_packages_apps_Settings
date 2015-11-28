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

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.Locale;

public class StatusBarExpandedSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_QUICK_PULLDOWN =
            "status_bar_expanded_quick_pulldown";

    private ListPreference mQuickPulldown;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_expanded_settings);
        mResolver = getContentResolver();

        mQuickPulldown =
                (ListPreference) findPreference(PREF_QUICK_PULLDOWN);
        int quickPulldown = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_QUICK_PULLDOWN, 0);
        mQuickPulldown.setValue(String.valueOf(quickPulldown));
        updateQuickPulldownSummary(quickPulldown);
        mQuickPulldown.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQuickPulldown) {
            int intValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_QUICK_PULLDOWN,
                    intValue);
            updateQuickPulldownSummary(intValue);
            return true;
        }
        return false;
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
