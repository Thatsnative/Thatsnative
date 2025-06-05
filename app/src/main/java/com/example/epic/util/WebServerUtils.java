/*
 * Copyright (C) 2011-2012 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This file is part of AdAway.
 *
 * AdAway is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdAway is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdAway.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.example.epic.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.epic.helper.PreferenceHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import timber.log.Timber;

/**
 * This class is an utility class to control web server execution.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
public class WebServerUtils {
    public static final String TEST_URL = "https://localhost/internal-test";
    private static final String WEB_SERVER_EXECUTABLE = "webserver";
    private static final String LOCALHOST_CERTIFICATE = "localhost-2410.crt";
    private static final String LOCALHOST_CERTIFICATE_KEY = "localhost-2410.key";

    /**
     * Start the web server in new thread with RootTools
     *
     * @param context The application context.
     */
    public static void startWebServer(Context context) {
        Timber.d("Starting web server…");

        Path resourcePath = context.getFilesDir().toPath().resolve(WEB_SERVER_EXECUTABLE);
        inflateResources(context, resourcePath);

        String parameters = "--resources " + resourcePath.toAbsolutePath() +
                (PreferenceHelper.getWebServerIcon(context) ? " --icon" : "") +
                " > /dev/null 2>&1";
//        runBundledExecutable(context, WEB_SERVER_EXECUTABLE, parameters);
    }

    private static void inflateResources(Context context, Path target) {
        AssetManager assetManager = context.getAssets();
        try {
            inflateResource(assetManager, LOCALHOST_CERTIFICATE, target);
            inflateResource(assetManager, LOCALHOST_CERTIFICATE_KEY, target);
            inflateResource(assetManager, "icon.svg", target);
            inflateResource(assetManager, "test.html", target);
        } catch (IOException e) {
            Timber.w(e, "Failed to inflate web server resources.");
        }
    }

    private static void inflateResource(AssetManager assetManager, String resource, Path target) throws IOException {
        if (!Files.isDirectory(target)) {
            Files.createDirectories(target);
        }
        Path targetFile = target.resolve(resource);
        if (!Files.isRegularFile(targetFile)) {
            Files.copy(assetManager.open(resource), targetFile);
        }
    }
}
