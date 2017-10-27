package com.microsoft.azure.mobile.react.crashes;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.microsoft.azure.mobile.crashes.model.ErrorReport;
import com.microsoft.azure.mobile.ingestion.models.Device;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Collection;

class RNCrashesUtils {
    private static final String LOG_TAG = "RNCrashes";

    static void logError(String message) {
        Log.e(LOG_TAG, message);
    }

    static void logInfo(String message) {
        Log.i(LOG_TAG, message);
    }

    static void logDebug(String message) {
        Log.d(LOG_TAG, message);
    }

    static WritableMap convertErrorReportToWritableMap(ErrorReport errorReport) throws JSONException {
        if (errorReport == null) {
            return Arguments.createMap();
        }
        WritableMap errorReportMap = Arguments.createMap();
        errorReportMap.putString("id", errorReport.getId());
        errorReportMap.putString("threadName", errorReport.getThreadName());
        errorReportMap.putString("appErrorTime", "" + errorReport.getAppErrorTime().getTime());
        errorReportMap.putString("appStartTime", "" + errorReport.getAppStartTime().getTime());
        errorReportMap.putString("exception", Log.getStackTraceString(errorReport.getThrowable()));
        //noinspection ThrowableResultOfMethodCallIgnored
        errorReportMap.putString("exceptionReason", errorReport.getThrowable().getMessage());

        Device deviceInfo = errorReport.getDevice();
        JSONStringer jsonStringer = new JSONStringer();
        jsonStringer.object();
        deviceInfo.write(jsonStringer);
        jsonStringer.endObject();
        JSONObject deviceInfoJson = new JSONObject(jsonStringer.toString());
        WritableMap deviceInfoMap = RNUtils.convertJsonObjectToWritableMap(deviceInfoJson);

        errorReportMap.putMap("device", deviceInfoMap);
        return errorReportMap;
    }

    private static WritableArray convertErrorReportsToWritableArray(Collection<ErrorReport> errorReports) throws JSONException {
        WritableArray errorReportsArray = Arguments.createArray();

        for (ErrorReport report : errorReports) {
            errorReportsArray.pushMap(convertErrorReportToWritableMap(report));
        }

        return errorReportsArray;
    }

    static WritableArray convertErrorReportsToWritableArrayOrEmpty(Collection<ErrorReport> errorReports) {
        try {
            return convertErrorReportsToWritableArray(errorReports);
        } catch (JSONException e) {
            logError("Unable to serialize crash reports");
            logError(Log.getStackTraceString(e));
            return Arguments.createArray();
        }
    }

    static WritableMap convertErrorReportToWritableMapOrEmpty(ErrorReport errorReport) {
        try {
            return convertErrorReportToWritableMap(errorReport);
        } catch (JSONException e) {
            logError("Unable to serialize crash report");
            logError(Log.getStackTraceString(e));
            return Arguments.createMap();
        }
    }
}
