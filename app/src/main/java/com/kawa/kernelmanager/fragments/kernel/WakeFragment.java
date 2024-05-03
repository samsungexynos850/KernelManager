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
package com.kawa.kernelmanager.fragments.kernel;

import android.widget.Toast;

import com.kawa.kernelmanager.R;
import com.kawa.kernelmanager.fragments.ApplyOnBootFragment;
import com.kawa.kernelmanager.fragments.recyclerview.RecyclerViewFragment;
import com.kawa.kernelmanager.utils.kernel.wake.Dt2s;
import com.kawa.kernelmanager.utils.kernel.wake.Dt2w;
import com.kawa.kernelmanager.utils.kernel.wake.GestureVibration;
import com.kawa.kernelmanager.utils.kernel.wake.Gestures;
import com.kawa.kernelmanager.utils.kernel.wake.Misc;
import com.kawa.kernelmanager.utils.kernel.wake.S2s;
import com.kawa.kernelmanager.utils.kernel.wake.S2w;
import com.kawa.kernelmanager.utils.kernel.wake.T2w;
import com.kawa.kernelmanager.utils.kernel.wake.TspCmd;
import com.kawa.kernelmanager.views.recyclerview.CardView;
import com.kawa.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.kawa.kernelmanager.views.recyclerview.SeekBarView;
import com.kawa.kernelmanager.views.recyclerview.SelectView;
import com.kawa.kernelmanager.views.recyclerview.SelectViewCheckbox;
import com.kawa.kernelmanager.views.recyclerview.SwitchView;
import com.kawa.kernelmanager.utils.AppSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by willi on 23.06.16.
 */
public class WakeFragment extends RecyclerViewFragment {

    private Dt2w mDt2w;
    private S2w mS2w;
    private T2w mT2w;
    private Dt2s mDt2s;
    private S2s mS2s;
    private Misc mMisc;

    @Override
    protected void init() {
        super.init();

        mDt2w = Dt2w.getInstance();
        mS2w = S2w.getInstance();
        mT2w = T2w.getInstance();
        mDt2s = Dt2s.getInstance();
        mS2s = S2s.getInstance();
        mMisc = Misc.getInstance();
        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (TspCmd.hasGlove()) {
            gloveInit(items);
        }
        if (mDt2w.supported()) {
            dt2wInit(items);
        }
        s2wInit(items);
        if (mT2w.supported()) {
            t2wInit(items);
        }
        if (mDt2s.supported()) {
            dt2sInit(items);
        }
        if (mS2s.supported()) {
            s2sInit(items);
        }
        if (GestureVibration.supported()) {
            gesVibInit(items);
        }
        if (mMisc.hasWake()) {
            wakeMiscInit(items);
        }
        if (Gestures.supported()) {
            gestureInit(items);
        }
        if (mMisc.hasCamera()) {
            cameraInit(items);
        }
        if (mMisc.hasPocket()) {
            pocketInit(items);
        }
        if (mMisc.hasTimeout()) {
            timeoutInit(items);
        }
        if (mMisc.hasChargeTimeout()) {
            chargetimeoutInit(items);
        }
        if (mMisc.hasPowerKeySuspend()) {
            powerKeySuspendInit(items);
        }
        if (mMisc.hasKeyPowerMode()) {
            keyPowerModeInit(items);
        }
        if (mMisc.hasChargingMode()) {
            chargingModeInit(items);
        }
        areaInit(items);
        vibrationInit(items);
        if (TspCmd.supported()) {
            cmdInit(items);
        }
    }

    private void dt2wInit(List<RecyclerViewItem> items) {
        CardView dt2wCard = new CardView(getActivity());
        dt2wCard.setTitle(getString(R.string.dt2w));

        SelectView dt2w = new SelectView();
        dt2w.setTitle(getString(R.string.dt2w));
        dt2w.setSummary(getString(R.string.dt2w_summary));
        dt2w.setItems(mDt2w.getMenu(getActivity()));
        dt2w.setItem(mDt2w.get());
        dt2w.setOnItemSelected((selectView, position, item)
                -> mDt2w.set(position, getActivity()));

        dt2wCard.addItem(dt2w);
        items.add(dt2wCard);
    }

