package com.example.epic.ui.blockListFragment

import HostBlockListAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.R
import com.example.epic.analityc.AnalyticLogger
import com.example.epic.coroutine.subscribeWhenResumed
import com.example.epic.databinding.BlockListFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import org.koin.androidx.viewmodel.ext.android.viewModel

class BlockListFragment :
    BaseFragmentNC<BlockListViewModel, BlockListFragmentLayoutBinding>(R.layout.block_list_fragment_layout) {

    override val viewModel: BlockListViewModel by viewModel()
    override val binding: BlockListFragmentLayoutBinding by viewBinding()

    private var adapter = HostBlockListAdapter { }

    override fun initView() {
        initRvAdapter()

        with(binding) {
            logo.isVisible = false
            logoTitle.isVisible = false
            exploreYourSuite.isVisible = false
            abiableProducts.isVisible = false
            exploreSecuritySuite.isVisible = false
            rvBlocked.isVisible = false
        }
    }

    override fun initListeners() {
        // You can add retry logic here if needed
    }

    override fun collectFlows() {
        viewModel.pagedHosts.subscribeWhenResumed(viewLifecycleOwner) { pagingData ->
            AnalyticLogger.info(pagingData.toString())
            adapter.submitData(lifecycleScope, pagingData)
        }

    }

    override fun onSuccess() {}
    override fun onLoading() {}
    override fun onError(message: String, code: Int) {}

    private fun initRvAdapter() {
        binding.rvBlocked.adapter = adapter
        binding.rvBlocked.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        val TAG = BlockListFragment::class.java.simpleName
        fun newInstance() = BlockListFragment()
    }
}