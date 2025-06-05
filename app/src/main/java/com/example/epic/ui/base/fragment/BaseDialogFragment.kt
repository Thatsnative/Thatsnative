package com.example.epic.ui.base.fragment

import com.example.epic.coroutine.subscribe
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.epic.ui.base.viewmodel.BaseViewModel
import com.example.epic.common.DEFAULT_BOOLEAN
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.LayoutRes
import kotlinx.coroutines.flow.Flow

abstract class BaseDialogFragment<ViewModel : BaseViewModel, ViewBind : ViewBinding>(
    @LayoutRes contentLayoutRes: Int
) : DialogFragment(contentLayoutRes) {

    abstract val viewModel: ViewModel
    abstract val binding: ViewBind

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = DEFAULT_BOOLEAN
        collectFlows()
        initView()
        initListeners()
    }

    protected open fun initView() {}

    protected open fun initListeners() {}

    protected open fun collectFlows() {}

    protected inline fun <reified T> collectFlow(flow: Flow<T>, crossinline action: (T) -> Unit) = view?.run {
        if (!this@BaseDialogFragment.isAdded) return@run
        flow.subscribe(viewLifecycleOwner) { action(it ?: return@subscribe) }
    }
}