    private void s2wInit(List<RecyclerViewItem> items) {
        CardView s2wCard = new CardView(getActivity());
        s2wCard.setTitle(getString(R.string.s2w));

        int n = mS2w.get();

        if (mS2w.supported()) {
            SelectViewCheckbox s2w = new SelectViewCheckbox();
            s2w.setTitle(getString(R.string.s2w));
            s2w.setSummary(getString(R.string.s2w_summary));
            s2w.setItems(mS2w.getMenu(getActivity()));
            s2w.setItem(mS2w.getStringValue(getActivity(), n));

            s2wCard.addItem(s2w);
        }

        if (mS2w.hasLenient()) {
            SwitchView lenient = new SwitchView();
            lenient.setTitle(getString(R.string.lenient));
            lenient.setSummary(getString(R.string.lenient_summary));
            lenient.setChecked(mS2w.isLenientEnabled());
            lenient.addOnSwitchListener((switchView, isChecked)
                    -> mS2w.enableLenient(isChecked, getActivity()));

            s2wCard.addItem(lenient);
        }

        if (s2wCard.size() > 0) {
            items.add(s2wCard);
        }
    }

    private void t2wInit(List<RecyclerViewItem> items) {
        CardView t2wCard = new CardView(getActivity());
        t2wCard.setTitle(getString(R.string.t2w));

        SelectView t2w = new SelectView();
        t2w.setTitle(getString(R.string.t2w));
        t2w.setSummary(getString(R.string.t2w_summary));
        t2w.setItems(mT2w.getMenu(getActivity()));
        t2w.setItem(mT2w.get());
        t2w.setOnItemSelected((selectView, position, item)
                -> mT2w.set(position, getActivity()));

        t2wCard.addItem(t2w);
        items.add(t2wCard);
    }

    private void dt2sInit(List<RecyclerViewItem> items) {
        CardView dt2sCard = new CardView(getActivity());
        dt2sCard.setTitle(getString(R.string.dt2s));

        SelectView dt2s = new SelectView();
        dt2s.setTitle(getString(R.string.dt2s));
        dt2s.setSummary(getString(R.string.dt2s_summary));
        dt2s.setItems(mDt2s.getMenu(getActivity()));
        dt2s.setItem(mDt2s.get());
        dt2s.setOnItemSelected((selectView, position, item)
                -> mDt2s.set(position, getActivity()));

        dt2sCard.addItem(dt2s);
        items.add(dt2sCard);
    }

    private void s2sInit(List<RecyclerViewItem> items) {
        CardView s2sCard = new CardView(getActivity());
        s2sCard.setTitle(getString(R.string.s2s));

        SelectView s2s = new SelectView();
        s2s.setTitle(getString(R.string.s2s));
        s2s.setSummary(getString(R.string.s2s_summary));
        s2s.setItems(mS2s.getMenu(getActivity()));
        s2s.setItem(mS2s.get());
        s2s.setOnItemSelected((selectView, position, item)
                -> mS2s.set(position, getActivity()));

        s2sCard.addItem(s2s);
        items.add(s2sCard);
    }

    private void gesVibInit(List<RecyclerViewItem> items){
        CardView gesVibCard = new CardView(getActivity());
        gesVibCard.setTitle(getString(R.string.gesVib_title));

        SeekBarView gesVib = new SeekBarView();
        gesVib.setTitle(getString(R.string.gesVib_title));
        gesVib.setMax(90);
        gesVib.setMin(0);
        gesVib.setProgress(GestureVibration.get());
        gesVib.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                GestureVibration.set(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        gesVibCard.addItem(gesVib);
        items.add(gesVibCard);
    }

    private void wakeMiscInit(List<RecyclerViewItem> items) {
        SelectView wake = new SelectView();
        wake.setSummary(getString(R.string.wake));
        wake.setItems(mMisc.getWakeMenu(getActivity()));
        wake.setItem(mMisc.getWake());
        wake.setOnItemSelected((selectView, position, item)
                -> mMisc.setWake(position, getActivity()));

        items.add(wake);
    }

    private void gestureInit(List<RecyclerViewItem> items) {
        List<String> gestures = Gestures.getMenu(requireActivity());
        for (int i = 0; i < gestures.size(); i++) {
            SwitchView gesture = new SwitchView();
            gesture.setSummary(gestures.get(i));
            gesture.setChecked(Gestures.isEnabled(i));

            final int position = i;
            gesture.addOnSwitchListener((switchView, isChecked)
                    -> Gestures.enable(isChecked, position, getActivity()));

            items.add(gesture);
        }
    }

    private void cameraInit(List<RecyclerViewItem> items) {
        SwitchView camera = new SwitchView();
        camera.setTitle(getString(R.string.camera_gesture));
        camera.setSummary(getString(R.string.camera_gesture_summary));
        camera.setChecked(mMisc.isCameraEnabled());
        camera.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableCamera(isChecked, getActivity()));

        items.add(camera);
    }

