package com.example.epic.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.R
import com.example.epic.arguments.ArgumentKey.IS_RUNNING_KEY
import com.example.epic.arguments.ArgumentKey.SCREEN_BOTTOM_INSET_KEY
import com.example.epic.arguments.BundleKey.BOTTOM_BAR_INSETS_KEY
import com.example.epic.arguments.BundleKey.TOGGLE_BUTTON_KEY
import com.example.epic.arguments.BundleKey.VPN_PREPARE_KEY
import com.example.epic.broadcast.Command
import com.example.epic.common.DEFAULT_FLOAT
import com.example.epic.common.PermissionUtils
import com.example.epic.common.getAdAwayApplication
import com.example.epic.common.orDefault
import com.example.epic.databinding.MainActivtyLayoutBinding
import com.example.epic.helper.PreferenceHelper
import com.example.epic.sharedPrefs.mainFeaturesPrefs.PREF_AD_BLOCK_COUNT
import com.example.epic.sharedPrefs.mainFeaturesPrefs.PREF_IS_COOKIE_PROTECTION_ENABLED
import com.example.epic.sharedPrefs.mainFeaturesPrefs.PREF_IS_MALWARE_PROTECTION_ENABLED
import com.example.epic.sharedPrefs.mainFeaturesPrefs.PREF_IS_PRIVACY_PROTECTION_ENABLED
import com.example.epic.sharedPrefs.mainFeaturesPrefs.PREF_IS_SOCIAL_MEDIA_PROTECTION_ENABLED
import com.example.epic.sharedPrefs.mainFeaturesPrefs.PREF_IS_SPEED_CHARGE_ENABLED
import com.example.epic.sharedPrefs.mainFeaturesPrefs.PREF_IS_YOUTUBE_PROTECTION_ENABLED
import com.example.epic.util.AppExecutors
import com.example.epic.vpn.VpnService
import com.example.epic.vpn.VpnServiceHeartbeat
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val CHANGE_VISIBILITY_ANIMATION_DURATION = 500L
private const val SPLASH_SCREEN_DELAY = 2000L

class MainActivity : AppCompatActivity(R.layout.main_activty_layout) {

    private val viewModel: MainViewModel by viewModel()
    private val binding: MainActivtyLayoutBinding by viewBinding()

    private val executors: AppExecutors = AppExecutors.getInstance()
    private val app by lazy { getAdAwayApplication() }
    private val parsingCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.setIsApplied(true)
            viewModel.setIsLoading(false)
        }
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
//        isGranted ->
//        if (!isGranted) {
//            requestPostNotificationsPermission()
//        }
    }

    private val prepareVpnLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            executors.diskIO().execute {
                app.sourceModel?.retrieveHostsSources()
            }
            viewModel.runVpn()
        } else {
            viewModel.stopVpn()
        }
    }

    private fun ifFirstLaunchAPK(): Boolean {
        val isFirstLaunch = PreferenceHelper.isFirstLaunch(this)
        if (!isFirstLaunch) {
            viewModel.setIsApplied(true)
            viewModel.setIsLoading(false)
        }
        return isFirstLaunch
    }

    private fun getAllData() {
        app.adBlockModel?.apply()
        executors.diskIO().execute {
            if (app.sourceModel?.checkForUpdate().orDefault()) {
                app.sourceModel?.retrieveHostsSources()
                app.adBlockModel?.apply()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPostNotificationsPermission()
        enableEdgeToEdge()
        returnSystemInsets()
        setContentView(binding.root)
        setupSplashScreen()
        initVpnRunButton()
        setSystemInsetsUpdateListener()
        setVpnPrepareListener()
        PreferenceHelper.registerOnProtectionChangeListener(object :
            OnSharedPreferenceChangeListener {
            var keys: List<String?> = listOf(
                PREF_IS_MALWARE_PROTECTION_ENABLED,
                PREF_IS_COOKIE_PROTECTION_ENABLED,
                PREF_IS_YOUTUBE_PROTECTION_ENABLED,
                PREF_IS_SPEED_CHARGE_ENABLED,
                PREF_IS_SOCIAL_MEDIA_PROTECTION_ENABLED,
                PREF_IS_PRIVACY_PROTECTION_ENABLED
            )

            override fun onSharedPreferenceChanged(
                sharedPreferences: SharedPreferences,
                key: String?
            ) {
                if (keys.contains(key)) {
                    executors.diskIO().execute {
                        app.sourceModel?.syncHostEntries()
                    }
                }
            }
        }
        )
    }

    private fun setSystemInsetsUpdateListener() {
        supportFragmentManager.setFragmentResultListener(BOTTOM_BAR_INSETS_KEY, this) { _, bundle ->
            val bottomBarInsets = bundle.getInt(SCREEN_BOTTOM_INSET_KEY)
            returnSystemInsets(bottomBarInsets)
        }
    }

    private fun setVpnPrepareListener() {
        supportFragmentManager.setFragmentResultListener(VPN_PREPARE_KEY, this) { _, _ ->
            executors.diskIO().execute {
                app.sourceModel?.retrieveHostsSources()
            }
        }
    }



    private fun getVpnPermission() {
        val vpnIntent = PermissionUtils.getVpnPermission(this)
        if (vpnIntent != null) {
            prepareVpnLauncher.launch(vpnIntent)
        } else {
            viewModel.runVpn()
        }
    }

    private fun requestPostNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionUtils.checkPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            viewModel.incrementNotificationRequestCount()
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                parsingCompleteReceiver,
                IntentFilter("com.example.epic.ACTION_PARSING_COMPLETE")
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(parsingCompleteReceiver)
    }

    private fun setupSplashScreen() {
        with(binding.customSplash.root) {
            if (!viewModel.isSplashShown.value) {
                isVisible = true
                postDelayed({
                    animate()
                        .alpha(DEFAULT_FLOAT)
                        .setDuration(CHANGE_VISIBILITY_ANIMATION_DURATION)
                        .withEndAction {
                            isVisible = false
                            viewModel.setIsSplashShown(true)
                        }
                        .start()
                }, SPLASH_SCREEN_DELAY)
            }
        }
    }


    private fun initVpnRunButton() {
        supportFragmentManager.setFragmentResultListener(TOGGLE_BUTTON_KEY, this) { _, bundle ->
            val isRunning = bundle.getBoolean(IS_RUNNING_KEY)
            toggleVPNButton(isRunning)
        }
    }

    private fun toggleVPNButton(isRunning: Boolean) {
        if (isRunning) {
            app.adBlockModel?.revert()
            val intent = Intent(this, VpnService::class.java)
            Command.STOP.appendToIntent(intent)
            stopService(intent)
            viewModel.setIsApplied(false)
            PreferenceHelper.setFirstLaunchCompleted(this)
            viewModel.stopVpn()
        } else {
            viewModel.setIsLoading(ifFirstLaunchAPK())
            getVpnPermission()
            val intent = Intent(this@MainActivity, VpnService::class.java)
            Command.START.appendToIntent(intent)
            val started = startForegroundService(intent) != null
            if (started) {
                VpnServiceHeartbeat.start(this@MainActivity)
                getAllData()
            }
        }
    }

    private fun returnSystemInsets(bottomBarInsets: Int = -1) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomInset = if (bottomBarInsets == -1) systemInsets.bottom else bottomBarInsets
            view.setPadding(
                systemInsets.left,
                systemInsets.top,
                systemInsets.right,
                bottomInset
            )
            insets
        }
    }
}


