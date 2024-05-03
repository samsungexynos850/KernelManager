package com.kawa.kernelmanager.utils.kernel.wake;

import android.content.Context;

import com.kawa.kernelmanager.fragments.ApplyOnBootFragment;
import com.kawa.kernelmanager.utils.Log;
import com.kawa.kernelmanager.utils.Utils;
import com.kawa.kernelmanager.utils.root.Control;
import com.kawa.kernelmanager.utils.root.RootUtils;

public class TspCmd {

    private static final String CMD = "/sys/class/sec/tsp/cmd";
    private static final String LIST = "/sys/class/sec/tsp/cmd_list";
    private static final String RESULT = "/sys/class/sec/tsp/cmd_result";
    private static final String PRESSUREENABLE = "/sys/class/sec/tsp/pressure_enable";
    private static final String FORCEINTENSITY = "/sys/class/timed_output/vibrator/force_touch_intensity";

    public static boolean setGloveMode(int mode, Context context) {
            RootUtils.SU su = new RootUtils.SU(true, false);
            boolean ret;
            run(Control.write("glove_mode," + mode, CMD) + " && settings put system auto_adjust_touch " + mode, CMD, context);
            ret = String.valueOf(su.runCommand("echo 'glove_mode," + mode + "' > " + CMD + " && cat " + RESULT)).contains("glove_mode," + mode + ":OK");
            su.close();
            return ret;
    }
    
    public static int getIntensity() {
        return Utils.strToInt(Utils.readFile(FORCEINTENSITY).replaceAll("[^0-9]", ""));
    }

    public static void setIntensity(int value, Context context) {
        run(Control.write(String.valueOf(value), FORCEINTENSITY), FORCEINTENSITY, context);
    }

    public static boolean hasIntensity() {
        return Utils.existFile(FORCEINTENSITY);
    }

    public static boolean getControl() {
        return Utils.strToInt(Utils.readFile(PRESSUREENABLE)) == 1;
    }

    public static void setControl(boolean value, Context context) {
        run(Control.write(String.valueOf(value ? 1 : 0), PRESSUREENABLE), PRESSUREENABLE, context);
    }

    public static boolean hasControl() {
        return Utils.existFile(PRESSUREENABLE);
    }

    public static int getLevel() {
        RootUtils.SU su = new RootUtils.SU(true, false);
        return Utils.strToInt(String.valueOf(su.runCommand("echo 'get_pressure_user_level' > /sys/class/sec/tsp/cmd && cat /sys/class/sec/tsp/cmd_result")).replaceAll("[^0-9]", ""));
    }

    public static void setLevel(int value, Context context) {
        run(Control.write("set_pressure_user_level," + String.valueOf(value), CMD), CMD, context);
    }

    public static boolean hasLevel() {
        return Utils.readFile(LIST).contains("set_pressure_user_level");
    }

    public static boolean hasGlove() {
        return Utils.readFile(LIST).contains("glove_mode");
    }

    public static boolean supported() {
        return hasIntensity() || hasControl() || hasLevel();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.WAKE, id, context);
    }
}
