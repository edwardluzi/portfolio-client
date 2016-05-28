package org.goldenroute.portfolioclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.goldenroute.portfolioclient.IntentConstants;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Activity mActivity;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public TopExceptionHandler(Activity activity) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        mActivity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        StringBuilder report = new StringBuilder();

        collectSystemInfo(report);
        collectDeviceInfo(report);
        collectPackageInfo(report);
        collectStackTrace(ex, report);
        collectLogs(report);

        try {
            FileOutputStream trace = mActivity.openFileOutput(
                    "stack.trace", Context.MODE_PRIVATE);
            trace.write(report.toString().getBytes());
            trace.close();
            clearLogs();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        mDefaultHandler.uncaughtException(thread, ex);
    }

    private StringBuilder collectStackTrace(Throwable ex, StringBuilder report) {
        report.append("--------- Stack trace ---------");
        report.append(IntentConstants.NEW_LINE);

        StackTraceElement[] elements = ex.getStackTrace();
        for (StackTraceElement element : elements) {
            report.append("    ");
            report.append(element.toString());
            report.append(IntentConstants.NEW_LINE);
        }
        report.append("-------------------------------");
        report.append(IntentConstants.NEW_LINE);
        report.append("--------- Cause ---------------");
        report.append(IntentConstants.NEW_LINE);

        Throwable cause = ex.getCause();
        if (cause != null) {
            report.append(cause.toString());
            report.append(IntentConstants.NEW_LINE);
            elements = cause.getStackTrace();
            for (StackTraceElement element : elements) {
                report.append("    ");
                report.append(element.toString());
                report.append(IntentConstants.NEW_LINE);
            }
        }
        report.append("-------------------------------");
        report.append(IntentConstants.NEW_LINE);

        return report;
    }

    private void collectSystemInfo(StringBuilder report) {
        report.append("Android version: ");
        report.append(Build.VERSION.SDK_INT);
        report.append(IntentConstants.NEW_LINE);
    }

    private void collectDeviceInfo(StringBuilder report) {
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER)) {
            model = Build.MANUFACTURER + " " + model;
        }
        report.append("Device:         ");
        report.append(model);
        report.append(IntentConstants.NEW_LINE);
    }

    private void collectPackageInfo(StringBuilder report) {
        PackageInfo packageInfo;
        try {
            packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            report.append("App version:    ");
            report.append(packageInfo.versionName);
            report.append(IntentConstants.NEW_LINE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void collectLogs(StringBuilder report) {
        Process process;
        try {
            process = Runtime.getRuntime().exec("logcat -d -v time");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                report.append(line);
                report.append(IntentConstants.NEW_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearLogs() {
        try {
           Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

