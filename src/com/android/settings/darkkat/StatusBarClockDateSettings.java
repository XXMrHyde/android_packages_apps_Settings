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
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Date;

public class StatusBarClockDateSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener { 

    private static final String PREF_CAT_CLOCK_DATE =
            "clock_date_cat_clock_date";
    private static final String PREF_CAT_CLOCK =
            "clock_date_cat_clock";
    private static final String PREF_CAT_DATE =
            "clock_date_cat_date";
    private static final String PREF_CLOCK_DATE_POSITION =
            "clock_date_position";
    private static final String PREF_SHOW_DATE =
            "clock_date_show_date";
    private static final String PREF_AM_PM =
            "clock_date_am_pm";
    private static final String PREF_DATE_SIZE =
            "clock_date_date_size";
    private static final String PREF_DATE_STYLE =
            "clock_date_date_style";
    private static final String PREF_DATE_FORMAT =
            "clock_date_date_format";
    private static final String PREF_CLOCK_DATE_COLOR =
            "clock_date_clock_date_color";

    public static final int DATE_STYLE_LOWERCASE = 1;
    public static final int DATE_STYLE_UPPERCASE = 2;
    private static final int CUSTOM_DATE_FORMAT_INDEX = 18;

    private static final int DEFAULT_TEXT_COLOR = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mClockDatePosition;
    private CheckBoxPreference mShowDate;
    private ListPreference mClockAmPm;
    private CheckBoxPreference mDateSize;
    private ListPreference mDateStyle;
    private ListPreference mDateFormat;
    private ColorPickerPreference mClockDateColor;

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

        addPreferencesFromResource(R.xml.status_bar_clock_date_settings);

        mResolver = getActivity().getContentResolver();

