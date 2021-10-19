package com.miscota.android.ui.productdetail

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.miscota.android.R
import com.miscota.android.databinding.FragmentProductDetailBinding
import com.miscota.android.ui.cart.CartUiModel
import com.miscota.android.ui.cart.toCartItemUiModel
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProductDetailFragment : Fragment() {

    private var binding by autoClean<FragmentProductDetailBinding>()
    private val viewModel by viewModel<ProductDetailViewModel>()

    private var optionAdapter: ProductDetailVariantsAdapter? = null
    private lateinit var imageAdapter: ProductDetailImageAdapter


    private var stock = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments().getParcelable<Product>(PARCELABLE_ARGS_PRODUCT)
        //var productType = viewModel.getType()
        args?.let { product ->
            viewModel.getProductStock(product.productId,viewModel.getType()?:"")
            println("viewModel.getType() ${viewModel.getType()}")
            binding.productType.text = product.productType
            //product.productType = product.productType
            binding.productNameBrand.text = colorMyText(product.brand,0,product.brand.length,
                ContextCompat.getColor(requireContext(), R.color.app_blue))

            binding.productNameBrandTwo.text = colorMyText(product.brand,0,product.brand.length,
                ContextCompat.getColor(requireContext(), R.color.app_blue))
            binding.productName.text = product.productTitle
            binding.descriptionText.text = product.descr
            binding.descriptionTextFirst.text = product.productDescription
            binding.priceText.text = "${String.format("%.2f",product.combinationOptions[0].optionPrice)} €"
            binding.discountText.text = product.discount

            val images = product.imageList.filterNotNull()
            imageAdapter = ProductDetailImageAdapter(images)
            binding.imageViewPager.adapter = imageAdapter
            if (images.size > 1) {
                TabLayoutMediator(
                    binding.tabLayout,
                    binding.imageViewPager
                ) { _, _ -> }.attach()
            }
            //binding.discountText.isVisible = product.hasDiscount
            //binding.oldPriceText.isVisible = product.hasDiscount
            //binding.oldPriceView.isVisible = product.hasDiscount
            addCombinationOptions(product)
            viewModel.eventsManager.viewItemEvent(product,"ProductDetailFragment","onViewCreated")
            val item = viewModel.eventsManager.viewItem(product, product.productTitle)
            val indexItem = viewModel.eventsManager.indexItem(item)
            viewModel.eventsManager.viewItemList(product, indexItem)
            //viewModel.eventsManager.selectItem(item)


        }

        if(viewModel.getType() == getString(R.string.type_ecommerce)){
            //binding.optionsRecyclerView[R.id.samedayProduct].visibility = View.INVISIBLE
            //binding.optionsRecyclerView[0].visibility = View.INVISIBLE
            //binding.optionsRecyclerView[1].visibility = View.INVISIBLE

            //println(binding.optionsRecyclerView)
        }

        val selectedCombination: OptionUiModel.Option? = null
        selectedCombination.let { option ->

            args?.let {
                it.combinationOptions.filter {
                    println(" it.id::  ${it.id}")
                    println(" it.variant::  ${it.variant}")

                    if (option != null) {
                        println(" option.id::  ${option.id}")
                    }
                    it.id == option?.id ?: 0

                }

            }

        }



        binding.addButton.setOnClickListener {
            viewModel.increment()
        }

        // Initializing a String Array
        val qtyNumbers = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","12","13","14","15", "16","17","18","19","20")

        // Initializing an ArrayAdapter
        val adapter = ArrayAdapter(
            requireActivity(), // Context
            R.layout.simple_spinner_item_custom, // Layout
            qtyNumbers // Array
        )

        adapter.setDropDownViewResource(R.layout.simple_dropdown_item_1line_custom)

        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){
                //binding.textVView.text = "Spinner selected : ${parent.getItemAtPosition(position).toString()}"
                val qty = parent.getItemAtPosition(position).toString()
                val qtyProduct = qty.toInt()

                viewModel.incrementCart(qtyProduct)

                println("Spinner selected : ${parent.getItemAtPosition(position).toString()}  - qtyProduct $qtyProduct")
            }

            override fun onNothingSelected(parent: AdapterView<*>){
                // Another interface callback
            }
        }



        binding.botonCart.setOnClickListener {
            //viewModel.increment()

        }

        binding.removeButton.setOnClickListener {
            viewModel.decrement()
        }

        viewModel.optionss.observe(viewLifecycleOwner){
            println(" print observe $it")
            return@observe
        }



        binding.addToCartButton.setOnClickListener {

            if(optionAdapter?.currentList != null){
                optionAdapter?.currentList!!.map { it2 ->
                    if (it2 is OptionUiModel.Option) {
                        if(it2.isChecked){
                            var stock = 0

                            val cartItem = args?.let {
                                viewModel.product?.combinations?.map {
                                    if (it.ref == it2.id)
                                    { stock = it.stock!!
                                    }
                                }
                                CartProduct(
                                    productId = it.productId.toInt(),
                                    title = it.productTitle,
                                    price = it2.price,
                                    oldPrice = it2.price,
                                    discount = it.discount,
                                    image = it.imageList.take(1)[0].orEmpty(),
                                    combinationReference = it2.id,
                                    combinationPrice = it2.optionPrice,
                                    //combinationStock = viewModel.product?.combinations?.get(index = 0)?.stock?: 1,
                                    combinationStock = stock,
                                    combinationUnitsPack = it2.unitsPack?:0,
                                    typeProduct = viewModel.getType()!!,
                                    stockItens = stock,
                                    brand = it.brand,
                                    costSd = viewModel.authStore.getCarriersSd()?.toDouble()?:0.0,
                                    costEco = viewModel.authStore.getCarriersEco()?.toDouble()?:0.0,
                                    totalCost = viewModel.authStore.getCarriers()?.toDouble()?:0.0
                                )
                            }

                                viewModel.addToCart(
                                    requireNotNull(cartItem), requireContext(), it2
                                )

                        }
                    }
                }
            }else if (viewModel.selectedCombination != null){

                val cartItem = args?.let {
                    CartProduct(
                        productId = it.productId.toInt(),
                        title = it.productTitle,
                        price = it.combinationOptions[0].optionPrice.toString(),
                        oldPrice = it.oldPrice,
                        discount = it.discount,
                        image = it.imageList.take(1)[0].orEmpty(),
                        combinationReference = it.combinationOptions.get(index = 0).id,
                        combinationPrice = it.combinationOptions.get(index = 0).optionPrice,
                        combinationStock = viewModel.product?.combinations?.get(index = 0)?.stock ?: 1,
                        combinationUnitsPack = viewModel.selectedCombination?.unitsPack ?: 1,
                        typeProduct = viewModel.getType()!!,
                        stockItens = viewModel.product?.combinations?.get(0)?.stock,
                        brand = it.brand,
                        costSd = viewModel.authStore.getCarriersSd()?.toDouble()?:0.0,
                        costEco = viewModel.authStore.getCarriersEco()?.toDouble()?:0.0,
                        totalCost = viewModel.authStore.getCarriers()?.toDouble()?:0.0
                    )
                }
                viewModel.selectedCombination.let {
                    viewModel.addToCart(
                        requireNotNull(cartItem), requireContext(), it
                    )
                }
            }
            else{
                Toast.makeText(requireContext(), "No hay ninguna opción seleccionada", Toast.LENGTH_SHORT).show()
            }

        }


        //addButton
        viewModel.quantity.observe(viewLifecycleOwner) { qty ->
            binding.quantityTextTwo.text = qty.toString()
            //binding.quantityText.text = qty.toString()

            viewModel.stock.value?.let { stock ->
                if (stock == -1)

                /**binding.addButton.visibility = if (qty >= stock) View.GONE else View.VISIBLE
                binding.addButton.visibility = if (stock <= qty) View.GONE else View.VISIBLE**/

                //binding.addButton.visibility =  View.VISIBLE
                //binding.botonCart.visibility =  View.VISIBLE
                return@let

            }
        }


        //addToCartButton
        viewModel.stock.observe(viewLifecycleOwner) {
            stock = it
            if (it == 0) {
                binding.addToCartButton.isEnabled = false
                binding.addToCartButton.text = getString(R.string.no_disponible)
            } else {
                binding.addToCartButton.isEnabled = true
                binding.addToCartButton.text = getString(R.string.add_to_cart)
            }
        }

        viewModel.messageEvent.observe(viewLifecycleOwner) { event ->
            event.consume()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun colorMyText(inputText:String,startIndex:Int,endIndex:Int,textColor:Int): Spannable {
        val outPutColoredText: Spannable = SpannableString(inputText)
        outPutColoredText.setSpan(
            ForegroundColorSpan(textColor), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return outPutColoredText
    }


    fun isLoggedIn(): Boolean {
        return viewModel.authStore.isLoggedIn()
    }

    fun loadCheckout(): MutableList<CartUiModel.ItemListCheckout> {

        val list: MutableList<CartUiModel.ItemListCheckout> = mutableListOf()
        viewModel.authStore.getCart().map {
            it.toCartItemUiModel()
            println(" it items checkout: $it")

            list.add(
                CartUiModel.ItemListCheckout(
                    qty = it.qty.toString(),
                    price = it.combinationPrice.toString(),
                    type = it.type,
                    ref = it.combinationReference,
                )
            )
        }
        return list
    }



    //añade las options = combinaciones a la vista
    private fun addCombinationOptions(product: Product) {
        binding.optionsRecyclerView.visibility = View.GONE
        binding.optionsLabel.visibility = View.INVISIBLE

        val options = mutableListOf<OptionUiModel>()

        if (product.combinationOptions.size > 1) {
            println("viewModel.product?.combinations?.get(0)?.stockItem ?: 0  "+ (viewModel.product?.combinations?.get(0)?.stock ?: 0)+"")

            product.combinationOptions.forEachIndexed { index, combinationOption ->

                options.add(combinationOption)
                if (index - 1 != options.size) {
                    options.add(OptionUiModel.Spacer)
                }
            }


                optionAdapter = ProductDetailVariantsAdapter { optionId ->
                optionAdapter?.submitList(
                    options.map {
                        if (it is OptionUiModel.Option) {
                            if (it.id == optionId) {

                                //binding.variationPriceText.text = "${it.optionPrice / it.variant.split(" ").firstOrNull()?.toDouble()!!}"
                                //println(" option  ${it.optionPrice} - ${it.variant.split(" ").firstOrNull()?.toDouble()} - ${it.optionPrice / it.variant.split(" ").firstOrNull()?.toDouble()!!} ")
                                 binding.priceText.text = it.price

                                print(" productTypeOption ${it.productTypeOption}")
                                it.copy(isChecked = true)
                            } else {
                                it.copy(isChecked = false)
                            }
                        } else {
                            it
                        }
                    }
                )
                }
                viewModel.selectedCombination?.id?.map{ it3->
                    options.map {
                        if (it is OptionUiModel.Option) {
                            if (it.id == it3.toString()) {
                                viewModel.selectedCombination?.copy(isChecked = true)
                                for( optionProduct in product.combinationOptions )
                                    if (viewModel.selectedCombination!!.id == optionProduct.id) {
                                        viewModel.selectedCombination =
                                            optionProduct
                                    }
                            }
                            else { viewModel.selectedCombination?.copy(isChecked = false)}
                        } else {
                            it
                        }
                }

            }

            optionAdapter?.submitList(options)

            binding.optionsRecyclerView.apply {
                layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = optionAdapter
                itemAnimator = null

            }


            binding.optionsRecyclerView.visibility = View.VISIBLE
            binding.optionsLabel.visibility = View.VISIBLE

        } else if (product.combinationOptions.size == 1) {
            viewModel.selectedCombination = product.combinationOptions[0]
        }
    }


    private fun boldColorMyText(inputText:String,startIndex:Int,endIndex:Int,textColor:Int): Spannable {
        val outPutBoldColorText: Spannable = SpannableString(inputText)
        outPutBoldColorText.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        outPutBoldColorText.setSpan(
            ForegroundColorSpan(textColor), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldColorText
    }



    companion object {
        const val PARCELABLE_ARGS_PRODUCT = "argsProduct"
    }
}