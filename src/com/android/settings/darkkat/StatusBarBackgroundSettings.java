/*
 * Copyright (C) 2016 DarkKat
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
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.InstrumentedFragment;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarBackgroundSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener { 

    private static final String PREF_CAT_GRADIENT_OPTIONS =
            "background_cat_gradient_options";
    private static final String PREF_CAT_COLORS =
            "background_cat_colors";
    private static final String PREF_TYPE =
            "background_type";
    private static final String PREF_GRADIENT_ORIENTATION =
            "background_gradient_orientation";
    private static final String PREF_USE_CENTER_COLOR =
            "background_gradient_use_center_color";
    private static final String PREF_START_COLOR =
            "background_start_color";
    private static final String PREF_CENTER_COLOR =
            "background_center_color";
    private static final String PREF_END_COLOR =
            "background_end_color";

    private static final int BACKGROUND_TYPE_GRADIENT = 1;
    private static final int BACKGROUND_TYPE_DISABLED = 2;

    private static final int BACKGROUND_ORIENTATION_T_B = 270;

    private static final int BLACK = 0xff000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ListPreference mType;
    private ListPreference mGradientOrientation;
    private SwitchPreference mUseCenterColor;
    private ColorPickerPreference mStartColor;
    private ColorPickerPreference mCenterColor;
    private ColorPickerPreference mEndColor;

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

        addPreferencesFromResource(R.xml.status_bar_background_settings);

        mResolver = getContentResolver();

        final int type = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BACKGROUND_TYPE, BACKGROUND_TYPE_DISABLED);

        mType =
                (ListPreference) findPreference(PREF_TYPE);
        mType.setValue(String.valueOf(type));
        mType.setSummary(mType.getEntry());
        mType.setOnPreferenceChangeListener(this);

        PreferenceCategory catGradientOptions =
                (PreferenceCategory) findPreference(PREF_CAT_GRADIENT_OPTIONS);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        if (type != BACKGROUND_TYPE_DISABLED) {
            int intColor;
            String hexColor;

            mStartColor =
                    (ColorPickerPreference) findPreference(PREF_START_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BACKGROUND_START_COLOR, BLACK); 
            mStartColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mStartColor.setSummary(hexColor);
            mStartColor.setResetColors(BLACK, BLACK);
            mStartColor.setOnPreferenceChangeListener(this);

            if (type == BACKGROUND_TYPE_GRADIENT) {
                final boolean useCenterColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_BACKGROUND_GRADIENT_USE_CENTER_COLOR, 0) == 1;

                mGradientOrientation =
                        (ListPreference) findPreference(PREF_GRADIENT_ORIENTATION);
                final int orientation = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_BACKGROUND_GRADIENT_ORIENTATION,
                        BACKGROUND_ORIENTATION_T_B);
                mGradientOrientation.setValue(String.valueOf(orientation));
                mGradientOrientation.setSummary(mGradientOrientation.getEntry());
                mGradientOrientation.setOnPreferenceChangeListener(this);

                mUseCenterColor = (SwitchPreference) findPreference(PREF_USE_CENTER_COLOR);
                mUseCenterColor.setChecked(useCenterColor);
                mUseCenterColor.setOnPreferenceChangeListener(this);

                mStartColor.setTitle(getResources().getString(R.string.background_start_color_title));

                if (useCenterColor) {
                    mCenterColor =
                            (ColorPickerPreference) findPreference(PREF_CENTER_COLOR);
                    intColor = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_BACKGROUND_CENTER_COLOR, BLACK); 
                    mCenterColor.setNewPreviewColor(intColor);
                    hexColor = String.format("#%08x", (0xffffffff & intColor));
                    mCenterColor.setSummary(hexColor);
                    mCenterColor.setResetColors(BLACK, BLACK);
                    mCenterColor.setOnPreferenceChangeListener(this);
                } else {
                    catColors.removePreference(findPreference(PREF_CENTER_COLOR));
                }

                mEndColor =
                        (ColorPickerPreference) findPreference(PREF_END_COLOR);
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_BACKGROUND_END_COLOR, BLACK); 
                mEndColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mEndColor.setSummary(hexColor);
                mEndColor.setResetColors(BLACK, BLACK);
                mEndColor.setOnPreferenceChangeListener(this);
            } else {
                catGradientOptions.removePreference(findPreference(PREF_GRADIENT_ORIENTATION));
                catGradientOptions.removePreference(findPreference(PREF_USE_CENTER_COLOR));
                catColors.removePreference(findPreference(PREF_CENTER_COLOR));
                catColors.removePreference(findPreference(PREF_END_COLOR));
                removePreference(PREF_CAT_GRADIENT_OPTIONS);
            }
        } else {
            catGradientOptions.removePreference(findPreference(PREF_GRADIENT_ORIENTATION));
            catGradientOptions.removePreference(findPreference(PREF_USE_CENTER_COLOR));
            catColors.removePreference(findPreference(PREF_START_COLOR));
            catColors.removePreference(findPreference(PREF_CENTER_COLOR));
            catColors.removePreference(findPreference(PREF_END_COLOR));
            removePreference(PREF_CAT_GRADIENT_OPTIONS);
            removePreference(PREF_CAT_COLORS);
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
        boolean value;
        int intValue;
        int index;
        String hex;
        int intHex;

        if (preference == mType) {
            intValue = Integer.valueOf((String) newValue);
            index = mType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BACKGROUND_TYPE, intValue);
            mType.setSummary(mType.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mGradientOrientation) {
            intValue = Integer.valueOf((String) newValue);
            index = mGradientOrientation.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BACKGROUND_GRADIENT_ORIENTATION,
                    intValue);
            mGradientOrientation.setSummary(mGradientOrientation.getEntries()[index]);
            return true;
        } else if (preference == mUseCenterColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BACKGROUND_GRADIENT_USE_CENTER_COLOR,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStartColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BACKGROUND_START_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCenterColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BACKGROUND_CENTER_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mEndColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BACKGROUND_END_COLOR, intHex);
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

        StatusBarBackgroundSettings getOwner() {
            return (StatusBarBackgroundSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_BACKGROUND_TYPE,
                                    BACKGROUND_TYPE_DISABLED);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_GRADIENT_ORIENTATION,
                                    BACKGROUND_ORIENTATION_T_B);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_GRADIENT_USE_CENTER_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_START_COLOR, BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_CENTER_COLOR, BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_END_COLOR, BLACK);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_TYPE,
                                    BACKGROUND_TYPE_DISABLED);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_GRADIENT_ORIENTATION,
                                    BACKGROUND_ORIENTATION_T_B);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_GRADIENT_USE_CENTER_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_START_COLOR, BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_CENTER_COLOR, BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BACKGROUND_END_COLOR, BLACK);
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
