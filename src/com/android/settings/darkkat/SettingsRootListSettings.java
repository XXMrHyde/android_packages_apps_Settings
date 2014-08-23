/* 
 * Copyright (C) 2013 DarkKat
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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class SettingsRootListSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_COLORIZE_ACCOUNT_ICONS =
            "settings_root_list_colorize_account_icons";
    private static final String PREF_CATEGORY_TEXT_COLOR =
            "settings_root_list_category_text_color";
    private static final String PREF_TITLE_TEXT_COLOR =
            "settings_root_list_title_text_color";
    private static final String PREF_ICON_COLOR =
            "settings_root_list_icon_color";

    private static final int DEFAULT_CATEGORY_TEXT_COLOR = 0xffbebebe;
    private static final int DEFAULT_TITLE_TEXT_COLOR    = 0xfff3f3f3;
    private static final int DEFAULT_ICON_COLOR          = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private CheckBoxPreference mColorizeAccountIcons;
    private ColorPickerPreference mCategoryTextColor;
    private ColorPickerPreference mTitleTextColor;
    private ColorPickerPreference mIconColor;

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
        int color;
        String hexColor;

        addPreferencesFromResource(R.xml.settings_root_list);
        mResolver = getActivity().getContentResolver();

        mColorizeAccountIcons =
                (CheckBoxPreference) findPreference(PREF_COLORIZE_ACCOUNT_ICONS);
        mColorizeAccountIcons.setChecked(Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_ROOT_LIST_COLORIZE_ACCOUNT_ICONS, 0) == 1);
        mColorizeAccountIcons.setOnPreferenceChangeListener(this);

        mCategoryTextColor =
                (ColorPickerPreference) findPreference(PREF_CATEGORY_TEXT_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_ROOT_LIST_CATEGORY_TEXT_COLOR,
                DEFAULT_CATEGORY_TEXT_COLOR);
        mCategoryTextColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mCategoryTextColor.setSummary(hexColor);
        mCategoryTextColor.setOnPreferenceChangeListener(this);

        mTitleTextColor =
                (ColorPickerPreference) findPreference(PREF_TITLE_TEXT_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_ROOT_LIST_TITLE_TEXT_COLOR,
                DEFAULT_TITLE_TEXT_COLOR);
        mTitleTextColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mTitleTextColor.setSummary(hexColor);
        mTitleTextColor.setOnPreferenceChangeListener(this);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_ROOT_LIST_ICON_COLOR,
                DEFAULT_ICON_COLOR);
        mIconColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mIconColor.setSummary(hexColor);
        mIconColor.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup) // use the backup icon
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
        if (preference == mColorizeAccountIcons) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_ROOT_LIST_COLORIZE_ACCOUNT_ICONS,
                            value ? 1 : 0);
            return true;
        } else if (preference == mCategoryTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_ROOT_LIST_CATEGORY_TEXT_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTitleTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_ROOT_LIST_TITLE_TEXT_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_ROOT_LIST_ICON_COLOR,
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

        SettingsRootListSettings getOwner() {
            return (SettingsRootListSettings) getTargetFragment();
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
                                    Settings.System.SETTINGS_ROOT_LIST_COLORIZE_ACCOUNT_ICONS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ROOT_LIST_CATEGORY_TEXT_COLOR,
                                    DEFAULT_CATEGORY_TEXT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ROOT_LIST_TITLE_TEXT_COLOR,
                                    DEFAULT_TITLE_TEXT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ROOT_LIST_ICON_COLOR,
                                    DEFAULT_ICON_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ROOT_LIST_COLORIZE_ACCOUNT_ICONS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ROOT_LIST_CATEGORY_TEXT_COLOR,
                                    0xffbe0000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ROOT_LIST_TITLE_TEXT_COLOR,
                                    0xffff0000);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ROOT_LIST_ICON_COLOR,
                                    0xff33b5e5);
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
