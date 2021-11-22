package com.miscota.android.ui.categories


import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor

import androidx.core.graphics.drawable.DrawableCompat

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load

import com.miscota.android.R
import com.miscota.android.databinding.ItemLoaderBinding
import com.miscota.android.databinding.ItemMainCategoriesBinding
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext


class MainCategoriesItemAdapter(
    private val itemClickListener: (MainCategoriesUiModel.Item) -> Unit
) : ListAdapter<MainCategoriesUiModel, MainCategoriesItemAdapter.ListViewHolder>(ListDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return when (ItemType.values()[viewType]) {
            ItemType.Loader -> {
                ListViewHolder.LoaderViewHolder(
                    ItemLoaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ItemType.Item -> {
                ListViewHolder.ItemViewHolder(
                    ItemMainCategoriesBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    itemClickListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            MainCategoriesUiModel.LoaderItem -> {
                ItemType.Loader.ordinal
            }
            is MainCategoriesUiModel.Item -> {
                ItemType.Item.ordinal
            }
        }
    }

    enum class ItemType {
        Loader,
        Item
    }

    sealed class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(item: MainCategoriesUiModel)

        class LoaderViewHolder(private val binding: ItemLoaderBinding) : ListViewHolder(binding.root) {
            override fun bind(item: MainCategoriesUiModel) {
                //binding.progressBar.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                //Drawable(R.drawable.ic_background_loading_transp)
                DrawableCompat.setTint(
                    binding.progressBar.indeterminateDrawable,
                    Color.parseColor("#4FC3F7")
                )

            }
        }


        class ItemViewHolder(
            private val binding: ItemMainCategoriesBinding,
            private val itemClickListener: (MainCategoriesUiModel.Item) -> Unit
        ) : ListViewHolder(binding.root) {

            override fun bind(item: MainCategoriesUiModel) {
                val uiModel = item as MainCategoriesUiModel.Item
                binding.textView.text = uiModel.title

                binding.imageView.load(uiModel.imageUrl) {
                    placeholder(R.color.grey_300)
                    error(R.color.grey_300)
                    bitmapConfig(Bitmap.Config.ARGB_8888)
                }

                binding.root.setOnClickListener {
                    itemClickListener.invoke(uiModel)
                    println("item MainCategoriesItemAdapter ${item.title}   ${item.subItems}")

                }
            }
        }

    }



    object ListDiffUtil : DiffUtil.ItemCallback<MainCategoriesUiModel>() {
        override fun areItemsTheSame(oldItem: MainCategoriesUiModel, newItem: MainCategoriesUiModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: MainCategoriesUiModel, newItem: MainCategoriesUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
