package com.miscota.android.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miscota.android.databinding.ItemMapBinding

class StoreLocationsAdapter(private val itemClickListener: (StoreLocationUiModel) -> Unit) :
    ListAdapter<StoreLocationUiModel, StoreLocationsAdapter.ItemViewHolder>(
        ListDiffUtil
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemMapBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            itemClickListener
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ItemViewHolder(
        private val binding: ItemMapBinding,
        private val itemClickListener: (StoreLocationUiModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoreLocationUiModel) {
            binding.addressTitle.text = item.addressTitle
            binding.address.text = item.address

            binding.addressTitle.isChecked = item.isSelected

            binding.addressTitle.setOnClickListener {
                itemClickListener.invoke(item)
            }
        }
    }

    object ListDiffUtil : DiffUtil.ItemCallback<StoreLocationUiModel>() {
        override fun areItemsTheSame(
            oldItem: StoreLocationUiModel,
            newItem: StoreLocationUiModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StoreLocationUiModel,
            newItem: StoreLocationUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
