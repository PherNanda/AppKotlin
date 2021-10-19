package com.miscota.android.ui.scheduledordered

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miscota.android.R
import com.miscota.android.databinding.*
import com.miscota.android.ui.NumberPickerFragment

class ScheduledOrderItemAdapter(
    private val activity: FragmentActivity,
    private val deleteItemClickListener: (ScheduledOrderUiModel.Product) -> Unit,
    private val addItemClickListener: (ScheduledOrderUiModel.Product) -> Unit,
    private val removeItemClickListener: (ScheduledOrderUiModel.Product) -> Unit,
    private val editItemClickListener: (ScheduledOrderUiModel.Header) -> Unit,
    private val sendNowButtonClickListener: (ScheduledOrderUiModel.ButtonsItem) -> Unit,
) : ListAdapter<ScheduledOrderUiModel, ScheduledOrderItemAdapter.ListViewHolder>(ListDiffUtil) {

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
                    ItemScheduledOrderProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    deleteItemClickListener = deleteItemClickListener,
                    addItemClickListener = addItemClickListener,
                    removeItemClickListener = removeItemClickListener
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
            ItemType.Header -> {
                ListViewHolder.HeaderViewHolder(
                    activity,
                    ItemScheduledOrderHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    editItemClickListener = editItemClickListener
                )
            }
            ItemType.Buttons -> {
                ListViewHolder.ButtonsViewHolder(
                    ItemScheduledOrderButtonsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    sendNowButtonClickListener = sendNowButtonClickListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            ScheduledOrderUiModel.LoaderItem -> {
                ItemType.Loader.ordinal
            }
            is ScheduledOrderUiModel.Product -> {
                ItemType.Product.ordinal
            }
            ScheduledOrderUiModel.SpacerItem -> {
                ItemType.Spacer.ordinal
            }
            is ScheduledOrderUiModel.Header -> {
                ItemType.Header.ordinal
            }
            is ScheduledOrderUiModel.ButtonsItem -> {
                ItemType.Buttons.ordinal
            }
        }
    }

    enum class ItemType {
        Loader,
        Product,
        Spacer,
        Header,
        Buttons
    }

    sealed class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(item: ScheduledOrderUiModel)

        class LoaderViewHolder(binding: ItemLoaderBinding) : ListViewHolder(binding.root) {
            override fun bind(item: ScheduledOrderUiModel) {
            }
        }

        class SpacerViewHolder(binding: ItemSpacerBinding) : ListViewHolder(binding.root) {
            override fun bind(item: ScheduledOrderUiModel) {
            }
        }

        class ProductViewHolder(
            private val binding: ItemScheduledOrderProductBinding,
            private val addItemClickListener: (ScheduledOrderUiModel.Product) -> Unit,
            private val removeItemClickListener: (ScheduledOrderUiModel.Product) -> Unit,
            private val deleteItemClickListener: (ScheduledOrderUiModel.Product) -> Unit,
        ) : ListViewHolder(binding.root) {

            override fun bind(item: ScheduledOrderUiModel) {
                val uiModel = item as ScheduledOrderUiModel.Product

                binding.priceText.text = "${uiModel.price} €"
                binding.discountText.text = uiModel.discount
                //binding.oldPriceText.text = uiModel.oldPrice
                //binding.quantityText.text = uiModel.quantity.toString()
                binding.productName.text = "${uiModel.productName}  (${uiModel.quantity.toString()})"
                binding.productImage.load(uiModel.image)
            }
        }

        class HeaderViewHolder(
            private val activity: FragmentActivity,
            private val binding: ItemScheduledOrderHeaderBinding,
            private val editItemClickListener: (ScheduledOrderUiModel.Header) -> Unit,
        ) : ListViewHolder(binding.root) {

            override fun bind(item: ScheduledOrderUiModel) {
                val uiModel = item as ScheduledOrderUiModel.Header

                binding.estimatedText.text = uiModel.estimation.substring(0,6) + " €"

                if (uiModel.frequency == 1)  {
                    binding.frequencyText.text =
                            String.format(activity.getString(R.string.scheduled_frequency_unit),  uiModel.frequency)
                } else {
                    binding.frequencyText.text =
                            String.format(activity.getString(R.string.scheduled_frequency_units),  uiModel.frequency)
                }

                binding.nextDeliveryText.text = uiModel.nextDeliver
                //binding.shippingText.text = uiModel.shippingAddress
                binding.nextOrderLabel.text=  uiModel.scheduleNo
                binding.statusText.text = uiModel.status

                binding.editLabel.setOnClickListener {
                   val picker = NumberPickerFragment(
                           20,
                           uiModel.frequency,
                           activity.getString(R.string.cancel),
                           activity.getString(R.string.save)
                   ) {
                       binding.frequencyText.text = if (it == 1)  "$it semana"  else "$it semanas"
                       editItemClickListener(uiModel)
                   }

                    picker.show(
                            activity.supportFragmentManager,
                            NumberPickerFragment::class.java.toString()
                    )
                }
            }
        }

        class ButtonsViewHolder(
            private val binding: ItemScheduledOrderButtonsBinding,
            private val sendNowButtonClickListener: (ScheduledOrderUiModel.ButtonsItem) -> Unit
        ) : ListViewHolder(binding.root) {

            override fun bind(item: ScheduledOrderUiModel) {
                val uiModel = item as ScheduledOrderUiModel.ButtonsItem
                binding.button1.setOnClickListener{
                    sendNowButtonClickListener.invoke(uiModel)
                }
            }
        }
    }

    object ListDiffUtil : DiffUtil.ItemCallback<ScheduledOrderUiModel>() {
        override fun areItemsTheSame(
            oldItem: ScheduledOrderUiModel,
            newItem: ScheduledOrderUiModel
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: ScheduledOrderUiModel,
            newItem: ScheduledOrderUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
