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

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_CAT_SIZE =
            "navigation_bar_cat_size";
    private static final String PREF_CAN_MOVE =
            "navigation_bar_can_move";
    private static final String PREF_HEIGHT =
            "navigation_bar_height";
    private static final String PREF_HEIGHT_LANDSCAPE =
            "navigation_bar_height_landscape";
    private static final String PREF_WIDTH =
            "navigation_bar_width";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mCanMove;
    private ListPreference mHeight;
    private ListPreference mHeightLandscape;
    private ListPreference mWidth;

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

        addPreferencesFromResource(R.xml.navigation_bar_settings);

        mResolver = getActivity().getContentResolver();
        int intValue;
        int intColor;
        String hexColor;

        boolean navigationBarCanMove = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_CAN_MOVE,
                DeviceUtils.isPhone(getActivity()) ? 1 : 0) == 0;

        mCanMove = (SwitchPreference) findPreference(PREF_CAN_MOVE);
        mCanMove.setChecked(navigationBarCanMove);
        mCanMove.setOnPreferenceChangeListener(this);

        mHeight =
                (ListPreference) findPreference(PREF_HEIGHT);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_HEIGHT, 48);
        mHeight.setValue(String.valueOf(intValue));
        mHeight.setSummary(mHeight.getEntry());
        mHeight.setOnPreferenceChangeListener(this);

        PreferenceCategory catSize =
                (PreferenceCategory) findPreference(PREF_CAT_SIZE);
        if (navigationBarCanMove) {
            mHeightLandscape =
                    (ListPreference) findPreference(PREF_HEIGHT_LANDSCAPE);
            intValue = Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, 48);
            mHeightLandscape.setValue(String.valueOf(intValue));
            mHeightLandscape.setSummary(mHeightLandscape.getEntry());
            mHeightLandscape.setOnPreferenceChangeListener(this);
            catSize.removePreference(findPreference(PREF_WIDTH));
        } else {
            mWidth =
                    (ListPreference) findPreference(PREF_WIDTH);
            intValue = Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_WIDTH, 42);
            mWidth.setValue(String.valueOf(intValue));
            mWidth.setSummary(mWidth.getEntry());
            mWidth.setOnPreferenceChangeListener(this);
            catSize.removePreference(findPreference(PREF_HEIGHT_LANDSCAPE));
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

        if (preference == mCanMove) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_CAN_MOVE, value ? 0 : 1);
            refreshSettings();
            return true;
        } else if (preference == mHeight) {
            intValue = Integer.valueOf((String) newValue);
            index = mHeight.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_HEIGHT, intValue);
            preference.setSummary(mHeight.getEntries()[index]);
            return true;
        } else if (preference == mHeightLandscape) {
            intValue = Integer.valueOf((String) newValue);
            index = mHeightLandscape.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, intValue);
            preference.setSummary(mHeightLandscape.getEntries()[index]);
            return true;
        } else if (preference == mWidth) {
            intValue = Integer.valueOf((String) newValue);
            index = mWidth.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_WIDTH, intValue);
            preference.setSummary(mWidth.getEntries()[index]);
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

        NavigationBarSettings getOwner() {
            return (NavigationBarSettings) getTargetFragment();
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
                                    Settings.System.NAVIGATION_BAR_CAN_MOVE,
                                    DeviceUtils.isPhone(getOwner().getActivity()) ? 1 : 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_HEIGHT, 48);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, 48);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_WIDTH, 42);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_CAN_MOVE,
                                    DeviceUtils.isPhone(getOwner().getActivity()) ? 1 : 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_HEIGHT, 48);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, 48);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_WIDTH, 42);
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
