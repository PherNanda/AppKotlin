package com.miscota.android.ui.productdetail

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miscota.android.R
import com.miscota.android.databinding.ItemProductDetailImageBinding
import io.card.payment.i18n.LocalizedStrings.getString

class ProductDetailImageAdapter(
    private val imageList: List<String?>
) : RecyclerView.Adapter<ProductDetailImageAdapter.ProductImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        return ProductImageViewHolder(
            ItemProductDetailImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        if (imageList[position] != null) {
            holder.bind(imageUrl = imageList[position])
            println("imageList[position]::: ${imageList[position]}")
        }

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ProductImageViewHolder(private val binding: ItemProductDetailImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String?) {
            binding.imageView.load(imageUrl) {
                placeholder(R.color.placeholder)
                error(R.color.placeholder)
                bitmapConfig(Bitmap.Config.ARGB_8888)
            }
            println("imageUrl::: $imageUrl")
        }

       
    }
}
