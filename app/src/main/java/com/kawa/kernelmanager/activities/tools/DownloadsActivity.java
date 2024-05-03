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
package com.kawa.kernelmanager.activities.tools;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kawa.kernelmanager.R;
import com.kawa.kernelmanager.activities.BaseActivity;
import com.kawa.kernelmanager.fragments.tools.downloads.AboutFragment;
import com.kawa.kernelmanager.fragments.tools.downloads.DownloadKernelFragment;
import com.kawa.kernelmanager.fragments.tools.downloads.FeaturesFragment;
import com.kawa.kernelmanager.utils.Utils;
import com.kawa.kernelmanager.utils.tools.SupportedDownloads;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 06.07.16.
 */
public class DownloadsActivity extends BaseActivity {

    public static final String JSON_INTENT = "json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        initToolBar();

        SupportedDownloads.KernelContent content = new SupportedDownloads.KernelContent(getIntent().getStringExtra(JSON_INTENT));
        getSupportActionBar().setTitle(Utils.htmlFrom(content.getName()).toString());

        final ViewPager viewPager = findViewById(R.id.viewpager);

        LinkedHashMap<String, Fragment> items = new LinkedHashMap<>();

        List<SupportedDownloads.KernelContent.Feature> features = content.getFeatures();
        List<SupportedDownloads.KernelContent.Download> downloads = content.getDownloads();

        if (content.getShortDescription() != null && content.getLongDescription() != null) {
            items.put(getString(R.string.about), AboutFragment.newInstance(content));
        }

        if (features.size() > 0) {
            items.put(getString(R.string.features), FeaturesFragment.newInstance(features));
        }

        if (downloads.size() > 0) {
            items.put(getString(R.string.downloads), DownloadKernelFragment.newInstance(downloads));
        }

        viewPager.setOffscreenPageLimit(items.size());
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), items);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private final LinkedHashMap<String, Fragment> mFragments;

        private PagerAdapter(FragmentManager fm, LinkedHashMap<String, Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(mFragments.keySet().toArray(new String[mFragments.size()])[position]);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.keySet().toArray(new String[mFragments.size()])[position];
        }

    }

}
