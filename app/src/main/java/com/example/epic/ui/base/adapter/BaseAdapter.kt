package com.example.epic.ui.base.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<ItemViewBinding : ViewBinding, Item : DiffUtilModel<*>, ViewHolder : BaseViewHolder<Item, ItemViewBinding>> :
    ListAdapter<Item, ViewHolder>(EventsDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            getItem(position)?.let { item ->
                bind(item, itemView.context)
                itemView.setOnClickListener {
                        onItemClick(item)
                }
            }
        }
    }

    class EventsDiffCallback<Item : DiffUtilModel<*>> : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) =
            (oldItem as DiffUtilModel<*>).id == (newItem as DiffUtilModel<*>).id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    }

}