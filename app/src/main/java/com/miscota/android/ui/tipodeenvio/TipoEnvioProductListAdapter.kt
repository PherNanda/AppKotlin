package com.miscota.android.ui.tipodeenvio

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miscota.android.R
import com.miscota.android.databinding.ItemTypeOrderProductBinding
import com.miscota.android.ui.cart.toCartItemUiModel
import com.miscota.android.util.AuthStore
import com.miscota.android.util.CartItem

class TipoEnvioProductListAdapter (private val context: Context,
                                   private val itensCart: List<CartItem>,
                                   private val numberImage: IntArray,
                                   private val authStore: AuthStore,
) : RecyclerView.Adapter<TipoEnvioProductListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTypeOrderProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TipoEnvioProductListAdapter.ViewHolder {
        val binding = ItemTypeOrderProductBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TipoEnvioProductListAdapter.ViewHolder, position: Int) {
        with(holder){
            with(itensCart[position]){

            binding.productImage.load(itensCart[position].product.image){
                error(R.color.placeholder)
                placeholder(R.color.placeholder)
            }

            binding.brandProductName.text = itensCart[position].product.brand
            binding.productName.text = itensCart[position].product.title

            binding.imageProduct.load(itensCart[position].product.image){
                error(R.color.placeholder)
                placeholder(R.color.placeholder)
            }

            binding.titleProduct.text = "${itensCart[position].product.brand}, ${itensCart[position].product.title} "
            binding.variationText.text = "${itensCart[position].product.combinationPrice} €"

            if (itensCart[position].product.typeProduct =="ecommerce"){

                val textEcommerce = "Entrega estándar"

                binding.button1Up.isChecked = true
                binding.button1Up.text = boldMyText(textEcommerce,0, textEcommerce.length)
                binding.button2Up.visibility = View.INVISIBLE
            }
            if (itensCart[position].product.typeProduct =="sameday"){
                
                val textSameday = "Entrega hoy"

                binding.button2Up.isChecked = true
                binding.button2Up.text = boldMyText(textSameday,0, textSameday.length)
                binding.button1Up.visibility = View.INVISIBLE
            }

                if (this.toCartItemUiModel()!=null){

                    println("test list image ${this.product.image}")
                    println("test list title ${this.product.title}")
                    println("test list brand ${this.product.brand}")

                }
            }
        }
    }

    private fun boldMyText(inputText:String,startIndex:Int,endIndex:Int): Spannable {
        val outPutBoldText: Spannable = SpannableString(inputText)
        outPutBoldText.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldText
    }

    override fun getItemCount(): Int {
        return itensCart.size
    }

}