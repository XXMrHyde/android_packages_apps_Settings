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
import com.android.settings.InstrumentedFragment;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationBarExtraButtonsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_CAT_MENU_BUTTON =
            "navigation_bar_cat_menu_button";
    private static final String PREF_SHOW_IME_ARROWS =
            "navigation_bar_show_ime_arrows";
    private static final String PREF_MENU_BUTTON_VISIBILITY =
            "navigation_bar_menu_button_visibility";
    private static final String PREF_MENU_BUTTON_POSITION =
            "navigation_bar_menu_button_position";
    private static final String PREF_IME_BUTTON_POSITION =
            "navigation_bar_ime_button_position";

    private static final int HIDE_IME_ARROWS  = 0;
    private static final int SHOW_IME_ARROWS  = 1;

    private static final int MENU_BUTTON_VISIBILITY_ON_REQUEST = 0;
    private static final int MENU_BUTTON_VISIBILITY_HIDDEN     = 2;

    private static final int MENU_IME_BUTTON_POSITION_RIGHT = 0;
    private static final int MENU_IME_BUTTON_POSITION_LEFT  = 1;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private SwitchPreference mShowImeArrows;
    private ListPreference mMenuButtonVisibility;
    private SwitchPreference mMenuButtonPosition;
    private SwitchPreference mImeButtonPosition;

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

        addPreferencesFromResource(R.xml.navigation_bar_extra_buttons_settings);

        mResolver = getContentResolver();
        final boolean isPhone = DeviceUtils.isPhone(getActivity());

        int intValue;

        PreferenceCategory catMenuButton =
                (PreferenceCategory) findPreference(PREF_CAT_MENU_BUTTON);

        mShowImeArrows = (SwitchPreference) findPreference(PREF_SHOW_IME_ARROWS);
        mShowImeArrows.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, HIDE_IME_ARROWS)
                == SHOW_IME_ARROWS);
        mShowImeArrows.setOnPreferenceChangeListener(this);

        mMenuButtonVisibility =
                (ListPreference) findPreference(PREF_MENU_BUTTON_VISIBILITY);
        final int menuButtonVisibility = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_MENU_BUTTON_VISIBILITY,
                MENU_BUTTON_VISIBILITY_ON_REQUEST);
        mMenuButtonVisibility.setValue(String.valueOf(menuButtonVisibility));
        mMenuButtonVisibility.setSummary(mMenuButtonVisibility.getEntry());
        mMenuButtonVisibility.setOnPreferenceChangeListener(this);

        if (menuButtonVisibility != MENU_BUTTON_VISIBILITY_HIDDEN) {
            mMenuButtonPosition = (SwitchPreference) findPreference(PREF_MENU_BUTTON_POSITION);
            mMenuButtonPosition.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_MENU_BUTTON_POSITION,
                    MENU_IME_BUTTON_POSITION_RIGHT) == MENU_IME_BUTTON_POSITION_LEFT);
            if (!isPhone) {
                mMenuButtonPosition.setSummary(R.string.navigation_bar_menu_button_position_tablet_summary);
            }
            mMenuButtonPosition.setOnPreferenceChangeListener(this);
        } else {
            catMenuButton.removePreference(findPreference(PREF_MENU_BUTTON_POSITION));
        }

        mImeButtonPosition = (SwitchPreference) findPreference(PREF_IME_BUTTON_POSITION);
        mImeButtonPosition.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_IME_BUTTON_POSITION,
                MENU_IME_BUTTON_POSITION_RIGHT) == MENU_IME_BUTTON_POSITION_LEFT);
        if (!isPhone) {
            mImeButtonPosition.setSummary(R.string.navigation_bar_ime_button_position_tablet_summary);
        }
        mImeButtonPosition.setOnPreferenceChangeListener(this);

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

        if (preference == mShowImeArrows) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS, value ? 1 : 0);
            return true;
        } else if (preference == mMenuButtonVisibility) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mMenuButtonVisibility.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_MENU_BUTTON_VISIBILITY, intValue);
            preference.setSummary(mMenuButtonVisibility.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mMenuButtonPosition) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_MENU_BUTTON_POSITION,
                    value ? 1 : 0);
            return true;
        } else if (preference == mImeButtonPosition) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_IME_BUTTON_POSITION,
                    value ? 1 : 0);
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

        NavigationBarExtraButtonsSettings getOwner() {
            return (NavigationBarExtraButtonsSettings) getTargetFragment();
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
                                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS,
                                    HIDE_IME_ARROWS);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_BUTTON_VISIBILITY,
                                    MENU_BUTTON_VISIBILITY_ON_REQUEST);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_BUTTON_POSITION,
                                    MENU_IME_BUTTON_POSITION_RIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_IME_BUTTON_POSITION,
                                    MENU_IME_BUTTON_POSITION_RIGHT);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_SHOW_IME_ARROWS,
                                    SHOW_IME_ARROWS);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_BUTTON_VISIBILITY,
                                    MENU_BUTTON_VISIBILITY_ON_REQUEST);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_MENU_BUTTON_POSITION,
                                    MENU_IME_BUTTON_POSITION_RIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NAVIGATION_BAR_IME_BUTTON_POSITION,
                                    MENU_IME_BUTTON_POSITION_LEFT);
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
        return InstrumentedFragment.NAVIGATIONBAR;
    }
}
