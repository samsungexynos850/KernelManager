/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.kawa.kernelmanager.views.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.kawa.kernelmanager.utils.ViewUtils;

/**
 * Created by willi on 05.05.16.
 */
public class GenericSelectView2 extends ValueView2 {

    public interface OnGenericValueListener {
        void onGenericValueSelected(GenericSelectView2 genericSelectView, String value);
    }

    private String mValueRaw;
    private OnGenericValueListener mOnGenericValueListener;
    private int mInputType = -1;
    private boolean mShowDialog;

    @Override
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        if (mShowDialog) {
            showDialog(activity);
        }
    }

    @Override
    public void onCreateView(View view) {
        view.setOnClickListener(v -> showDialog(v.getContext()));
        super.onCreateView(view);
    }

    public void setValueRaw(String value) {
        mValueRaw = value;
    }

    public void setOnGenericValueListener(OnGenericValueListener onGenericValueListener) {
        mOnGenericValueListener = onGenericValueListener;
    }

    public void setInputType(int inputType) {
        mInputType = inputType;
    }

    private void showDialog(Context context) {
        if (mValueRaw == null) {
            mValueRaw = getValue();
        }
        if (mValueRaw == null) return;

        mShowDialog = true;
        ViewUtils.dialogEditText(mValueRaw, (dialog, which) -> {
        }, text -> {
            setValueRaw(text);
            if (mOnGenericValueListener != null) {
                mOnGenericValueListener.onGenericValueSelected(GenericSelectView2.this, text);
            }
        }, mInputType, context).setTitle(getTitle()).setOnDismissListener(
                dialog -> mShowDialog = false).show();
    }

}
