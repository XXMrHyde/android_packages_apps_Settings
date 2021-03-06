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
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarMultiUserSwitchSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_COLORS =
            "multi_user_switch_cat_colors";
    private static final String PREF_ICON_COLOR =
            "multi_user_switch_icon_color";
    private static final String PREF_ACTIVE_TEXT_FRAME_COLOR =
            "multi_user_switch_active_text_frame_color";
    private static final String PREF_INACTIVE_TEXT_COLOR =
            "multi_user_switch_inactive_text_color";

    private static final int WHITE             = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT   = 0xff33b5e5;
    private static final int MATERIAL_TEAL_500 = 0xff009688;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mActiveTextFrameColor;
    private ColorPickerPreference mInactiveTextColor;

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

        addPreferencesFromResource(R.xml.status_bar_multi_user_switch_settings);
        mResolver = getActivity().getContentResolver();

        final boolean hasMultiUserSwitch =
                getResources().getBoolean(R.bool.config_has_status_bar_multi_user_switch);

        int intColor;
        String hexColor;

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        if (!hasMultiUserSwitch) {
            mIconColor.setTitle(getResources().getString(
                    R.string.multi_user_switch_guest_icon_color_title));
        }
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ICON_COLOR, WHITE);
        mIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setResetColors(WHITE, HOLO_BLUE_LIGHT);
        mIconColor.setOnPreferenceChangeListener(this);

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        if (hasMultiUserSwitch) {
            mActiveTextFrameColor =
                    (ColorPickerPreference) findPreference(PREF_ACTIVE_TEXT_FRAME_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ACTIVE_TEXT_COLOR,
                    MATERIAL_TEAL_500);
            mActiveTextFrameColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mActiveTextFrameColor.setSummary(hexColor);
            mActiveTextFrameColor.setResetColors(MATERIAL_TEAL_500, MATERIAL_TEAL_500);
            mActiveTextFrameColor.setOnPreferenceChangeListener(this);

            mInactiveTextColor =
                    (ColorPickerPreference) findPreference(PREF_INACTIVE_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_INACTIVE_TEXT_COLOR,
                    WHITE);
            mInactiveTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mInactiveTextColor.setSummary(hexColor);
            mInactiveTextColor.setResetColors(WHITE, HOLO_BLUE_LIGHT);
            mInactiveTextColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(findPreference(PREF_ACTIVE_TEXT_FRAME_COLOR));
            catColors.removePreference(findPreference(PREF_INACTIVE_TEXT_COLOR));
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
        String hex;
        int intHex;

        if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mActiveTextFrameColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ACTIVE_TEXT_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mInactiveTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_INACTIVE_TEXT_COLOR,
                    intHex);
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

        StatusBarMultiUserSwitchSettings getOwner() {
            return (StatusBarMultiUserSwitchSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_colors_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ACTIVE_TEXT_COLOR,
                                    MATERIAL_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_INACTIVE_TEXT_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_ACTIVE_TEXT_COLOR,
                                    MATERIAL_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_MULTI_USER_SWITCH_INACTIVE_TEXT_COLOR,
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

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.STATUSBAR;
    }
}