        mClockDatePosition =
                (ListPreference) findPreference(PREF_CLOCK_DATE_POSITION);
        int clockDatePosition = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CLOCK_DATE_POSITION, 0);
        mClockDatePosition.setValue(String.valueOf(clockDatePosition));
        mClockDatePosition.setSummary(mClockDatePosition.getEntry());
        mClockDatePosition.setOnPreferenceChangeListener(this);

        boolean isClockEnabled = clockDatePosition != 2;
        boolean isDateEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_DATE, 0) == 1;

        PreferenceCategory statusBarCatClockDate =
                (PreferenceCategory) findPreference(PREF_CAT_CLOCK_DATE);
        PreferenceCategory statusBarCatClock =
                (PreferenceCategory) findPreference(PREF_CAT_CLOCK);
        PreferenceCategory statusBarDateCategory =
                (PreferenceCategory) findPreference(PREF_CAT_DATE);
        mClockAmPm = (ListPreference) findPreference(PREF_AM_PM);
        mClockDateColor =
                (ColorPickerPreference) findPreference(PREF_CLOCK_DATE_COLOR);

        if (isClockEnabled) {
            mShowDate = (CheckBoxPreference) findPreference(PREF_SHOW_DATE);
            mShowDate.setChecked(isDateEnabled);
            mShowDate.setOnPreferenceChangeListener(this);

            // Remove "AM/PM Style" if 24 hour mode is enabled
            if (!DateFormat.is24HourFormat(getActivity())) {
                int clockAmPm = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_AM_PM, 2);
                mClockAmPm.setEnabled(true);
                mClockAmPm.setValue(String.valueOf(clockAmPm));
                mClockAmPm.setSummary(mClockAmPm.getEntry());
                mClockAmPm.setOnPreferenceChangeListener(this);
            } else {
                mClockAmPm.setSummary(
                        getResources().getString(R.string
                        .clock_date_am_pm_disabled_summary));
                mClockAmPm.setEnabled(false);
            }

            int intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_CLOCK_DATE_COLOR,
                    DEFAULT_TEXT_COLOR); 
            mClockDateColor.setNewPreviewColor(intColor);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mClockDateColor.setSummary(hexColor);
            mClockDateColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_DATE);
            statusBarCatClockDate.removePreference(mClockDateColor);
            statusBarCatClock.removePreference(mClockAmPm);
            removePreference(PREF_CAT_CLOCK_DATE);
            removePreference(PREF_CAT_CLOCK);
        }

        mDateSize = (CheckBoxPreference) findPreference(PREF_DATE_SIZE);
        mDateStyle = (ListPreference) findPreference(PREF_DATE_STYLE);
        mDateFormat = (ListPreference) findPreference(PREF_DATE_FORMAT);
        if (isClockEnabled && isDateEnabled) {
            mDateSize.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_DATE_SIZE, 0) == 1);
            mDateSize.setOnPreferenceChangeListener(this);

            int dateStyle = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_DATE_STYLE, 0);
            mDateStyle.setValue(String.valueOf(dateStyle));
            mDateStyle.setSummary(mDateStyle.getEntry());
            mDateStyle.setOnPreferenceChangeListener(this);

            if (mDateFormat.getValue() == null) {
                mDateFormat.setValue("EEE");
            } 
            mDateFormat.setOnPreferenceChangeListener(this);
            parseClockDateFormats();
        } else {
            statusBarDateCategory.removePreference(mDateSize);
            statusBarDateCategory.removePreference(mDateStyle);
            statusBarDateCategory.removePreference(mDateFormat);
            removePreference(PREF_CAT_DATE);
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
        boolean result = false;

        AlertDialog dialog;

        if (preference == mClockDatePosition) {
            int clockDatePosition = Integer.valueOf((String) newValue);
            int index = mClockDatePosition.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_CLOCK_DATE_POSITION, clockDatePosition);
            preference.setSummary(mClockDatePosition.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowDate) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_DATE, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mClockAmPm) {
            int clockAmPm = Integer.valueOf((String) newValue);
            int index = mClockAmPm.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_AM_PM, clockAmPm);
            preference.setSummary(mClockAmPm.getEntries()[index]);
            return true;
        } else if (preference == mDateSize) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_DATE_SIZE, value ? 1 : 0);
            return true;
        } else if (preference == mDateStyle) {
            int dateStyle = Integer.valueOf((String) newValue);
            int index = mDateStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_DATE_STYLE, dateStyle);
            preference.setSummary(mDateStyle.getEntries()[index]);
            return true;
        }  else if (preference == mDateFormat) {
            int index = mDateFormat.findIndexOfValue((String) newValue);

            if (index == CUSTOM_DATE_FORMAT_INDEX) {
                AlertDialog.Builder alert =
                        new AlertDialog.Builder(getActivity());
                alert.setTitle(
                    R.string.clock_date_date_string_edittext_title);
                alert.setMessage(
                    R.string.clock_date_date_string_edittext_summary);

                final EditText input = new EditText(getActivity());
                String oldText = Settings.System.getString(mResolver,
                        Settings.System.STATUS_BAR_DATE_FORMAT);
                if (oldText != null) {
                    input.setText(oldText);
                }
                alert.setView(input);

                alert.setPositiveButton(R.string.menu_save,
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface,
                            int whichButton) {
                        String value = input.getText().toString();
                        if (value.equals("")) {
                            return;
                        }
                        Settings.System.putString(mResolver,
                            Settings.System.STATUS_BAR_DATE_FORMAT, value);

                        return;
                    }
                });

                alert.setNegativeButton(R.string.menu_cancel,
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface,
                        int which) {
                        return;
                    }
                });
                dialog = alert.create();
                dialog.show();
            } else {
                if ((String) newValue != null) {
                    Settings.System.putString(mResolver,
                        Settings.System.STATUS_BAR_DATE_FORMAT, (String) newValue);
                }
            }
            return true;
        } else if (preference == mClockDateColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_CLOCK_DATE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void parseClockDateFormats() {
        // Parse and repopulate mStatusBarDateFormat's entries based on current date.
        String[] dateEntries = getResources().getStringArray(
                R.array.clock_date_date_format_entries);
        CharSequence parsedDateEntries[];
        parsedDateEntries = new String[dateEntries.length];
        Date now = new Date();

        int lastEntry = dateEntries.length - 1;
        int dateFormat = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_DATE_STYLE, 2);
        for (int i = 0; i < dateEntries.length; i++) {
            if (i == lastEntry) {
                parsedDateEntries[i] = dateEntries[i];
            } else {
                String newDate;
                CharSequence dateString = DateFormat.format(dateEntries[i], now);
                if (dateFormat == DATE_STYLE_LOWERCASE) {
                    newDate = dateString.toString().toLowerCase();
                } else if (dateFormat == DATE_STYLE_UPPERCASE) {
                    newDate = dateString.toString().toUpperCase();
                } else {
                    newDate = dateString.toString();
                }

                parsedDateEntries[i] = newDate;
            }
        }
        mDateFormat.setEntries(parsedDateEntries);
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

        StatusBarClockDateSettings getOwner() {
            return (StatusBarClockDateSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_AM_PM, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_DATE_SIZE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_DATE_STYLE, 0);
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_DATE_FORMAT,
                                    "EEE");
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CLOCK_DATE_COLOR,
                                    DEFAULT_TEXT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_AM_PM, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_DATE_SIZE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_DATE_STYLE, 0);
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_DATE_FORMAT,
                                    "EEE");
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CLOCK_DATE_COLOR,
                                    0xffff0000);
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
