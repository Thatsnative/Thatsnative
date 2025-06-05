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

package com.example.epic.helper;

import static com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferencesImplKt.PREF_IS_COOKIE_PROTECTION_ENABLED;
import static com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferencesImplKt.PREF_IS_MALWARE_PROTECTION_ENABLED;
import static com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferencesImplKt.PREF_IS_PRIVACY_PROTECTION_ENABLED;
import static com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferencesImplKt.PREF_IS_SOCIAL_MEDIA_PROTECTION_ENABLED;
import static com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferencesImplKt.PREF_IS_SPEED_CHARGE_ENABLED;
import static com.example.epic.sharedPrefs.mainFeaturesPrefs.MainFeaturesPreferencesImplKt.PREF_IS_YOUTUBE_PROTECTION_ENABLED;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.epic.R;
import com.example.epic.model.adblocking.AdBlockMethod;
import com.example.epic.util.Constants;
import com.example.epic.vpn.VpnStatus;

import java.util.Collections;
import java.util.Set;

public final class PreferenceHelper {
    private static SharedPreferences.OnSharedPreferenceChangeListener onProtectionChangeListener = null;
    private static SharedPreferences sharedPrefs = null;

    private PreferenceHelper() {

    }

    public static void init(Context context) {
        sharedPrefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
    }

    public static boolean getEnableIpv6(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                "pref_enable_ipv6_def",
                false
        );
    }


    public static boolean getUpdateCheckHostsDaily(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_update_check_hosts_daily_key),
                context.getResources().getBoolean(R.bool.pref_update_check_hosts_daily_def)
        );
    }

    public static boolean getAutomaticUpdateDaily(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_automatic_update_daily_key),
                context.getResources().getBoolean(R.bool.pref_automatic_update_daily_def)
        );
    }

    public static boolean getUpdateOnlyOnWifi(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_update_only_on_wifi_key),
                context.getResources().getBoolean(R.bool.pref_update_only_on_wifi_def)
        );
    }

    public static String getRedirectionIpv4(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getString(
                context.getString(R.string.pref_redirection_ipv4_key),
                context.getString(R.string.pref_redirection_ipv4_def)
        );
    }

    public static String getRedirectionIpv6(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getString(
                context.getString(R.string.pref_redirection_ipv6_key),
                context.getString(R.string.pref_redirection_ipv6_def)
        );
    }

    public static boolean getWebServerEnabled(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_webserver_enabled_key),
                context.getResources().getBoolean(R.bool.pref_webserver_enabled_def)
        );
    }

    public static boolean getWebServerIcon(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_webserver_icon_key),
                context.getResources().getBoolean(R.bool.pref_webserver_icon_def)
        );
    }

    public static AdBlockMethod getAdBlockMethod(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return AdBlockMethod.fromCode(prefs.getInt(
                context.getString(R.string.pref_ad_block_method_key),
                context.getResources().getInteger(R.integer.pref_ad_block_method_key_def)
        ));
    }

    public static VpnStatus getVpnServiceStatus(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return VpnStatus.fromCode(prefs.getInt(
                context.getString(R.string.pref_vpn_service_status_key),
                context.getResources().getInteger(R.integer.pref_vpn_service_status_def)
        ));
    }

    public static void setVpnServiceStatus(Context context, VpnStatus status) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.pref_vpn_service_status_key), status.toCode());
        editor.apply();
    }

    public static boolean getVpnServiceOnBoot(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_vpn_service_on_boot_key),
                context.getResources().getBoolean(R.bool.pref_vpn_service_on_boot_def)
        );
    }

    public static boolean getVpnWatchdogEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_vpn_watchdog_enabled_key),
                context.getResources().getBoolean(R.bool.pref_vpn_watchdog_enabled_def)
        );
    }

    public static boolean getDebugEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_enable_debug_key),
                context.getResources().getBoolean(R.bool.pref_enable_debug_def)
        );
    }

    public static boolean getTelemetryEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_enable_telemetry_key),
                context.getResources().getBoolean(R.bool.pref_enable_telemetry_def)
        );
    }

    public static String getVpnExcludedSystemApps(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getString(
                context.getString(R.string.pref_vpn_excluded_system_apps_key),
                context.getString(R.string.pref_vpn_excluded_system_apps_default)
        );
    }

    public static Set<String> getVpnExcludedApps(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getStringSet(
                context.getString(R.string.pref_vpn_excluded_user_apps_key),
                Collections.emptySet()
        );
    }

    /**
     * Checks if this is the first time the app is launched after installation.
     */
    public static boolean isFirstLaunch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_first_launch_key),
                true // Default: true (first launch)
        );
    }

    /**
     * Marks the app as "launched" (no longer first run).
     */
    public static void setFirstLaunchCompleted(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_first_launch_key), false);
        editor.apply();
    }

    /**
     * Checks VPN status is running.
     */
    public static boolean isVpnRunning(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(
                context.getString(R.string.pref_is_vpn_running_key),
                false
        );
    }

    /**
     * Marks the VPN running status.
     */
    public static void setIsVpnRunning(Context context, boolean isVpnRunning) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_is_vpn_running_key), isVpnRunning);
        editor.apply();
    }

    /**
     * Checks user is authorized.
     */
    public static boolean isAuthorized(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return !prefs.getString(
                context.getString(R.string.pref_token),
                ""
        ).isEmpty();
    }

    /**
     * Get adBlock count.
     */
    public static int adBlockCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getInt(
                context.getString(R.string.pref_ad_block_count),
                0
        );
    }

    /**
     * Marks adBlock count.
     */
    public static void incrementAdBlockCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.pref_ad_block_count), adBlockCount(context) + 1);
        editor.apply();
    }

    /**
     * Checks is Speed charge Enabled.
     */
    public static boolean isSpeedEnabled() {
        return sharedPrefs.getBoolean(
                PREF_IS_SPEED_CHARGE_ENABLED,
                false
        );
    }

    /**
     * Checks is Privacy Protection Enabled.
     */
    public static boolean isPrivacyProtectionEnabled() {
        return sharedPrefs.getBoolean(
                PREF_IS_PRIVACY_PROTECTION_ENABLED,
                false
        );
    }

    /**
     * Checks is Cookie Protection Enabled.
     */
    public static boolean isCookieProtectionEnabled() {
        return sharedPrefs.getBoolean(
                PREF_IS_COOKIE_PROTECTION_ENABLED,
                false
        );
    }

    /**
     * Checks is Security Protection Enabled.
     */
    public static boolean isSecurityEnabled() {
        return sharedPrefs.getBoolean(
                PREF_IS_MALWARE_PROTECTION_ENABLED,
                false
        );
    }

    /**
     * Checks is Social Media Protection Enabled.
     */
    public static boolean isSocialMediaProtectionEnabled() {
        return sharedPrefs.getBoolean(
                PREF_IS_SOCIAL_MEDIA_PROTECTION_ENABLED,
                false
        );
    }

    /**
     * Checks is Social Media Protection Enabled.
     */
    public static boolean isYoutubeProtectionEnabled() {
        return sharedPrefs.getBoolean(
                PREF_IS_YOUTUBE_PROTECTION_ENABLED,
                false
        );
    }

    public static void registerOnProtectionChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (onProtectionChangeListener != null) {
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(onProtectionChangeListener);
        }
        onProtectionChangeListener = listener;
        sharedPrefs.registerOnSharedPreferenceChangeListener(onProtectionChangeListener);
    }
}