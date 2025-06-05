package com.example.epic.common

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.epic.vpn.VpnService

object PermissionUtils {

    // Список всех разрешений, которые нужно проверить
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_NETWORK_STATE,

        )

    // Список всех разрешений, которые нужно проверить
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    val REQUIRED_PERMISSIONS_UPSIDE_DOWN_CAKE = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
    )

    // Метод для проверки и запроса разрешений
    fun checkAndRequestPermissions(activity: Activity, requestCode: Int) {
        val missingPermissions = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toMutableList().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                addAll(REQUIRED_PERMISSIONS_UPSIDE_DOWN_CAKE)
            }
        }

        if (missingPermissions.isNotEmpty()) {
            // Если есть недостающие разрешения, запрашиваем их
            ActivityCompat.requestPermissions(
                activity,
                missingPermissions.toTypedArray(),
                requestCode
            )
        }
    }

    // Метод для обработки результата запроса разрешений
    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
        onPermissionsGranted: () -> Unit, onPermissionsDenied: () -> Unit
    ) {
        if (requestCode == 100) { // используйте свой requestCode
            val deniedPermissions = grantResults.zip(permissions)
                .filter { it.first != PackageManager.PERMISSION_GRANTED }
            if (deniedPermissions.isEmpty()) {
                onPermissionsGranted() // Все разрешения получены
            } else {
                onPermissionsDenied() // Есть отклоненные разрешения
            }
        }
    }

    fun checkPermission(activity: Activity, permission: String) =
        ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED

    fun getVpnPermission(activity: Activity) = VpnService.prepare(activity)
}