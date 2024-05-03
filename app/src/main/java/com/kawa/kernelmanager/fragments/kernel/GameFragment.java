package com.kawa.kernelmanager.fragments.kernel;

import androidx.appcompat.app.AlertDialog;

import com.kawa.kernelmanager.R;
import com.kawa.kernelmanager.fragments.ApplyOnBootFragment;
import com.kawa.kernelmanager.fragments.DescriptionFragment;
import com.kawa.kernelmanager.fragments.recyclerview.RecyclerViewFragment;
import com.kawa.kernelmanager.utils.AppSettings;
import com.kawa.kernelmanager.utils.Log;
import com.kawa.kernelmanager.utils.PackageInfo;
import com.kawa.kernelmanager.utils.Utils;
import com.kawa.kernelmanager.utils.kernel.cpu.CPUFreq;
import com.kawa.kernelmanager.utils.kernel.game.GameControl;
import com.kawa.kernelmanager.utils.kernel.gpu.GPUFreqExynos;
import com.kawa.kernelmanager.views.recyclerview.ButtonView2;
import com.kawa.kernelmanager.views.recyclerview.CardView;
import com.kawa.kernelmanager.views.recyclerview.DescriptionView;
import com.kawa.kernelmanager.views.recyclerview.GenericSelectView;
import com.kawa.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.kawa.kernelmanager.views.recyclerview.SeekBarView;
import com.kawa.kernelmanager.views.recyclerview.SelectView;
import com.kawa.kernelmanager.views.recyclerview.StatsView;
import com.kawa.kernelmanager.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameFragment extends RecyclerViewFragment {
    private GameControl mGameControl;
    private GPUFreqExynos mGPUFreqExynos;
    private CPUFreq mCPUFreq;
    private PackageInfo mPackageInfo;
    private StatsView status;

    private List<SwitchView> mPACKAGES = new ArrayList<>();
    private int mPackageInfoMode = 1;

    @Override
    protected void init() {
        super.init();

        mGameControl = GameControl.getInstance();
        mGPUFreqExynos = GPUFreqExynos.getInstance();
        mCPUFreq = CPUFreq.getInstance(getActivity());
        mPackageInfo = PackageInfo.getInstance(getActivity());

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        if (GameControl.supported()) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.gameControl),getString(R.string.gameControl_Summary)));
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (GameControl.supported()) {
            gameControlInfo(items);
            gameControlInit(items);
            if (mGameControl.hasThermalInit()) {
                throttleControlInit(items);
            }
            if(mGameControl.hasGamePackages()) {
                gamePackagesInit(items);
            }
        }
    }

    private void setStatusSummary() {
        if (mGameControl.hasStatus()) {
            String[] lines = mGameControl.getStatus().split("\n");
            String summary = lines[0].trim() + " " + getString(R.string.running_games);
            if (GameControl.isEnabledAlwaysOn())
                summary += " (" + getString(R.string.force_enabled) + "]";
            for (String str : lines) {
                if (str.length() > 2) {
                    summary += "\n" + Utils.readFile("/proc/" + str.trim() + "/cmdline").trim();
                } else {
                    summary += "\n";
                }
            }
            status.setStat(summary);
        } else if (mGameControl.hasAlwaysOn()) {
            if (GameControl.isEnabledAlwaysOn())
                status.setStat(getString(R.string.force_enabled));
            else
                status.setStat(getString(R.string.unknown));
        }
    }

    private void gameControlInfo(List<RecyclerViewItem> items) {
        if (mGameControl.hasStatus() || mGameControl.hasAlwaysOn()) {
            status = new StatsView();
            status.setTitle(getString(R.string.gameControl) + " " + getString(R.string.status));
            status.setFullSpan(true);
            setStatusSummary();
            items.add(status);
        }
    }

    private void gameControlInit(List<RecyclerViewItem> items) {
        CardView gameControlCard = new CardView(getActivity());
		if(mGameControl.hasVersion())
            gameControlCard.setTitle(getString(R.string.gameControl) + " - v" + mGameControl.getVersion());
        else
            gameControlCard.setTitle(getString(R.string.gameControl));

        if(mGameControl.hasAlwaysOn()){
            SwitchView alwaysOn = new SwitchView();
            alwaysOn.setTitle(getString(R.string.gameControl_always_on));
            alwaysOn.setSummary(getString(R.string.gameControl_always_on_desc));
            alwaysOn.setChecked(mGameControl.isEnabledAlwaysOn());
            alwaysOn.addOnSwitchListener((switchView, isChecked) ->
                    mGameControl.enableAlwaysOn(isChecked, getActivity())
            );
            gameControlCard.addItem(alwaysOn);
        }

        if(mGameControl.hasBatteryIdle()){
            SwitchView batteryidle = new SwitchView();
            batteryidle.setTitle(getString(R.string.gameControl_batteryidle));
            batteryidle.setSummary(getString(R.string.gameControl_batteryidle_desc));
            batteryidle.setChecked(mGameControl.isEnabledBatteryIdle());
            batteryidle.addOnSwitchListener((switchView, isChecked) ->
                    mGameControl.enableBatteryIdle(isChecked, getActivity())
            );
            gameControlCard.addItem(batteryidle);
        }

        if(mCPUFreq.getFreqs() != null) {

            if (mGameControl.hasBIGMax()) {
                SelectView BIGMax = new SelectView();
                BIGMax.setSummary(getString(R.string.gameControl_BIG_max));
                BIGMax.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
                BIGMax.setOnItemSelected((selectView, position, item)
                        -> mGameControl.setBIGMax(mCPUFreq.getFreqs().get(position), getActivity()));

                gameControlCard.addItem(BIGMax);

                BIGMax.setItem((mGameControl.getBIGMax() / 1000) + getString(R.string.mhz));
            }

            if (mGameControl.hasBIGMin()) {
                SelectView BIGMin = new SelectView();
                BIGMin.setSummary(getString(R.string.gameControl_BIG_min));
                BIGMin.setItems(mCPUFreq.getAdjustedFreq(getActivity()));
                BIGMin.setOnItemSelected((selectView, position, item)
                        -> mGameControl.setBIGMin(mCPUFreq.getFreqs().get(position), getActivity()));

                gameControlCard.addItem(BIGMin);

                BIGMin.setItem((mGameControl.getBIGMin() / 1000) + getString(R.string.mhz));
            }

            if (mCPUFreq.isBigLITTLE()) {
                if (mCPUFreq.hasMidCpu()) {

                    if (mGameControl.hasMIDDLEMax()) {
                        SelectView MIDDLEMax = new SelectView();
                        MIDDLEMax.setSummary(getString(R.string.gameControl_MIDDLE_max));
                        MIDDLEMax.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                        MIDDLEMax.setOnItemSelected((selectView, position, item)
                                -> mGameControl.setMIDDLEMax(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position), getActivity()));

                        gameControlCard.addItem(MIDDLEMax);

                        MIDDLEMax.setItem((mGameControl.getMIDDLEMax() / 1000) + getString(R.string.mhz));
                    }

                    if (mGameControl.hasMIDDLEMin()) {
                        SelectView MIDDLEMin = new SelectView();
                        MIDDLEMin.setSummary(getString(R.string.gameControl_MIDDLE_min));
                        MIDDLEMin.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getMidCpu(), getActivity()));
                        MIDDLEMin.setOnItemSelected((selectView, position, item)
                                -> mGameControl.setMIDDLEMin(mCPUFreq.getFreqs(mCPUFreq.getMidCpu()).get(position), getActivity()));

                        gameControlCard.addItem(MIDDLEMin);

                        MIDDLEMin.setItem((mGameControl.getMIDDLEMin() / 1000) + getString(R.string.mhz));
                    }
                }

                if (mGameControl.hasLITTLEMax()) {
                    SelectView LITTLEMax = new SelectView();
                    LITTLEMax.setSummary(getString(R.string.gameControl_LITTLE_max));
                    LITTLEMax.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                    LITTLEMax.setOnItemSelected((selectView, position, item)
                            -> mGameControl.setLITTLEMax(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));

                    gameControlCard.addItem(LITTLEMax);

                    LITTLEMax.setItem((mGameControl.getLITTLEMax() / 1000) + getString(R.string.mhz));
                }

                if (mGameControl.hasLITTLEMin()) {
                    SelectView LITTLEMin = new SelectView();
                    LITTLEMin.setSummary(getString(R.string.gameControl_LITTLE_min));
                    LITTLEMin.setItems(mCPUFreq.getAdjustedFreq(mCPUFreq.getLITTLECpu(), getActivity()));
                    LITTLEMin.setOnItemSelected((selectView, position, item)
                            -> mGameControl.setLITTLEMin(mCPUFreq.getFreqs(mCPUFreq.getLITTLECpu()).get(position), getActivity()));

                    gameControlCard.addItem(LITTLEMin);

                    LITTLEMin.setItem((mGameControl.getLITTLEMin() / 1000) + getString(R.string.mhz));
                }
            }
        }

        if(mGPUFreqExynos.getAvailableFreqs() != null) {
            if(mGameControl.hasGPUMax()) {
                SelectView GPUMax = new SelectView();
                GPUMax.setSummary(getString(R.string.gameControl_GPU_max));
                GPUMax.setItems(mGPUFreqExynos.getAdjustedFreqs(getActivity()));
                GPUMax.setOnItemSelected((selectView, position, item)
                        -> mGameControl.setGPUMax(mGPUFreqExynos.getAvailableFreqs().get(position), getActivity()));

                gameControlCard.addItem(GPUMax);

                GPUMax.setItem((mGameControl.getGPUMax() / 1000) + getString(R.string.mhz));
            }

            if(mGameControl.hasGPUMin()) {
                SelectView GPUMin = new SelectView();
                GPUMin.setSummary(getString(R.string.gameControl_GPU_min));
                GPUMin.setItems(mGPUFreqExynos.getAdjustedFreqs(getActivity()));
                GPUMin.setOnItemSelected((selectView, position, item)
                        -> mGameControl.setGPUMin(mGPUFreqExynos.getAvailableFreqs().get(position), getActivity()));

                gameControlCard.addItem(GPUMin);

                GPUMin.setItem((mGameControl.getGPUMin() / 1000) + getString(R.string.mhz));
            }
        }

        if(mGameControl.hasMIFMin()) {
            GenericSelectView minMIFfreq = new GenericSelectView();
            minMIFfreq.setSummary(getString(R.string.gameControl_MIF_min));
            minMIFfreq.setValue(String.valueOf(mGameControl.getMIFMin()));
            minMIFfreq.setValueRaw(minMIFfreq.getValue());
            minMIFfreq.setOnGenericValueListener((genericSelectView, value)
                    -> mGameControl.setMIFMin(Integer.parseInt(value), getActivity()));

            gameControlCard.addItem(minMIFfreq);
        }

        if(mGameControl.hasINTMin()) {
            GenericSelectView minINTfreq = new GenericSelectView();
            minINTfreq.setSummary(getString(R.string.gameControl_INT_min));
            minINTfreq.setValue(String.valueOf(mGameControl.getINTMin()));
            minINTfreq.setValueRaw(minINTfreq.getValue());
            minINTfreq.setOnGenericValueListener((genericSelectView, value)
                    -> mGameControl.setINTMin(Integer.parseInt(value), getActivity()));

            gameControlCard.addItem(minINTfreq);
        }

        if (mGameControl.hasLittleFreq()) {
            GenericSelectView customLittlefreq = new GenericSelectView();
            customLittlefreq.setSummary(getString(R.string.gameControl_custom_little_freq));
            customLittlefreq.setValue(String.valueOf(mGameControl.getLittleFreq()));
            customLittlefreq.setValueRaw(customLittlefreq.getValue());
            customLittlefreq.setOnGenericValueListener((genericSelectView, value)
                    -> mGameControl.setLittleFreq(Integer.parseInt(value), getActivity()));

            gameControlCard.addItem(customLittlefreq);
        }

        if (mGameControl.hasLittleVolt()) {
            GenericSelectView customLittleVolt = new GenericSelectView();
            customLittleVolt.setSummary(getString(R.string.gameControl_custom_little_volt));
            customLittleVolt.setValue(String.valueOf(mGameControl.getLittleVolt()));
            customLittleVolt.setValueRaw(customLittleVolt.getValue());
            customLittleVolt.setOnGenericValueListener((genericSelectView, value)
                    -> mGameControl.setLittleVolt(Integer.parseInt(value), getActivity()));

            gameControlCard.addItem(customLittleVolt);
        }

        if (mGameControl.hasBigFreq()) {
            GenericSelectView customBigfreq = new GenericSelectView();
            customBigfreq.setSummary(getString(R.string.gameControl_custom_Big_freq));
            customBigfreq.setValue(String.valueOf(mGameControl.getBigFreq()));
            customBigfreq.setValueRaw(customBigfreq.getValue());
            customBigfreq.setOnGenericValueListener((genericSelectView, value)
                    -> mGameControl.setBigFreq(Integer.parseInt(value), getActivity()));

            gameControlCard.addItem(customBigfreq);
        }

        if (mGameControl.hasBigVolt()) {
            GenericSelectView customBigVolt = new GenericSelectView();
            customBigVolt.setSummary(getString(R.string.gameControl_custom_Big_volt));
            customBigVolt.setValue(String.valueOf(mGameControl.getBigVolt()));
            customBigVolt.setValueRaw(customBigVolt.getValue());
            customBigVolt.setOnGenericValueListener((genericSelectView, value)
                    -> mGameControl.setBigVolt(Integer.parseInt(value), getActivity()));

            gameControlCard.addItem(customBigVolt);
        }

        if (mGameControl.hasGpuFreq()) {
            GenericSelectView customGpufreq = new GenericSelectView();
            customGpufreq.setSummary(getString(R.string.gameControl_custom_Gpu_freq));
            customGpufreq.setValue(String.valueOf(mGameControl.getGpuFreq()));
            customGpufreq.setValueRaw(customGpufreq.getValue());
            customGpufreq.setOnGenericValueListener((genericSelectView, value)
                    -> mGameControl.setGpuFreq(Integer.parseInt(value), getActivity()));

            gameControlCard.addItem(customGpufreq);
        }

        if (mGameControl.hasGpuVolt()) {
            GenericSelectView customGpuVolt = new GenericSelectView();
            customGpuVolt.setSummary(getString(R.string.gameControl_custom_Gpu_volt));
            customGpuVolt.setValue(String.valueOf(mGameControl.getGpuVolt()));
            customGpuVolt.setValueRaw(customGpuVolt.getValue());
            customGpuVolt.setOnGenericValueListener((genericSelectView, value)
                    -> mGameControl.setGpuVolt(Integer.parseInt(value), getActivity()));

            gameControlCard.addItem(customGpuVolt);
        }

        items.add(gameControlCard);
    }

    private void throttleControlInit(List<RecyclerViewItem> items) {
        CardView throttleCard = new CardView(getActivity());
        throttleCard.setTitle(getString(R.string.gameControl_throttle_control));

        if(mGameControl.hasThermalBypass()){
            SwitchView thermalBypass = new SwitchView();
            thermalBypass.setTitle(getString(R.string.gameControl_thermal_bypass));
            thermalBypass.setSummary(getString(R.string.gameControl_thermal_bypass_desc));
            thermalBypass.setChecked(mGameControl.isEnabledThermalBypass());
            thermalBypass.addOnSwitchListener((switchView, isChecked) ->
                    mGameControl.enableThermalBypass(isChecked, getActivity())
            );
            throttleCard.addItem(thermalBypass);
        }

        items.add(throttleCard);
    }

    private void gamePackagesInit (List<RecyclerViewItem> items){
        final CardView CardPackages = new CardView(getActivity());
        CardPackages.setTitle(getString(R.string.gameControl_packages));

        CardGamePackagesInit(CardPackages);

        if (CardPackages.size() > 0) {
            items.add(CardPackages);
        }
    }

    private void CardGamePackagesInit(final CardView card) {
        card.clearItems();
        mPACKAGES.clear();
        List<String> enabledAppList = new ArrayList<>(Arrays.asList(GameControl.getGamePackages().split("\n")));

        mPackageInfoMode = AppSettings.getInt("game_control_show_packages_filter", 1, getActivity());

        SeekBarView mode = new SeekBarView();
        mode.setSummary(getString(R.string.gameControl_packages_mode));
        mode.setMax(11);
        mode.setMin(1);
        mode.setProgress(mPackageInfoMode - 1);
        mode.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mPackageInfoMode = position + 1;
                AppSettings.saveInt("game_control_show_packages_filter", mPackageInfoMode, getActivity());
                getHandler().postDelayed(() -> CardGamePackagesInit(card), 250);
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        card.addItem(mode);

        ButtonView2 reset = new ButtonView2();
        reset.setTitle(getString(R.string.reset));
        reset.setSummary(getString(R.string.reset_summary));
        reset.setButtonText(getString(R.string.reset));
        reset.setOnItemClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(getString(R.string.wkl_alert_title));
            alert.setMessage(getString(R.string.gameControl_reset_message));
            alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
            alert.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                mPackageInfoMode = 1;
                mode.setProgress(mPackageInfoMode - 1);
                AppSettings.saveInt("game_control_show_packages_filter", mPackageInfoMode, getActivity());
                mGameControl.setGamePackages("", getActivity());
                getHandler().postDelayed(() -> CardGamePackagesInit(card), 250);
            });
            alert.show();
        });
        card.addItem(reset);

        for (int i=0; i<enabledAppList.size(); i++) {
            String packageName = enabledAppList.get(i);
            SwitchView gamePackages = new SwitchView();
            String appName = mPackageInfo.getAppNameFromPackage(packageName);
            if (appName == null)
                continue;
            gamePackages.setSummary(packageName);
            gamePackages.setTitle(appName);
            gamePackages.setIcon(mPackageInfo.getIconFromPackage(packageName, getActivity()));
            gamePackages.setChecked(GameControl.checkGamePackage(packageName));
            gamePackages.addOnSwitchListener((switchView, isChecked) ->
                    mGameControl.editGamePackage(isChecked, String.valueOf(switchView.getSummary()), getActivity())
            );
            card.addItem(gamePackages);
            mPACKAGES.add(gamePackages);
        }
        List<String> adjustedAppPackages = mPackageInfo.getAdjustedAppPackages(mPackageInfoMode);
        for (int i=0; i<adjustedAppPackages.size(); i++) {
            String packageName = adjustedAppPackages.get(i);
            SwitchView gamePackages = new SwitchView();
            if (enabledAppList.contains(packageName))
                continue;
            gamePackages.setSummary(packageName);
            gamePackages.setTitle(mPackageInfo.getAppNameFromPackage(packageName));
            gamePackages.setIcon(mPackageInfo.getIconFromPackage(packageName, getActivity()));
            gamePackages.setChecked(GameControl.checkGamePackage(packageName));
            gamePackages.addOnSwitchListener((switchView, isChecked) ->
                    mGameControl.editGamePackage(isChecked, String.valueOf(switchView.getSummary()), getActivity())
            );
            card.addItem(gamePackages);
            mPACKAGES.add(gamePackages);
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (status != null)
            setStatusSummary();
    }
}
