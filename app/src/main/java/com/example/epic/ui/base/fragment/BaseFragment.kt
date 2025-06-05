package com.example.epic.ui.base.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.epic.ui.base.viewmodel.BaseViewModel
import androidx.annotation.LayoutRes
import com.example.epic.coroutine.subscribe
import com.example.epic.ui.base.viewmodel.UiState
import kotlinx.coroutines.flow.Flow

abstract class BaseFragment<ViewModel : BaseViewModel, ViewBind : ViewBinding>(
    @LayoutRes contentLayoutRes: Int
) : Fragment(contentLayoutRes) {

    abstract val viewModel: ViewModel
    abstract val binding: ViewBind

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.subscribe(viewLifecycleOwner, ::onStateChanges)
        collectFlows()
        initView()
        initListeners()
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.onDetach()
    }

    protected open fun initView() {}

    protected open fun initListeners() {}

    protected open fun collectFlows() {}

    private fun onStateChanges(newState: UiState) {
        when (newState) {
            is UiState.READY -> onSuccess()
            is UiState.LOADING -> onLoading()
            is UiState.ERROR -> onError(newState.message, newState.code)
        }
    }

    abstract fun onSuccess()
    abstract fun onLoading()
    abstract fun onError(message: String, code: Int)

    protected inline fun <reified T> collectFlow(flow: Flow<T>, crossinline action: (T) -> Unit) =
        view?.run {
            if (!this@BaseFragment.isAdded) return@run
            flow.subscribe(viewLifecycleOwner) { action(it ?: return@subscribe) }
        }

}