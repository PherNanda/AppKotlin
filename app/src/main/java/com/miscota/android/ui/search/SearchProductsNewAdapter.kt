package com.miscota.android.ui.search

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miscota.android.R
import com.miscota.android.databinding.ItemSearchProductBinding
import com.miscota.android.ui.category.CategoryUiModel
import com.miscota.android.util.RecyclerViewLoadMoreListener

class SearchProductsNewAdapter(
    private val productClickListener: (CategoryUiModel.Product) -> Unit,
    private val loadMoreTopProducts: () -> Unit,
) : ListAdapter<CategoryUiModel.Product, SearchProductsNewAdapter.ProductViewHolder>(
    ListDiffUtil
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        return ProductViewHolder(
            binding = ItemSearchProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            productClickListener = productClickListener,
            loadMoreTopProducts = loadMoreTopProducts
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemSearchProductBinding,
        private val productClickListener: (CategoryUiModel.Product) -> Unit,
        private val loadMoreTopProducts: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var uiModel: CategoryUiModel.Product

        init {
            binding.root.setOnClickListener {
                productClickListener.invoke(uiModel)
            }
        }

        fun bind(item: CategoryUiModel.Product) {
            uiModel = item
            binding.priceText.text = String.format("${item.productPrice.split("€").firstOrNull().toString()} €")

            binding.discountText.isVisible = item.hasDiscount
            binding.discountText.text = item.discountPrice
            binding.productName.text = item.productName

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

    object ListDiffUtil : DiffUtil.ItemCallback<CategoryUiModel.Product>() {
        override fun areItemsTheSame(
            oldItem: CategoryUiModel.Product,
            newItem: CategoryUiModel.Product
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: CategoryUiModel.Product,
            newItem: CategoryUiModel.Product
        ): Boolean {
            return oldItem == newItem
        }
    }
}
