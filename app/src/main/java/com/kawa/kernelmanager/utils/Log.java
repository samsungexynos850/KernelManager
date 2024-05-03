/*
 * Copyright (C) 2018 Willi Ye <williye97@gmail.com>
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
package com.kawa.kernelmanager.utils;

/**
 * Created by willi on 22.03.18.
 */

public class Log {

    private static final String TAG = "xxTR Manager";

    public static void i(String message) {
        android.util.Log.i(TAG, getMessage(message));
    }

    public static void e(String message) {
        android.util.Log.e(TAG, getMessage(message));
    }

    private static String getMessage(String message) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[4];
        String className = element.getClassName();

        return Utils.strFormat("[%s][%s] %s",
                className.substring(className.lastIndexOf(".") + 1),
                element.getMethodName(),
                message);
    }

}
