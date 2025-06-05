package com.example.epic.ui.base.fragment

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.epic.ui.base.viewmodel.BaseViewModel
import androidx.annotation.LayoutRes
import com.example.epic.coroutine.subscribe
import kotlinx.coroutines.flow.Flow

abstract class BaseBottomSheetFragment<ViewModel : BaseViewModel, ViewBind : ViewBinding>(
    @LayoutRes contentLayoutRes: Int
) : BottomSheetDialogFragment(contentLayoutRes) {

    abstract val viewModel: ViewModel
    abstract val binding: ViewBind

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
        initView()
        initListeners()
    }

    protected open fun initView() {}

    protected open fun initListeners() {}

    protected open fun collectFlows() {}

    protected inline fun <reified T> collectFlow(flow: Flow<T>, crossinline action: (T) -> Unit) = view?.run {
        if (!this@BaseBottomSheetFragment.isAdded) return@run
        flow.subscribe(viewLifecycleOwner) { action(it ?: return@subscribe) }
    }
}
