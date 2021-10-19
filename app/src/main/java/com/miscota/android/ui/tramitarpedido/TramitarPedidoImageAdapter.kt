package com.miscota.android.ui.tramitarpedido

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miscota.android.R
import com.miscota.android.databinding.ItemCategoryTagBinding
import com.miscota.android.databinding.ItemProductTramitarPedidoBinding
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.cart.toCartItemUiModel
import com.miscota.android.ui.category.CategoryItemTagAdapter
import com.miscota.android.util.AuthStore
import com.miscota.android.util.CartItem
import java.util.*

class TramitarPedidoImageAdapter (private val context: Context,
                                  private val itensCart: List<CartItem>,
                                  private val numberImage: IntArray,
                                  private val authStore: AuthStore,
) : RecyclerView.Adapter<TramitarPedidoImageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProductTramitarPedidoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TramitarPedidoImageAdapter.ViewHolder {
        val binding = ItemProductTramitarPedidoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TramitarPedidoImageAdapter.ViewHolder, position: Int) {
        with(holder){
            with(itensCart[position]){

            binding.productImage.load(itensCart[position].product.image){
                error(R.color.placeholder)
                placeholder(R.color.placeholder)
            }
            binding.qtyProduct.text = itensCart[position].qty.toString()

                if (this.toCartItemUiModel()!=null){

                    println("test list image ${this.product.image}")
                    println("test list title ${this.product.title}")

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itensCart.size
    }

}