/*
 * Copyright (C) 2016 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.StatusBarExpandedColorHelper;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class ThemeColorNotifications extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_NO_NOTIFICATIONS_TEXT_COLOR = "no_notifications_text_color";
    private static final String PREF_DISMISS_ALL_ICON_COLOR      = "dismiss_all_icon_color";
    private static final String PREF_DISMISS_ALL_RIPPLE_COLOR    = "dismiss_all_ripple_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mNoNotificationsTextColor;
    private ColorPickerPreference mDismissAllIconColor;
    private ColorPickerPreference mDismissAllRippleColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.theme_color_notifications);
        mResolver = getContentResolver();

        int intColor;
        String hexColor;

        mNoNotificationsTextColor =
                (ColorPickerPreference) findPreference(PREF_NO_NOTIFICATIONS_TEXT_COLOR);
        intColor = StatusBarExpandedColorHelper.getNoNotificationsTextColor(getActivity());
        mNoNotificationsTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNoNotificationsTextColor.setSummary(hexColor);
        mNoNotificationsTextColor.setResetColors(StatusBarExpandedColorHelper.WHITE,
                StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
        mNoNotificationsTextColor.setOnPreferenceChangeListener(this);

        mDismissAllIconColor =
                (ColorPickerPreference) findPreference(PREF_DISMISS_ALL_ICON_COLOR);
        intColor = StatusBarExpandedColorHelper.getNotificationsDismissAllIconColor(getActivity());
        mDismissAllIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mDismissAllIconColor.setSummary(hexColor);
        mDismissAllIconColor.setResetColors(StatusBarExpandedColorHelper.WHITE,
                StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
        mDismissAllIconColor.setOnPreferenceChangeListener(this);

        mDismissAllRippleColor =
                (ColorPickerPreference) findPreference(PREF_DISMISS_ALL_RIPPLE_COLOR);
        intColor = StatusBarExpandedColorHelper.getNotificationsDismissAllRippleColor(getActivity());
        mDismissAllRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mDismissAllRippleColor.setSummary(hexColor);
        mDismissAllRippleColor.setResetColors(StatusBarExpandedColorHelper.WHITE,
                StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
        mDismissAllRippleColor.setOnPreferenceChangeListener(this);

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

        if (preference == mNoNotificationsTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.NO_NOTIFICATIONS_TEXT_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mDismissAllIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.DISMISS_ALL_NOTIFICATIONS_ICON_COLOR, intHex);
            refreshSettings();
            return true;
        } else if (preference == mDismissAllRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.DISMISS_ALL_NOTIFICATIONS_RIPPLE_COLOR, intHex);
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

        ThemeColorNotifications getOwner() {
            return (ThemeColorNotifications) getTargetFragment();
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
                                    Settings.System.NO_NOTIFICATIONS_TEXT_COLOR,
                                            StatusBarExpandedColorHelper.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DISMISS_ALL_NOTIFICATIONS_ICON_COLOR,
                                            StatusBarExpandedColorHelper.WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DISMISS_ALL_NOTIFICATIONS_RIPPLE_COLOR,
                                            StatusBarExpandedColorHelper.WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.NO_NOTIFICATIONS_TEXT_COLOR,
                                            StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DISMISS_ALL_NOTIFICATIONS_ICON_COLOR,
                                            StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DISMISS_ALL_NOTIFICATIONS_RIPPLE_COLOR,
                                            StatusBarExpandedColorHelper.HOLO_BLUE_LIGHT);
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
