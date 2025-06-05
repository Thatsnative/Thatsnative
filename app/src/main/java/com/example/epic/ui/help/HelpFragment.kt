package com.example.epic.ui.help

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.R
import com.example.epic.arguments.BundleKey.VPN_PREPARE_KEY
import com.example.epic.common.PermissionUtils
import com.example.epic.databinding.HelpFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import com.example.epic.ui.base.viewmodel.UiState
import org.koin.androidx.viewmodel.ext.android.viewModel

class HelpFragment :
    BaseFragmentNC<HelpViewModel, HelpFragmentLayoutBinding>(R.layout.help_fragment_layout) {
    override val viewModel: HelpViewModel by viewModel()
    override val binding: HelpFragmentLayoutBinding by viewBinding()

    private var isVpnPermissionShown = true

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getVpnPermission()
        }
    }

    private val prepareVpnLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            requireActivity().supportFragmentManager.setFragmentResult(VPN_PREPARE_KEY, bundleOf())
            popBackStack()
        }
    }

    override fun initView() {
        initHelpPoint()
        updateGetPermissionText()
        viewModel.updateState(UiState.READY)
    }

    override fun initListeners() {
        binding.getPermissionButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionUtils.checkPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                if (viewModel.canShowNotificationPermission()) {
                    requestPostNotificationsPermission()
                } else {
                    isVpnPermissionShown = false
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    }
                    requireActivity().startActivity(intent)
                }
            } else {
                getVpnPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getVpnPermissionAfterRedirect()
        updateGetPermissionText()
    }

    override fun collectFlows() {}

    override fun onSuccess() {}

    override fun onLoading() {}

    override fun onError(message: String, code: Int) {}

    private fun initHelpPoint() {
        with(binding) {
            helpPoint1.number.text = 1.toString()
            helpPoint1.description.text = getString(R.string.close_the_application)
            helpPoint2.number.text = 2.toString()
            helpPoint2.description.text = getString(R.string.open_the_settings_app_on_your_phone)
            helpPoint3.number.text = 3.toString()
            helpPoint3.description.text =
                getString(R.string.open_google_browser_and_find_n_extensions)
            helpPoint4.number.text = 4.toString()
            helpPoint4.description.text = getString(R.string.turn_on_adblock_avlyx)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPostNotificationsPermission() {
        activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        viewModel.incrementNotificationRequestCount()
    }

    private fun getVpnPermission() {
        PermissionUtils.getVpnPermission(requireActivity())?.let(prepareVpnLauncher::launch)
        isVpnPermissionShown = true
    }

    private fun getVpnPermissionAfterRedirect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !PermissionUtils.checkPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            ) && !isVpnPermissionShown
        ) {
            getVpnPermission()
        }
    }

    private fun updateGetPermissionText() {
        binding.getPermissionButton.text = if (!viewModel.canShowNotificationPermission() &&
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    PermissionUtils.checkPermission(
                        requireActivity(),
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                    )
        ) {
            getString(R.string.go_to_settings)
        } else {
            getString(R.string.get_permissions)
        }
    }
}