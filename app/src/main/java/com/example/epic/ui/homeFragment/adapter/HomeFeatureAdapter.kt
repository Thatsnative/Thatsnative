package com.example.epic.ui.homeFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.epic.databinding.MainRecyclerItemBinding
import com.example.epic.ui.base.adapter.BaseRecyclerViewAdapter


class HomeFeatureAdapter(private val onSwitchClick: (HomeFeatureItemModel) -> Unit) :
    BaseRecyclerViewAdapter<MainRecyclerItemBinding, HomeFeatureItemModel, HomeFeatureViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeFeatureViewHolder(
        MainRecyclerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onSwitchClick
    )

    override fun getItemCount() = itemList.size
}