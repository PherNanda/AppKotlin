package com.miscota.android.ui.search

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miscota.android.R
import com.miscota.android.databinding.*
import com.miscota.android.ui.category.CategoryAdapter
import com.miscota.android.ui.category.CategoryItemAdapter
import com.miscota.android.ui.category.CategoryTopProductAdapter
import com.miscota.android.ui.category.CategoryUiModel
import com.miscota.android.util.RecyclerViewLoadMoreListener

class SearchProductsAdapterChanged(
    private val onCategoryClickListener: (product: CategoryUiModel.CategoryListItem.Category) -> Unit,
    private val loadMoreTopProducts: () -> Unit,
    private val onProductClickListener: (product: CategoryUiModel.Product) -> Unit,
    private val typeProduct: String
) :
    ListAdapter<CategoryUiModel, SearchProductsAdapterChanged.ListViewHolder>(ListDiffUtil) {

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
            ItemType.Product -> {
                ListViewHolder.ProductViewHolder(
                    binding = ItemSearchProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onProductClickListener = onProductClickListener,
                    typeProduct = typeProduct
                )
            }
            ItemType.Spacer -> {
                ListViewHolder.SpacerViewHolder(
                    ItemSpacerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ItemType.CategoryList -> {
                ListViewHolder.CategoryListViewHolder(
                    binding = ItemCategoryListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onCategoryClickListener = onCategoryClickListener,
                )
            }
            ItemType.TopProductsList -> {
                ListViewHolder.TopProductsListViewHolder(
                    binding = ItemCategoryTopSalesBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onProductClickListener = onProductClickListener,
                    loadMoreTopProducts = loadMoreTopProducts,
                )
            }
            ItemType.Text -> {
                ListViewHolder.TextViewHolder(
                    ItemCategoryTextBinding.inflate(
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
            CategoryUiModel.LoaderItem -> {
                ItemType.Loader.ordinal
            }
            is CategoryUiModel.Product -> {
                ItemType.Product.ordinal
            }
            CategoryUiModel.SpacerItem -> {
                ItemType.Spacer.ordinal
            }
            is CategoryUiModel.CategoryListItem -> {
                ItemType.CategoryList.ordinal
            }
            is CategoryUiModel.TopProductListItem -> {
                ItemType.TopProductsList.ordinal
            }
            CategoryUiModel.TextItem -> {
                ItemType.Text.ordinal
            }
        }
    }

    enum class ItemType {
        Loader,
        Spacer,
        CategoryList,
        TopProductsList,
        Text,
        Product
    }

    sealed class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(item: CategoryUiModel)

        class LoaderViewHolder(binding: ItemLoaderBinding) : ListViewHolder(binding.root) {
            override fun bind(item: CategoryUiModel) {
            }
        }

        class SpacerViewHolder(binding: ItemSpacerBinding) : ListViewHolder(binding.root) {
            override fun bind(item: CategoryUiModel) {
            }
        }

        class TextViewHolder(binding: ItemCategoryTextBinding) : ListViewHolder(binding.root) {
            override fun bind(item: CategoryUiModel) {
            }
        }

        class CategoryListViewHolder(
            private val binding: ItemCategoryListBinding,
            private val onCategoryClickListener: (product: CategoryUiModel.CategoryListItem.Category) -> Unit,
        ) :
            ListViewHolder(binding.root) {
            override fun bind(item: CategoryUiModel) {
                val uiModel = item as CategoryUiModel.CategoryListItem

                val categoryAdapter = CategoryAdapter {
                    onCategoryClickListener.invoke(it)
                }

                binding.recyclerViewCatList.apply {
                    layoutManager = LinearLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                    adapter = categoryAdapter
                }

                categoryAdapter.submitList(uiModel.categories)
            }
        }

        class TopProductsListViewHolder(
            private val binding: ItemCategoryTopSalesBinding,
            private val loadMoreTopProducts: () -> Unit,
            private val onProductClickListener: (product: CategoryUiModel.Product) -> Unit,
        ) :
            ListViewHolder(binding.root) {
            override fun bind(item: CategoryUiModel) {
                val uiModel = item as CategoryUiModel.TopProductListItem

                val adapter = CategoryTopProductAdapter {
                    onProductClickListener.invoke(it)
                }

                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                    addOnScrollListener(
                        RecyclerViewLoadMoreListener(
                            loadMore = {
                                loadMoreTopProducts.invoke()
                            }
                        )
                    )
                    this.adapter = adapter
                }
                adapter.submitList(uiModel.products)
            }
        }

        class ProductViewHolder(
            private val binding: ItemSearchProductBinding,
            private val onProductClickListener: (product: CategoryUiModel.Product) -> Unit,
            private val typeProduct: String,
        ) : ListViewHolder(binding.root) {

            override fun bind(item: CategoryUiModel) {
                item as CategoryUiModel.Product

                binding.root.setOnClickListener {
                    onProductClickListener.invoke(item)
                }

                binding.priceText.text = String.format("${item.productPrice.split("€").firstOrNull().toString()} €")
                //println("binding.priceText.text searchProductAdapterChanged ${binding.priceText.text}")
                binding.discountText.isVisible = item.hasDiscount
                binding.discountText.text = item.discountPrice
                binding.productName.text = item.productName
                //println("binding.productName.text searchProductAdapterChanged ${binding.productName.text}")
                if (item.imageList.count() > 0) {
                    item.imageList[0]?.let {
                        binding.productImage.load(it) {
                            placeholder(R.color.placeholder)
                            error(R.color.placeholder)
                            bitmapConfig(Bitmap.Config.ARGB_8888)
                        }
                    }
                }



            }

        }

        fun boldMyText(inputText:String,startIndex:Int,endIndex:Int): Spannable {
            val outPutBoldText: Spannable = SpannableString(inputText)
            outPutBoldText.setSpan(
                StyleSpan(Typeface.BOLD), startIndex, endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            return outPutBoldText
        }
    }

    object ListDiffUtil : DiffUtil.ItemCallback<CategoryUiModel>() {
        override fun areItemsTheSame(oldItem: CategoryUiModel, newItem: CategoryUiModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: CategoryUiModel,
            newItem: CategoryUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}

