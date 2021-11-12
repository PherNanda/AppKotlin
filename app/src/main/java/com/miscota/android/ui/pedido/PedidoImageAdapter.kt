package com.miscota.android.ui.pedido

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miscota.android.R
import com.miscota.android.databinding.ItemProductTramitarPedidoBinding
import com.miscota.android.ui.cart.toCartItemUiModel
import com.miscota.android.util.AuthStore
import com.miscota.android.util.CartItem

class PedidoImageAdapter (private val context: Context,
                        private val itensCart: List<CartItem>,
                        private val numberImage: IntArray,
                        private val authStore: AuthStore,
                        ) : RecyclerView.Adapter<PedidoImageAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemProductTramitarPedidoBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PedidoImageAdapter.ViewHolder {
            val binding = ItemProductTramitarPedidoBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return ViewHolder(binding)
        }


        override fun onBindViewHolder(holder: PedidoImageAdapter.ViewHolder, position: Int) {
            with(holder){
                with(itensCart[position]){

                    if (this.toCartItemUiModel()!=null){
                        if(this.product.typeProduct == "sameday"){
                            binding.cardProductImageItem.strokeColor = Color.parseColor("#FFDE70")
                        }
                    }

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