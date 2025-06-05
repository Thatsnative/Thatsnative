package com.example.epic.util.log;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * This class is an utility class that configures the application log.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
public final class ApplicationLog {
    /**
     * Private constructor.
     */
    private ApplicationLog() {

    }


    private static boolean isApplicationDebuggable(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
