package com.example.epic.ui.whiteList

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.R
import com.example.epic.common.DEFAULT_BOOLEAN
import com.example.epic.common.EMPTY_STRING
import com.example.epic.coroutine.subscribe
import com.example.epic.databinding.WhiteListFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import com.example.epic.ui.base.viewmodel.UiState
import com.example.epic.ui.whiteList.adapter.WhiteListAdapter
import com.example.epic.ui.whiteList.adapter.WhiteListState
import org.koin.androidx.viewmodel.ext.android.viewModel

class WhiteListFragment :
    BaseFragmentNC<WhiteListViewModel, WhiteListFragmentLayoutBinding>(R.layout.white_list_fragment_layout) {
    override val viewModel: WhiteListViewModel by viewModel()
    override val binding: WhiteListFragmentLayoutBinding by viewBinding()

    private val whiteListAdapter by lazy { WhiteListAdapter(viewModel::onBlockHost) }

    override fun initListeners() {
        setupSearchBarListener()
    }

    override fun initView() {
        initToolbar()
        initWhiteList()
        viewModel.updateState(UiState.READY)
    }

    override fun collectFlows() {
        with(viewModel) {
            whiteList.subscribe(lifecycleScope) {

            }
            whiteListState.subscribe(lifecycleScope) {
                it?.let {
                    updateToolbarRightButtonState(it)
                    when (it) {
                        WhiteListState.READY -> {
                            updateToolbarRightButtonAction {
                                updateWhiteListState(WhiteListState.EDIT, true)
                            }
                        }

                        WhiteListState.EMPTY -> {
                            updateToolbarRightButtonAction { }
                        }

                        WhiteListState.CAN_ADD -> {
                            updateToolbarRightButtonAction {
                                onAllowHost(binding.searchLayout.searchText.toString())
                            }
                        }

                        WhiteListState.EDIT -> {
                            updateToolbarRightButtonAction(viewModel::syncWhiteList)
                        }
                    }
                }
            }
        }
    }

    override fun onSuccess() {

    }

    override fun onLoading() {

    }

    override fun onError(message: String, code: Int) {

    }

    private fun initToolbar() {
        with(binding.toolbarLayout) {
            backButton.setOnClickListener { popBackStack() }
            rightButton.text = EMPTY_STRING
            rightButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun updateToolbarRightButtonAction(action: () -> Unit) {
        binding.toolbarLayout.rightButton.setOnClickListener {
            action.invoke()
        }
    }

    private fun updateToolbarRightButtonState(state: WhiteListState) {
        val (text, drawable) = when (state) {
            WhiteListState.READY -> {
                updateWhiteListVisibility(true)
                getString(R.string.edit) to null
            }

            WhiteListState.EMPTY -> {
                updateWhiteListVisibility(DEFAULT_BOOLEAN)
                EMPTY_STRING to null
            }

            WhiteListState.CAN_ADD -> {
                updateWhiteListVisibility(true)
                EMPTY_STRING to AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_add_40
                )
            }

            WhiteListState.EDIT -> {
                updateWhiteListVisibility(true)
                getString(R.string.done) to null
            }
        }
        binding.toolbarLayout.rightButton.text = text
        binding.toolbarLayout.rightButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            drawable,
            null,
            null,
            null
        )
    }

    private fun initWhiteList() {

    }

    private fun updateWhiteListVisibility(isVisible: Boolean) {
        binding.whiteList.isVisible = isVisible
        binding.emptyWhiteList.root.isVisible = !isVisible
    }

    private fun setupSearchBarListener() {
        binding.searchLayout.searchText.doAfterTextChanged {
            viewModel.getAllowedHosts(it.toString())
        }
    }

}