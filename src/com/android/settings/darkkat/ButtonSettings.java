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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class ButtonSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_VOLBTN_WAKE =
            "button_volume_button_wake";
    private static final String PREF_VOLBTN_SWAP =
            "button_swap_volume_buttons";

    private CheckBoxPreference mVolBtnWake;
    private CheckBoxPreference mVolBtnSwap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.button_settings);

        mVolBtnWake = (CheckBoxPreference) findPreference(PREF_VOLBTN_WAKE);
        mVolBtnWake.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);
        mVolBtnWake.setOnPreferenceChangeListener(this);

        mVolBtnSwap = (CheckBoxPreference) findPreference(PREF_VOLBTN_SWAP);
        mVolBtnSwap.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SWAP_VOLUME_BUTTONS_ON_ROTATION, 0) == 1);
        mVolBtnSwap.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mVolBtnWake) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_WAKE_SCREEN,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVolBtnSwap) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SWAP_VOLUME_BUTTONS_ON_ROTATION,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
