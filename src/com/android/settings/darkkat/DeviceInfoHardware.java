/*
 * Copyright (C) 2016 DarkKat
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

package com.android.settings.darkkat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;

import com.android.internal.logging.MetricsLogger;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DeviceInfoHardware extends SettingsPreferenceFragment {

    private static final String FILENAME_MSV          = "/sys/board_properties/soc/msv";
    private static final String FILENAME_PROC_MEMINFO = "/proc/meminfo";
    private static final String FILENAME_PROC_CPUINFO = "/proc/cpuinfo";

    private static final String PREF_DEVICE_MODEL  = "device_model";
    private static final String PREF_DEVICE_CPU    = "device_cpu";
    private static final String PREF_DEVICE_MEMORY = "device_memory";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.device_info_hardware);

        setStringSummary(PREF_DEVICE_MODEL, Build.MODEL + getMsvSuffix());
        setStringSummary(PREF_DEVICE_MODEL, Build.MODEL);

        final String cpuInfo = getCPUInfo();
        String memInfo = getMemInfo();

        if (cpuInfo != null) {
            setStringSummary(PREF_DEVICE_CPU, cpuInfo);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_DEVICE_CPU));
        }

        if (memInfo != null) {
            setStringSummary(PREF_DEVICE_MEMORY, memInfo);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_DEVICE_MEMORY));
        }
    }

    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }

    private void setValueSummary(String preference, String property) {
        try {
            findPreference(preference).setSummary(
                    SystemProperties.get(property,
                            getResources().getString(R.string.device_info_default)));
        } catch (RuntimeException e) {
            // No recovery
        }
    }

    /**
     * Reads a line from the specified file.
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    /**
     * Returns " (ENGINEERING)" if the msv file has a zero value, else returns "".
     * @return a string to append to the model number description.
     */
    private String getMsvSuffix() {
        // Production devices should have a non-zero value. If we can't read it, assume it's a
        // production device so that we don't accidentally show that it's an ENGINEERING device.
        try {
            String msv = readLine(FILENAME_MSV);
            // Parse as a hex number. If it evaluates to a zero, then it's an engineering build.
            if (Long.parseLong(msv, 16) == 0) {
                return " (ENGINEERING)";
            }
        } catch (IOException ioe) {
            // Fail quietly, as the file may not exist on some devices.
        } catch (NumberFormatException nfe) {
            // Fail quietly, returning empty string should be sufficient
        }
        return "";
    }

    private static String getMemInfo() {
        String result = null;
        BufferedReader reader = null;

        try {
            /* /proc/meminfo entries follow this format:
             * MemTotal:         362096 kB
             * MemFree:           29144 kB
             * Buffers:            5236 kB
             * Cached:            81652 kB
             */
            String firstLine = readLine(FILENAME_PROC_MEMINFO);
            if (firstLine != null) {
                String parts[] = firstLine.split("\\s+");
                if (parts.length == 3) {
                    result = Long.parseLong(parts[1])/1024 + " MB";
                }
            }
        } catch (IOException e) {}

        return result;
    }

    private static String getCPUInfo() {
        String result = null;
        int coreCount = 0;

        try {
            /* The expected /proc/cpuinfo output is as follows:
             * Processor	: ARMv7 Processor rev 2 (v7l)
             * BogoMIPS	: 272.62
             * BogoMIPS	: 272.62
             *
             * On kernel 3.10 this changed, it is now the last
             * line. So let's read the whole thing, search
             * specifically for "Processor" or "model name"
             * and retain the old
             * "first line" as fallback.
             * Also, use "processor : <id>" to count cores
             */
            BufferedReader ci = new BufferedReader(new FileReader(FILENAME_PROC_CPUINFO));
            String firstLine = ci.readLine();
            String latestLine = firstLine;
            while (latestLine != null) {
                if (latestLine.startsWith("Processor")
                    || latestLine.startsWith("model name"))
                  result = latestLine.split(":")[1].trim();
                if (latestLine.startsWith("processor"))
                  coreCount++;
                latestLine = ci.readLine();
            }
            if (result == null && firstLine != null) {
                result = firstLine.split(":")[1].trim();
            }
            /* Don't do this. hotplug throws off the count
            if (coreCount > 1) {
                result = result + " (x" + coreCount + ")";
            }
            */
            ci.close();
        } catch (IOException e) {}

        return result;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DEVICEINFO;
    }
}
