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
package com.kawa.kernelmanager.utils.tools;

import com.kawa.kernelmanager.utils.Utils;
import com.kawa.kernelmanager.utils.root.RootFile;
import com.kawa.kernelmanager.utils.root.RootUtils;

import java.util.List;

/**
 * Created by willi on 16.07.16.
 */
public class Initd {
    private static String sysblock;

    static {
        try {
            sysblock = RootUtils.isSAR() ? "/" : "/system";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private static String INITD;
	static {
		try{
            INITD = RootUtils.isSAR() ? "/vendor/etc/init.d" : "/system/etc/init.d";
        } catch (Exception e){
            e.printStackTrace();
        }
	}

//    private static final String INITD = "/system/etc/init.d";

    public static void write(String file, String text) {
        RootUtils.mount(true, sysblock);
        RootFile f = new RootFile(INITD + "/" + file);
        f.write(text, false);
        RootUtils.chmod(INITD + "/" + file, "755");
        RootUtils.mount(false, sysblock);
    }

    public static void delete(String file) {
        RootUtils.mount(true, sysblock);
        RootFile f = new RootFile(INITD + "/" + file);
        f.delete();
        RootUtils.mount(false, sysblock);
    }

    public static String execute(String file) {
        RootUtils.chmod(INITD + "/" + file, "755");
        return RootUtils.runCommand(INITD + "/" + file);
    }

    public static String read(String file) {
        return Utils.readFile(INITD + "/" + file);
    }

    public static List<String> list() {
        RootFile file = new RootFile(INITD);
        if (!file.exists()) {
            RootUtils.mount(true, sysblock);
            file.mkdir();
            RootUtils.mount(false, sysblock);
        }
        return file.list();
    }

}