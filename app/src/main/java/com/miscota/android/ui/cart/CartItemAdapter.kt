package com.miscota.android.ui.cart


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miscota.android.R
import com.miscota.android.databinding.*
import com.miscota.android.ui.checkoutpayment.PaymentMethod
import com.miscota.android.util.AuthStore
import kotlin.collections.ArrayList


class CartItemAdapter(
    val deleteItemClickListener: (CartUiModel.Item) -> Unit,
    private val addItemClickListener: (CartUiModel.Item) -> Unit,
    private val removeItemClickListener: (CartUiModel.Item) -> Unit,
    private val type: (CartUiModel.Item) -> Unit,
    private val authStore: AuthStore,
    private val carriers: (CartUiModel.SummaryItem) -> Unit,
    private val carriersSd: (CartUiModel.SummaryItem) -> Unit,
    private val carriersItem: () -> Unit,
    private val carriersSdItem: (CartUiModel.Item) -> Unit,
    private var types: ArrayList<String>?,
    private val payment: (CartUiModel.SummaryItem) -> Unit,
    private val deliveredNextDay: (CartUiModel.SummaryItem) -> Unit
): ListAdapter<CartUiModel, CartItemAdapter.ListViewHolder>(ListDiffUtil)  {


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
                    ItemCartBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    deleteItemClickListener = deleteItemClickListener,
                    addItemClickListener = addItemClickListener,
                    removeItemClickListener = removeItemClickListener,
                    carriersItem = carriersItem,
                    carriersSdItem = carriersSdItem,
                    type = type,
                    types = types,
                    authStore = authStore,
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
            ItemType.Summary -> {
                ListViewHolder.ItemSummaryViewHolder(
                    ItemCartSummaryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                )
            }

            ItemType.Subtotal -> {
                ListViewHolder.ItemSubtotalViewHolder(
                    ItemCartSubtotalBinding.inflate(
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
            CartUiModel.LoaderItem -> {
                ItemType.Loader.ordinal
            }
            is CartUiModel.Item -> {
                ItemType.Item.ordinal
            }
            CartUiModel.SpacerItem -> {
                ItemType.Spacer.ordinal
            }
            is CartUiModel.SummaryItem -> {
                ItemType.Summary.ordinal
            }
            is CartUiModel.Subtotal -> {
                ItemType.Subtotal.ordinal
            }

        }
    }

    enum class ItemType {
        Loader,
        Item,
        Spacer,
        Summary,
        Subtotal
    }

    sealed class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(item: CartUiModel)

        class LoaderViewHolder(binding: ItemLoaderBinding) : ListViewHolder(binding.root) {
            override fun bind(item: CartUiModel) {
            }
        }

        class SpacerViewHolder(binding: ItemSpacerBinding) : ListViewHolder(binding.root) {
            override fun bind(item: CartUiModel) {
            }
        }

        class ItemViewHolder(
            private val binding: ItemCartBinding,
            private val addItemClickListener: (CartUiModel.Item) -> Unit,
            private val removeItemClickListener: (CartUiModel.Item) -> Unit,
            private val deleteItemClickListener: (CartUiModel.Item) -> Unit,
            private val carriersItem: () -> Unit,
            private val carriersSdItem: (CartUiModel.Item) -> Unit,
            private val type: (CartUiModel.Item) -> Unit,
            private var types: ArrayList<String>?,
            private val authStore: AuthStore
        ) : ListViewHolder(binding.root) {

            override fun bind(item: CartUiModel) {
                val uiModel = item as CartUiModel.Item
                binding.productName.text = uiModel.productName
                binding.priceText.text = String.format(String.format("%.2f", uiModel.price)+" €")


                binding.quantityText.text = uiModel.quantity.toString()
                binding.productTypeCart.text =
                if (uiModel.type == "sameday") "entrega hoy" else "entrega estándar"

                if (uiModel.type == "sameday"){
                    binding.samedayIn.visibility = View.VISIBLE
                }

                types?.add(uiModel.type ?: "")

                binding.productImage.load(uiModel.image) {
                    error(R.color.placeholder)
                    placeholder(R.color.placeholder)
                }

                binding.brandProductName.text = uiModel.brand

                binding.addButton.setOnClickListener {
                    val quantity = binding.quantityText.text.toString().toInt() + 1
                    binding.quantityText.text = quantity.toString()
                    addItemClickListener.invoke(uiModel.copy(quantity = quantity))
                    carriersItem.invoke()
                    binding.removeButton.isEnabled = true
                    //println(" quantity.. $quantity")
                }

                binding.removeButton.setOnClickListener {
                    val quantity = binding.quantityText.text.toString().toInt() - 1
                    if (quantity > 0) {
                        binding.quantityText.text = quantity.toString()
                        removeItemClickListener.invoke(uiModel.copy(quantity = quantity))

                    }
                }

                //position

                binding.deleteButton.setOnClickListener {
                    deleteItemClickListener.invoke(uiModel)
                }

            }
        }

        class ItemSummaryViewHolder(
            private val binding: ItemCartSummaryBinding,
        ) : ListViewHolder(binding.root) {

            lateinit var paymentMethod: PaymentMethod

            override fun bind(item: CartUiModel) {
                val uiModel = item as CartUiModel.SummaryItem

            }
        }

        class ItemSubtotalViewHolder(
            private val binding: ItemCartSubtotalBinding
        ) : ListViewHolder(binding.root) {

            override fun bind(item: CartUiModel) {
                val uiModel = item as CartUiModel.Subtotal

                binding.priceText.text = "${String.format("%.2f", item.subtotal)} €"
                binding.productsLabelCartText.text = "(${uiModel.totalProducts})"
            }
        }

    }
        object ListDiffUtil : DiffUtil.ItemCallback<CartUiModel>(){
            override fun areItemsTheSame(oldItem: CartUiModel, newItem: CartUiModel): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: CartUiModel, newItem: CartUiModel): Boolean {
                return oldItem == newItem
            }
        }
}
