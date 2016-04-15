/*
 * Copyright (C) 2010 Daniel Nilsson
 * Copyright (C) 2013 Slimroms
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

package net.margaritov.preference.colorpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.internal.util.darkkat.ColorHelper;

import com.android.settings.R;

public class ColorPickerDialog extends Dialog implements
        ColorPickerView.OnColorChangedListener, PopupMenu.OnMenuItemClickListener,
        TextWatcher, View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener {

    private static final String PREFERENCE_NAME  =
            "color_picker_dialog";
    private static final String FAVORITE_COLOR_BUTTON  =
            "favorite_color_button_";

    private static final int PALETTE_DARKKAT  = 0;
    private static final int PALETTE_MATERIAL = 1;
    private static final int PALETTE_RGB      = 2;

    private static final int SHOW = 0;
    private static final int HIDE = 1;
    private static final int NONE = 2;

    private View mColorPickerView;

    private LinearLayout mActionBarMain;
    private LinearLayout mActionBarEditHex;

    private ImageButton mBackButton;
    private ColorPickerApplyColorButton mApplyColorButton;
    private ImageButton mMoreButton;

    private ImageButton mHexBackButton;
    private EditText mHex;
    private ImageButton mSetButton;
    private View mDivider;

    private ColorPickerView mColorPicker;

    private final ContentResolver mResolver;
    private final Resources mResources;
	private final float mDensity;

    private final int mInitialColor;
    private final int mAndroidColor;
    private final int mDarkKatColor;
    private int mOldColorValue;
    private int mNewColorValue;
    private final boolean mHideReset;
    private boolean mEditHexBarVisible;

    private Animator mAlphaAnimator;
    private Animator mApplyColorButtonAnimator;

    private int mApplyColorIconAnimationType;

    private OnColorChangedListener mListener;

    public interface OnColorChangedListener {
        public void onColorChanged(int color);
    }

    public ColorPickerDialog(Context context, int theme, int initialColor,
            int androidColor, int darkkatColor) {
        super(context, theme);

        mResolver = context.getContentResolver();
        mResources = context.getResources();
		mDensity = mResources.getDisplayMetrics().density;

        mInitialColor = initialColor;
        mOldColorValue = mInitialColor;
        mNewColorValue = mOldColorValue;
        mAndroidColor = androidColor;
        mDarkKatColor = darkkatColor;
        if (mAndroidColor != 0x00000000 && mDarkKatColor != 0x00000000) {
            mHideReset = false;
        } else {
            mHideReset = true;
        }
        mEditHexBarVisible = false;

        setUp();
    }

    private void setUp() {
        // To fight color branding.
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        mColorPickerView = inflater.inflate(R.layout.color_picker_dialog, null);
        setContentView(mColorPickerView);

        mActionBarMain = (LinearLayout) mColorPickerView.findViewById(R.id.action_bar_main);
        mActionBarEditHex = (LinearLayout) mColorPickerView.findViewById(R.id.action_bar_edit_hex);
        mActionBarEditHex.setVisibility(View.GONE);

        mBackButton = (ImageButton) mColorPickerView.findViewById(R.id.back);
        mBackButton.setOnClickListener(this);

        mApplyColorButton =
                (ColorPickerApplyColorButton) mColorPickerView.findViewById(R.id.apply_color_button);
        mApplyColorButton.setColor(mInitialColor);
        mApplyColorButton.applySetIconAlpha(0f);
        mApplyColorButton.showSetIcon(false);

        mMoreButton = (ImageButton) mColorPickerView.findViewById(R.id.more);
        mMoreButton.setOnClickListener(this);

        mHexBackButton = (ImageButton) mColorPickerView.findViewById(R.id.action_bar_edit_hex_back);
        mHexBackButton.setOnClickListener(this);

        mHex = (EditText) mColorPickerView.findViewById(R.id.hex);
        mHex.setText(ColorPickerPreference.convertToARGB(mInitialColor));
        mHex.setOnFocusChangeListener(this);

        mSetButton = (ImageButton) mColorPickerView.findViewById(R.id.enter);
        mSetButton.setOnClickListener(this);

        mDivider = mColorPickerView.findViewById(R.id.divider);
        mDivider.setVisibility(View.GONE);

        mColorPicker = (ColorPickerView) mColorPickerView.findViewById(R.id.color_picker_view);
        mColorPicker.setOnColorChangedListener(this);
        mColorPicker.setColor(mInitialColor);

        mAlphaAnimator = createAlphaAnimator(0, 1);
        mApplyColorButtonAnimator = createApplyColorButtonAnimator(0, 1);

        setUpFavoriteColorButtons();
        setUpPaletteColorButtons();
    }

    private void setUpFavoriteColorButtons() {
        TypedArray ta = mResources.obtainTypedArray(R.array.color_picker_favorite_color_buttons);

        for (int i=0; i<4; i++) {
            int resId = ta.getResourceId(i, 0);
            int buttonNumber = i + 1;
            String tag = String.valueOf(buttonNumber);
            ColorPickerColorButton button = (ColorPickerColorButton) mColorPickerView.findViewById(resId);
            button.setTag(tag);
            button.setOnLongClickListener(this);
            if (getFavoriteButtonValue(button) != 0) {
                button.setImageResource(R.drawable.color_picker_color_button_color);
                button.setColor(getFavoriteButtonValue(button));
                button.setOnClickListener(this);
            }
        }

        ta.recycle();
    }

    private void setUpPaletteColorButtons() {
        TypedArray layouts = mResources.obtainTypedArray(R.array.color_picker_palette_color_buttons_layouts);
        TypedArray buttons = mResources.obtainTypedArray(R.array.color_picker_palette_color_buttons);
        TypedArray colors = mResources.obtainTypedArray(R.array.color_picker_darkkat_palette);

        for (int i=0; i<3; i++) {
            int layoutResId = layouts.getResourceId(i, 0);
            LinearLayout layout = (LinearLayout) mColorPickerView.findViewById(layoutResId);
            TextView paletteTitle = (TextView) layout.findViewById(R.id.palette_color_buttons_title);
            int titleResId = R.string.palette_darkkat_title;
            if (i == PALETTE_MATERIAL) {
                titleResId = R.string.palette_material_title;
                colors = mResources.obtainTypedArray(R.array.color_picker_material_palette);
            } else if (i == PALETTE_RGB) {
                titleResId = R.string.palette_rgb_title;
                colors = mResources.obtainTypedArray(R.array.color_picker_rgb_palette);
            }
            paletteTitle.setText(titleResId);

            for (int j=0; j<8; j++) {
                int buttonResId = buttons.getResourceId(j, 0);
                ColorPickerColorButton button = (ColorPickerColorButton) layout.findViewById(buttonResId);
                button.setColor(mResources.getColor(colors.getResourceId(j, 0)));
                button.setOnClickListener(this);
            }
        }

        layouts.recycle();
        buttons.recycle();
        colors.recycle();
    }

    private ValueAnimator createAlphaAnimator(float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = animation.getAnimatedFraction();
                if (mEditHexBarVisible) {
                    mActionBarMain.setAlpha(alpha);
                    mActionBarEditHex.setAlpha(1f - alpha);
                    mDivider.setAlpha(1f - alpha);
                } else {
                    mActionBarMain.setAlpha(1f - alpha);
                    mActionBarEditHex.setAlpha(alpha);
                    mDivider.setAlpha(alpha);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mEditHexBarVisible) {
                    mActionBarMain.setVisibility(View.VISIBLE);
                    mActionBarMain.jumpDrawablesToCurrentState();
                } else {
                    mActionBarEditHex.setVisibility(View.VISIBLE);
                    mActionBarEditHex.jumpDrawablesToCurrentState();
                    mDivider.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mEditHexBarVisible) {
                    mActionBarEditHex.setVisibility(View.GONE);
                    mDivider.setVisibility(View.GONE);
                    mEditHexBarVisible = false;
                } else {
                    mActionBarMain.setVisibility(View.GONE);
                    mEditHexBarVisible = true;
                }
            }
        });
        return animator;
    }

    private ValueAnimator createApplyColorButtonAnimator(float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                int blended = ColorHelper.getBlendColor(mOldColorValue, mNewColorValue, position);
                mApplyColorButton.setColor(blended);
                if (mApplyColorIconAnimationType != NONE) {
                    float translationX = mApplyColorIconAnimationType == SHOW ? 1f : 0f;
                    float alpha = 0f;
                    boolean applyAlpha = false;

                    if (mApplyColorIconAnimationType == SHOW) {
                        translationX = 48 * mDensity * (1f - position);
                        if (position > 0.5f) {
                            alpha = (position - 0.5f) * 2;
                            applyAlpha = true;
                        }
                    } else {
                        translationX = 48 * mDensity * position;
                        if (position <= 0.5f && position > 0f) {
                            alpha = 1f - position * 2;
                            applyAlpha = true;
                        }
                    }
                    mApplyColorButton.setColorPreviewTranslationX(translationX);
                    if (applyAlpha) {
                        mApplyColorButton.applySetIconAlpha(alpha);
                    }
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mApplyColorIconAnimationType != NONE) {
                    if (mApplyColorIconAnimationType != SHOW) {
                        mApplyColorButton.showSetIcon(false);
                    } else {
                        mApplyColorButton.setOnClickListener(getDialogOnClickListener());
                    }
                }
                mOldColorValue = mNewColorValue;
            }
        });
        return animator;
    }

    private View.OnClickListener getDialogOnClickListener() {
        return this;
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void onColorChanged(int color) {
        mApplyColorIconAnimationType = NONE;
        if (color != mOldColorValue) {
            mNewColorValue = color;
            if (mNewColorValue == mInitialColor) {
                if (mOldColorValue != mInitialColor) {
                    mApplyColorIconAnimationType = HIDE;
                    mApplyColorButton.setOnClickListener(null);
                    mApplyColorButton.setClickable(false);
                }
            } else if (mOldColorValue == mInitialColor) {
                mApplyColorIconAnimationType = SHOW;
                mApplyColorButton.showSetIcon(true);
            }
            mApplyColorButtonAnimator.start();

            try {
                if (mHex != null) {
                    mHex.setText(ColorPickerPreference.convertToARGB(color));
                }
            } catch (Exception e) {}
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back ||
                v.getId() == R.id.apply_color_button) {
            if (mListener != null && v.getId() == R.id.apply_color_button) {
                mListener.onColorChanged(mApplyColorButton.getColor());
            }
            dismiss();
        } else if (v.getId() == R.id.more) {
            showMorePopupMenu(v);
        } else if (v.getId() == R.id.action_bar_edit_hex_back) {
            mEditHexBarVisible = true;
            mAlphaAnimator.start();
        } else if (v.getId() == R.id.enter) {
            String text = mHex.getText().toString();
            try {
                int newColor = ColorPickerPreference.convertToColorInt(text);
                if (newColor != mOldColorValue) {
                    mColorPicker.setColor(newColor, true);
                }
            } catch (Exception e) {}

            mEditHexBarVisible = true;
            mAlphaAnimator.start();
        } else if (v instanceof ColorPickerColorButton) {
            try {
                int newColor = ((ColorPickerColorButton) v).getColor();
                if (newColor != mOldColorValue) {
                    mColorPicker.setColor(newColor, true);
                }
            } catch (Exception e) {}
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ColorPickerColorButton button = (ColorPickerColorButton) v;
        if (!v.hasOnClickListeners()) {
            button.setImageResource(R.drawable.color_picker_color_button_color);
            button.setOnClickListener(this);
        }
        button.setColor(mApplyColorButton.getColor());
        writeFavoriteButtonValue(button);
        return true;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.reset_android) {
            mColorPicker.setColor(mAndroidColor, true);
            return true;
        } else if (item.getItemId() == R.id.reset_darkkat) {
            mColorPicker.setColor(mDarkKatColor, true);
            return true;
        } else if (item.getItemId() == R.id.edit_hex) {
            mEditHexBarVisible = false;
            mAlphaAnimator.start();
            return true;
        }
        return false;
    }

    private void showMorePopupMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.color_picker_more);
        popup.setForceShowIcon();
        if (mHideReset) {
            popup.getMenu().removeItem(R.id.reset_color);
        }
        popup.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            mHex.removeTextChangedListener(this);
            InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            mHex.addTextChangedListener(this);
        }
    }

    private int getColor() {
        return mColorPicker.getColor();
    }

    public void setAlphaSliderVisible(boolean visible) {
        mColorPicker.setAlphaSliderVisible(visible);
    }

    private void writeFavoriteButtonValue(ColorPickerColorButton button) {
        SharedPreferences preferences =
                getContext().getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        preferences.edit().putInt(FAVORITE_COLOR_BUTTON + (String) button.getTag(),
                button.getColor()).commit();
    }

    private int getFavoriteButtonValue(ColorPickerColorButton button) {
        SharedPreferences preferences =
                getContext().getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return preferences.getInt(FAVORITE_COLOR_BUTTON + (String) button.getTag(), 0);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt("new_color", mNewColorValue);
        state.putInt("old_color", mOldColorValue);
        state.putBoolean("edit_hex_bar_visible", mEditHexBarVisible);
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mNewColorValue = savedInstanceState.getInt("new_color");
        mOldColorValue = savedInstanceState.getInt("old_color");
        mEditHexBarVisible = savedInstanceState.getBoolean("edit_hex_bar_visible");

        mColorPicker.setColor(mNewColorValue);
        if (mNewColorValue != mInitialColor) {
            mApplyColorButton.setColor(mNewColorValue);
            mApplyColorButton.setColorPreviewTranslationX(0f);
            mApplyColorButton.showSetIcon(true);
            mApplyColorButton.applySetIconAlpha(1f);
            mApplyColorButton.setOnClickListener(this);
        }

        if (mEditHexBarVisible) {
            mActionBarMain.setVisibility(View.GONE);
            mActionBarEditHex.setVisibility(View.VISIBLE);
            mDivider.setVisibility(View.VISIBLE);
        }
    }
}
