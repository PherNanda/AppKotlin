package com.miscota.android.ui.scheduledordered

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.miscota.android.MainActivity
import com.miscota.android.databinding.FragmentScheduledOrderBinding
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScheduledOrderFragment : Fragment() {
    private var binding by autoClean<FragmentScheduledOrderBinding>()

    private val viewModel by viewModel<ScheduleOrderViewModel>()

    private lateinit var listAdapter: ScheduledOrderItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduledOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).binding.imageBack.isVisible = true

        listAdapter = ScheduledOrderItemAdapter(
            requireActivity(),
            addItemClickListener = { item ->

            },
            removeItemClickListener = { item ->

            },
            deleteItemClickListener = { item ->
            },
            sendNowButtonClickListener = { item ->
                //showBottomSheet()
                run {
                    binding.emptyView.visibility = View.VISIBLE
                    viewModel.sendNow(item.autoShipId.toInt())
                }
            },
            editItemClickListener = { item ->
            }

        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }

        // TODO: Dummy data until API issue fixed
        /*listAdapter.submitList(
            listOf(
                ScheduledOrderUiModel.Header(
                    scheduleNo = "Schedule # 1",
                    nextDeliver = "Apr 8, 2021",
                    frequencies = "2 per week",
                    shippingAddress = "Gran Vía, 33, 4º A 36204 Vigo, Pontevedra, España",
                    estimation = "29,2€",
                    status = "Active"
                ),
                ScheduledOrderUiModel.Product(
                    oldPrice = "0,0€",
                    discount = "0,0€",
                    price = "0,0€",
                    productName = "Temporary Product 3",
                    productId = 22,
                    description = "Abc",
                    image = "",
                    quantity = 4
                ),
                ScheduledOrderUiModel.Product(
                    oldPrice = "10,0€",
                    discount = "20,0€",
                    price = "30,0€",
                    productName = "Temporary Product 1",
                    productId = 26,
                    description = "Abc",
                    image = "",
                    quantity = 2
                ), ScheduledOrderUiModel.Product(
                    oldPrice = "120,0€",
                    discount = "5,0€",
                    price = "115,0€",
                    productName = "Temporary Product 2",
                    productId = 28,
                    description = "Abc",
                    image = "",
                    quantity = 1
                ),
                ScheduledOrderUiModel.ButtonsItem,
                ScheduledOrderUiModel.Header(
                    scheduleNo = "Schedule # 2",
                    nextDeliver = "Apr 22, 2021",
                    frequencies = "1 per week",
                    shippingAddress = "Gran Vía, 33, 4º A 36204 Vigo, Pontevedra, España",
                    estimation = "28,2€",
                    status = "Active"
                ),
                ScheduledOrderUiModel.Product(
                    oldPrice = "20,0€",
                    discount = "1,0€",
                    price = "19,0€",
                    productName = "Temporary Product 5",
                    productId = 122,
                    description = "Abc",
                    image = "",
                    quantity = 4
                ),
                ScheduledOrderUiModel.Product(
                    oldPrice = "10,0€",
                    discount = "10,0€",
                    price = "10,0€",
                    productName = "Temporary Product 4",
                    productId = 12,
                    description = "",
                    image = "",
                    quantity = 8
                ),
                ScheduledOrderUiModel.ButtonsItem,
                ScheduledOrderUiModel.SpacerItem
            )
        )*/

        viewModel.items.observe(
            viewLifecycleOwner,
            { list ->
                listAdapter.submitList(list)

                if (list.any()) {
                    binding.emptyView.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.VISIBLE
                }
            }
        )

        viewModel.messageEvent.observe(
            viewLifecycleOwner,
            {
                it.consume()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog()
        dialog.show(
            childFragmentManager,
            BottomSheetDialog::class.java.toString()
        )
    }
}