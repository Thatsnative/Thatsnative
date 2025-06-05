package com.example.epic.ui.homeFragment.adapter

import android.content.Context
import com.example.epic.databinding.MainRecyclerItemBinding
import com.example.epic.ui.base.adapter.BaseViewHolder

class HomeFeatureViewHolder(
    private val binding: MainRecyclerItemBinding,
    private val onSwitchClick: (HomeFeatureItemModel) -> Unit
) :
    BaseViewHolder<HomeFeatureItemModel, MainRecyclerItemBinding>(binding) {
    override fun bind(item: HomeFeatureItemModel, context: Context) {
        with(binding) {
            icon.setImageResource(item.icon)
            title.setText(item.title)
            description.setText(item.description)
            switchButton.isChecked = item.isChecked
            switchButton.setOnCheckedChangeListener { view, isChecked ->
                onSwitchClick(item.copy(isChecked = isChecked))
            }
        }
    }
}