package com.miscota.android.ui.productdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miscota.android.databinding.*


class ProductDetailVariantsAdapter(
    private val itemClickListener: (optionId: String) -> Unit,
) :
    ListAdapter<OptionUiModel,ProductDetailVariantsAdapter.ProductDetailViewHolder>(ListDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductDetailViewHolder {

        return when(ItemType.values()[viewType]){
            ItemType.Spacer -> ProductDetailViewHolder.SpacerViewHolder(
                HoizontalSpaceBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ItemType.Item -> ProductDetailViewHolder.ProductDetailVariantViewHolder(
                ItemProductDetailVariantBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                itemClickListener = itemClickListener,
            )
        }
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        holder.bind(item = getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OptionUiModel.Option -> {
                ItemType.Item.ordinal
            }
            OptionUiModel.Spacer -> {
                ItemType.Spacer.ordinal
            }
        }
    }

    enum class ItemType {
        Spacer,
        Item
    }

    sealed class ProductDetailViewHolder(view: View):RecyclerView.ViewHolder(view){

        abstract fun bind(item: OptionUiModel)

        class ProductDetailVariantViewHolder(
            private val binding: ItemProductDetailVariantBinding,
            private val itemClickListener: (optionId: String) -> Unit,
        ) :
            ProductDetailViewHolder(binding.root) {
            private lateinit var option: OptionUiModel.Option
            init {

                binding.button.setOnClickListener{
                    itemClickListener.invoke(option.id)
                    //binding.priceText.text = boldMyText(option.price,0,option.price.length)
                    if(option.isChecked){

                        binding.priceText.text = "${String.format("%.2f", option.optionPrice)}"

                    }
                    println("product detal variants adapter ")
                    println("button.option.id "+option.id)
                    println("button.option.optionPrice "+option.optionPrice)
                    println("button.option.stock "+option.stock)
                    println("button.option.unitsPack "+option.unitsPack)
                    println("button.option.variant "+option.variant)
                    println("button.option.price "+option.price)
                    println("button.option.isChecked "+option.isChecked)
                    println("button.option.stockItens "+option.stockItens)

                }

            }

            override fun bind(model: OptionUiModel) {
                option = model as OptionUiModel.Option

                binding.priceTextMultiPack.text = model.price
                binding.buttonMultiPack.text = model.unitsPack.toString()

                binding.button.text = model.variant
                binding.imageSameday.visibility = View.INVISIBLE
                binding.samedayProduct.visibility = View.INVISIBLE
                //binding.priceText.text = "${String.format("%.2f", model.price.toDouble())}"
                binding.priceText.text = model.price
                binding.button.isChecked = model.isChecked

                if (option.isChecked){
                    //binding.priceText.text = "${String.format("%.2f",model.price.toDouble())}"
                    //binding.priceText.text = boldMyText(option.price,0,option.price.length)
                }
                println("model.variant "+model.variant)
                println("model.price "+model.price)
                println("model.optionPrice "+model.optionPrice)
                println("model.id "+model.id)
                println("model.isChecked "+model.isChecked)
                println("model.uid "+model.uid)
                println("model.stock "+model.stock)
                println("model.stockItens "+model.stockItens)
                println("model.unitsPack "+model.unitsPack)
                //println("model.uid "+model.)



            }
        }

        class SpacerViewHolder(
            private val binding: HoizontalSpaceBinding
        ):ProductDetailViewHolder(binding.root){
            override fun bind(item: OptionUiModel) {
            }

        }

    }

    object ListDiffUtil : DiffUtil.ItemCallback<OptionUiModel>() {
        override fun areItemsTheSame(
            oldItem: OptionUiModel,
            newItem: OptionUiModel
        ): Boolean {
            return oldItem.uid == newItem.uid
            println("oldItem.uid  "+oldItem.uid)
            println("newItem.uid  "+newItem.uid)
        }

        override fun areContentsTheSame(
            oldItem: OptionUiModel,
            newItem: OptionUiModel
        ): Boolean {
            return oldItem == newItem
        }

    }
}
