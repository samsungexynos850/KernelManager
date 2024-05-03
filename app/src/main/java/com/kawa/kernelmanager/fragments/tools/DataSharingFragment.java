/*
 * Copyright (C) 2015-2017 Willi Ye <williye97@gmail.com>
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
package com.kawa.kernelmanager.fragments.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.kawa.kernelmanager.R;
import com.kawa.kernelmanager.activities.DataSharingSearchActivity;
import com.kawa.kernelmanager.fragments.DescriptionFragment;
import com.kawa.kernelmanager.fragments.recyclerview.RecyclerViewFragment;
import com.kawa.kernelmanager.services.monitor.Monitor;
import com.kawa.kernelmanager.utils.AppSettings;
import com.kawa.kernelmanager.utils.Utils;
import com.kawa.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.kawa.kernelmanager.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by willi on 17.12.16.
 */

public class DataSharingFragment extends RecyclerViewFragment {

    private SwitchView mDataSharingSwitch;

    @Override
    protected boolean showBottomFab() {
        return true;
    }

    @Override
    protected Drawable getBottomFabDrawable() {
        Drawable drawable = DrawableCompat.wrap(
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_search));
        DrawableCompat.setTint(drawable, Color.WHITE);
        return drawable;
    }

    @Override
    protected void onBottomFabClick() {
        super.onBottomFabClick();

        startActivity(new Intent(getActivity(), DataSharingSearchActivity.class));
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(
                getString(R.string.welcome), getString(R.string.data_sharing_summary)));
        addViewPagerFragment(DescriptionFragment.newInstance(
                getString(R.string.welcome),
                Utils.htmlFrom(getString(R.string.data_sharing_summary_link))));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mDataSharingSwitch = new SwitchView();
        mDataSharingSwitch.setSummary(getString(R.string.sharing_enable));
        mDataSharingSwitch.setChecked(AppSettings.isDataSharing(getActivity()));
        mDataSharingSwitch.addOnSwitchListener((switchView, isChecked) -> {
            if (isChecked) {
                Utils.startService(getActivity(), new Intent(getActivity(), Monitor.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), Monitor.class));
            }
            AppSettings.saveDataSharing(isChecked, getActivity());
        });

        items.add(mDataSharingSwitch);
    }

    private BroadcastReceiver mDisableReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mDataSharingSwitch != null) {
                mDataSharingSwitch.setChecked(false);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mDisableReceiver, new IntentFilter(Monitor.ACTION_DISABLE));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mDisableReceiver);
    }
}
