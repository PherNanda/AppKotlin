package com.miscota.android.ui.categories


import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miscota.android.R
import com.miscota.android.databinding.ItemLoaderBinding
import com.miscota.android.databinding.ItemSubCategoriesBinding

class SubCategoriesItemAdapter(
    private val itemClickListener: (MainCategoriesUiModel.Item) -> Unit) :
    ListAdapter<MainCategoriesUiModel, SubCategoriesItemAdapter.ListViewHolder>(ListDiffUtil) {


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
                    ItemSubCategoriesBinding.inflate(
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

        class LoaderViewHolder(binding: ItemLoaderBinding) : ListViewHolder(binding.root) {
            override fun bind(item: MainCategoriesUiModel) {
            }
        }

        class ItemViewHolder(
            private val binding: ItemSubCategoriesBinding,
            private val itemClickListener: (MainCategoriesUiModel.Item) -> Unit
        ) : ListViewHolder(binding.root) {

            override fun bind(item: MainCategoriesUiModel) {
                val uiModel = item as MainCategoriesUiModel.Item
                binding.textView.text = uiModel.title

                binding.root.setOnClickListener { it ->

                    binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0,
                        R.drawable.ic_next_up, 0
                    )

                    if(uiModel.subItems.isNotEmpty()) {

                        itemClickListener.invoke(uiModel)
                    }else{

                        itemClickListener.invoke(uiModel)

                    }

                }
            }
        }

        fun boldColorMyText(inputText:String,startIndex:Int,endIndex:Int,textColor:Int): Spannable {
            val outPutBoldColorText: Spannable = SpannableString(inputText)
            outPutBoldColorText.setSpan(
                StyleSpan(Typeface.BOLD), startIndex, endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            outPutBoldColorText.setSpan(
                ForegroundColorSpan(textColor), startIndex, endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            return outPutBoldColorText
        }

    }


    object ListDiffUtil : DiffUtil.ItemCallback<MainCategoriesUiModel>() {
        override fun areItemsTheSame(
            oldItem: MainCategoriesUiModel,
            newItem: MainCategoriesUiModel
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: MainCategoriesUiModel,
            newItem: MainCategoriesUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
