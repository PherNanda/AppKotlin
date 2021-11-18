package com.miscota.android.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miscota.android.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categoryClickListener: (CategoryUiModel.CategoryListItem.Category) -> Unit,
) : ListAdapter<CategoryUiModel.CategoryListItem.Category, CategoryAdapter.CategoryViewHolder>(
    ListDiffUtil
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        return CategoryViewHolder(
            binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            categoryClickListener = categoryClickListener,
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        private val categoryClickListener: (CategoryUiModel.CategoryListItem.Category) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var uiModel: CategoryUiModel.CategoryListItem.Category

        init {
            binding.checkBox.setOnCheckedChangeListener { _, checked ->
                if (checked) {

                    categoryClickListener.invoke(uiModel.copy(isChecked = true))
                }

            }
        }

        fun bind(item: CategoryUiModel.CategoryListItem.Category) {
            uiModel = item
            
            binding.checkBox.text = uiModel.title
            binding.checkBox.isChecked = uiModel.isChecked
        }

    }

    object ListDiffUtil : DiffUtil.ItemCallback<CategoryUiModel.CategoryListItem.Category>() {
        override fun areItemsTheSame(
            oldItem: CategoryUiModel.CategoryListItem.Category,
            newItem: CategoryUiModel.CategoryListItem.Category
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: CategoryUiModel.CategoryListItem.Category,
            newItem: CategoryUiModel.CategoryListItem.Category
        ): Boolean {
            return oldItem == newItem
        }
    }
}
