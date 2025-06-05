package com.example.epic.ui.base.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.viewbinding.ViewBinding
import com.example.epic.ui.base.viewmodel.BaseViewModel
import com.example.epic.common.DEFAULT_BOOLEAN
import androidx.annotation.LayoutRes
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.epic.R

abstract class BaseFragmentNC<ViewModel : BaseViewModel, ViewBind : ViewBinding>(
    @LayoutRes contentLayoutRes: Int
) : BaseFragment<ViewModel, ViewBind>(contentLayoutRes) {

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.nav_host_fragment) ?: findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController // initialize navController
    }

    protected fun popBackStack() {
        navController.popBackStack()
    }

    protected fun popBackStack(destinationId: Int) {
        navController.popBackStack(destinationId, DEFAULT_BOOLEAN)
    }

    protected fun navigateFragment(destinationId: Int, arg: Bundle? = null) {
        navController.navigate(destinationId, arg)
    }

    protected fun popUpToFragment(destinationId: Int, arg: Bundle? = null, clearDestination: Int) {
        navController.navigate(destinationId, arg, navOptions {
            popUpTo(clearDestination) {
                inclusive = true
            }
            anim {
                enter = android.R.anim.fade_in
                exit = android.R.anim.fade_out
                popEnter = android.R.anim.fade_in
                popExit = android.R.anim.fade_out
            }
        })
    }

    protected fun navigateFragment(destinations: NavDirections) {
        navController.navigate(destinations)
    }

}