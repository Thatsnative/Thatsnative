package com.example.epic.ui.base.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.epic.common.DEFAULT_INT

abstract class BaseRecyclerViewAdapter<ItemViewBinding : ViewBinding, Item : DiffUtilModel<*>, ViewHolder : BaseViewHolder<Item, ItemViewBinding>>
    : RecyclerView.Adapter<ViewHolder>() {
    protected var itemList: MutableList<Item> = mutableListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            itemList[position].let { item ->
                bind(item, itemView.context)
                itemView.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    fun submitList(itemList: List<Item>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyItemChanged(DEFAULT_INT, itemList.lastIndex)
    }
}