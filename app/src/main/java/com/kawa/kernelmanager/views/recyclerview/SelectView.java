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
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kawa.kernelmanager.R;
import com.kawa.kernelmanager.views.dialog.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 01.05.16.
 */
public class SelectView extends ValueView {

    public interface OnItemSelected {
        void onItemSelected(SelectView selectView, int position, String item);
    }

    private View mView;
    private OnItemSelected mOnItemSelected;
    private MaterialAlertDialogBuilder mDialog;
    private List<String> mItems = new ArrayList<>();
    private boolean mEnabled = true;

    @Override
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        if (mDialog != null) {
            mDialog.show();
        }
    }

    @Override
    public void onCreateView(View view) {
        mView = view;
        super.onCreateView(view);
    }

    public void setItem(String item) {
        setValue(item);
    }

    public void setItem(int position) {
        if (position >= 0 && position < mItems.size()) {
            setValue(mItems.get(position));
        } else {
            setValue(R.string.not_in_range);
        }
    }

    public void setItems(List<String> items) {
        mItems = items;
        refresh();
    }

    public void setOnItemSelected(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
    }

    public void setEnabled(boolean enable) {
        mEnabled = enable;
        refresh();
    }

    private void showDialog(Context context) {
        String[] items = mItems.toArray(new String[mItems.size()]);

        mDialog = new MaterialAlertDialogBuilder(context).setItems(items,
                (dialog, which) -> {
                    setItem(which);
                    if (mOnItemSelected != null) {
                        mOnItemSelected.onItemSelected(SelectView.this, which, mItems.get(which));
                    }
                })
                .setOnDismissListener(dialog -> mDialog = null);
        if (getTitle() != null) {
            mDialog.setTitle(getTitle());
        }
        mDialog.show();
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mView != null && getValue() != null) {
            mView.setOnClickListener(v -> showDialog(v.getContext()));
            mView.setEnabled(mEnabled);
        }
    }
}
