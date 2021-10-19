package com.miscota.android.ui.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miscota.android.databinding.ItemHeaderTextBinding
import com.miscota.android.databinding.ItemSubCategoriesBinding

class AllProductsCategoryItemAdapter(private val itemClickListener: (AllProductsCategoryUiModel.SubCategoryUiModel) -> Unit) :
    ListAdapter<AllProductsCategoryUiModel, AllProductsCategoryItemAdapter.ListViewHolder>(
        ListDiffUtil
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return when (ItemType.values()[viewType]) {
            ItemType.Item -> {
                ListViewHolder.ItemViewHolder(
                    ItemSubCategoriesBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    itemClickListener
                )
            }
            ItemType.Header -> {
                ListViewHolder.HeaderViewHolder(
                    ItemHeaderTextBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AllProductsCategoryUiModel.Header -> {
                ItemType.Header.ordinal
            }
            is AllProductsCategoryUiModel.SubCategoryUiModel -> {
                ItemType.Item.ordinal
            }
        }
    }

    enum class ItemType {
        Header,
        Item
    }

    sealed class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(item: AllProductsCategoryUiModel)

        class HeaderViewHolder(private val binding: ItemHeaderTextBinding) :
            ListViewHolder(binding.root) {
            override fun bind(item: AllProductsCategoryUiModel) {
                val uiModel = item as AllProductsCategoryUiModel.Header
                binding.textView.text = uiModel.categoryName
            }
        }

        class ItemViewHolder(
            private val binding: ItemSubCategoriesBinding,
            private val itemClickListener: (AllProductsCategoryUiModel.SubCategoryUiModel) -> Unit
        ) : ListViewHolder(binding.root) {

            override fun bind(item: AllProductsCategoryUiModel) {
                val uiModel = item as AllProductsCategoryUiModel.SubCategoryUiModel
                binding.textView.text = uiModel.categoryName

                binding.root.setOnClickListener {
                    itemClickListener.invoke(uiModel)
                }
            }
        }
    }

    object ListDiffUtil : DiffUtil.ItemCallback<AllProductsCategoryUiModel>() {
        override fun areItemsTheSame(
            oldItem: AllProductsCategoryUiModel,
            newItem: AllProductsCategoryUiModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AllProductsCategoryUiModel,
            newItem: AllProductsCategoryUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