    private void pocketInit(List<RecyclerViewItem> items) {
        SwitchView pocket = new SwitchView();
        pocket.setTitle(getString(R.string.pocket_mode));
        pocket.setSummary(getString(R.string.pocket_mode_summary));
        pocket.setChecked(mMisc.isPocketEnabled());
        pocket.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enablePocket(isChecked, getActivity()));

        items.add(pocket);
    }

    private void timeoutInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 1; i <= mMisc.getTimeoutMax(); i++)
            list.add(i + getString(R.string.min));

        SeekBarView timeout = new SeekBarView();
        timeout.setTitle(getString(R.string.timeout));
        timeout.setSummary(getString(R.string.timeout_summary));
        timeout.setItems(list);
        timeout.setProgress(mMisc.getTimeout());
        timeout.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mMisc.setTimeout(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(timeout);
    }

    private void chargetimeoutInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 1; i <= 60; i++) {
            list.add(i + getString(R.string.min));
        }

        SeekBarView chargetimeout = new SeekBarView();
        chargetimeout.setTitle(getString(R.string.charge_timeout));
        chargetimeout.setSummary(getString(R.string.charge_timeout_summary));
        chargetimeout.setItems(list);
        chargetimeout.setProgress(mMisc.getChargeTimeout());
        chargetimeout.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mMisc.setChargeTimeout(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(chargetimeout);
    }

    private void powerKeySuspendInit(List<RecyclerViewItem> items) {
        SwitchView powerKeySuspend = new SwitchView();
        powerKeySuspend.setTitle(getString(R.string.power_key_suspend));
        powerKeySuspend.setSummary(getString(R.string.power_key_suspend_summary));
        powerKeySuspend.setChecked(mMisc.isPowerKeySuspendEnabled());
        powerKeySuspend.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enablePowerKeySuspend(isChecked, getActivity()));

        items.add(powerKeySuspend);
    }

    private void keyPowerModeInit(List<RecyclerViewItem> items) {
        SwitchView keyPowerMode = new SwitchView();
        keyPowerMode.setTitle(getString(R.string.key_power_mode));
        keyPowerMode.setSummary(getString(R.string.key_power_mode_summary));
        keyPowerMode.setChecked(mMisc.isKeyPowerModeEnabled());
        keyPowerMode.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableKeyPowerMode(isChecked, getActivity()));

        items.add(keyPowerMode);
    }

    private void chargingModeInit(List<RecyclerViewItem> items) {
        SwitchView chargingMode = new SwitchView();
        chargingMode.setTitle(getString(R.string.charging_mode));
        chargingMode.setSummary(getString(R.string.charging_mode_summary));
        chargingMode.setChecked(mMisc.isChargingModeEnabled());
        chargingMode.addOnSwitchListener((switchView, isChecked)
                -> mMisc.enableChargingMode(isChecked, getActivity()));

        items.add(chargingMode);
    }

    private void areaInit(List<RecyclerViewItem> items) {
        CardView areaCard = new CardView(getActivity());
        areaCard.setTitle(getString(R.string.area));

        if (mDt2s.hasWidth()) {
            final int w = getResources().getDisplayMetrics().widthPixels;
            SeekBarView width = new SeekBarView();
            width.setTitle(getString(R.string.width));
            width.setUnit(getString(R.string.px));
            width.setMax(w);
            width.setMin(Math.round(w / 28.8f));
            width.setProgress(mDt2s.getWidth() - Math.round(w / 28.8f));
            width.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mDt2s.setWidth(position + Math.round(w / 28.8f), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            areaCard.addItem(width);
        }

        if (mDt2s.hasHeight()) {
            final int h = getResources().getDisplayMetrics().heightPixels;
            SeekBarView height = new SeekBarView();
            height.setTitle(getString(R.string.height));
            height.setUnit(getString(R.string.px));
            height.setMax(h);
            height.setMin(Math.round(h / 51.2f));
            height.setProgress(mDt2s.getHeight() - Math.round(h / 51.2f));
            height.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mDt2s.setHeight(position + Math.round(h / 51.2f), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            areaCard.addItem(height);
        }

        if (areaCard.size() > 0) {
            items.add(areaCard);
        }
    }

    private void vibrationInit(List<RecyclerViewItem> items) {
        if (mMisc.hasVibration()) {
            SwitchView vibration = new SwitchView();
            vibration.setSummary(getString(R.string.vibration));
            vibration.setChecked(mMisc.isVibrationEnabled());
            vibration.addOnSwitchListener((switchView, isChecked)
                    -> mMisc.enableVibration(isChecked, getActivity()));

            items.add(vibration);
        }

        if (mMisc.hasVibVibration()) {
            SeekBarView vibVibration = new SeekBarView();
            vibVibration.setTitle(getString(R.string.vibration_strength));
            vibVibration.setUnit("%");
            vibVibration.setMax(90);
            vibVibration.setProgress(mMisc.getVibVibration());
            vibVibration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mMisc.setVibVibration(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(vibVibration);
        }
    }

    private void gloveInit(List<RecyclerViewItem> items) {
        CardView gloveCard = new CardView(getActivity());
        AtomicBoolean isPrevGloveEnabled = new AtomicBoolean(AppSettings.getGloveModeEnabled(getActivity()));
        gloveCard.setTitle(getString(R.string.glove_mode));

        SwitchView gloveSwitch = new SwitchView();
        gloveSwitch.setSummary(getString(R.string.glove_mode_summary));

        // Update switch
        gloveSwitch.setChecked(isPrevGloveEnabled.get());

        gloveSwitch.addOnSwitchListener((switchView, isChecked) -> {
            if (TspCmd.setGloveMode(isChecked ? 1 : 0, getActivity())) {
                isPrevGloveEnabled.set(isChecked);
                AppSettings.setGloveModeEnabled(isChecked, getActivity());
            } else {
                gloveSwitch.setChecked(isPrevGloveEnabled.get());
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        });

        gloveCard.addItem(gloveSwitch);
        items.add(gloveCard);
    }

    private void cmdInit(List<RecyclerViewItem> items) {
        CardView pressureCard = new CardView(getActivity());
        pressureCard.setTitle(getString(R.string.home_3d_button));

        if (TspCmd.hasControl()) {
            SwitchView pressureControl = new SwitchView();
            pressureControl.setSummary(getString(R.string.pressure_button));
            pressureControl.setChecked(TspCmd.getControl());
            pressureControl.addOnSwitchListener((switchView, isChecked)
                    -> TspCmd.setControl(isChecked, getActivity()));

            pressureCard.addItem(pressureControl);
        }

        if (TspCmd.hasLevel()) {
            SeekBarView pressureLevel = new SeekBarView();
            pressureLevel.setTitle(getString(R.string.pressure_level));
            pressureLevel.setMax(5);
			pressureLevel.setMin(1);
            pressureLevel.setProgress(TspCmd.getLevel() - 1);
            pressureLevel.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    TspCmd.setLevel(position + 1, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            pressureCard.addItem(pressureLevel);
        }

        if (TspCmd.hasIntensity()) {
            SeekBarView pressureIntensity = new SeekBarView();
            pressureIntensity.setTitle(getString(R.string.vibration_intensity));
            pressureIntensity.setMax(10000);
			pressureIntensity.setMin(0);
            pressureIntensity.setProgress(TspCmd.getIntensity());
            pressureIntensity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    TspCmd.setIntensity(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            pressureCard.addItem(pressureIntensity);
        }

        items.add(pressureCard);
    }
}
