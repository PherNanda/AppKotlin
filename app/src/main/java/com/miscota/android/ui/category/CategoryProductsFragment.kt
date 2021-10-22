package com.miscota.android.ui.category


import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.FragmentCategoryProductsBinding
import com.miscota.android.ui.productdetail.ProductDetailFragment
import com.miscota.android.ui.productdetail.toProductDetailUiModel
import com.miscota.android.util.RecyclerViewLoadMoreListener
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class CategoryProductsFragment : Fragment() {
    private var binding by autoClean<FragmentCategoryProductsBinding>()

    private val viewModel by viewModel<CategoryViewModel> {
        parametersOf(requireArguments())
    }
    private lateinit var listAdapter: CategoryItemAdapter

    private lateinit var listCatAdapter: CategoryItemTagAdapter
    private var categoryList = ArrayList<String>()
    private var categoryListAdapter = ArrayList<CategoryItemTagItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundleCategory: Bundle? = arguments
        categoryList = bundleCategory?.getStringArrayList("listCategory")!!
        println(" categoryList ${categoryList.size}")
        println(" categoryList.get() ${categoryList[0]}")
        println(" categoryList.get(1) ${categoryList[1].split("}").firstOrNull()}")

        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (!viewModel.showLoading.value!!) {
                binding.loading.visibility = View.VISIBLE
                binding.fragmentProduct.setBackgroundResource(R.drawable.ic_background_loading_transp)
            }
            if (viewModel.showLoading.value!!) {
                binding.loading.visibility = View.GONE
            }
        }


        (requireActivity() as MainActivity).binding.imageBack.isVisible = true
        (requireActivity() as MainActivity).binding.imageBack.setOnClickListener {
            //startActivity(Intent(requireContext(), MainActivity::class.java))
            //fragmentManager?.popBackStackImmediate()
            findNavController().navigateUp()
        }



        //change widht search
        val params: ViewGroup.LayoutParams = binding.searchLayout.layoutParams!!
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.searchLayout.layoutParams = params

        binding.textSamedayFlag.text = boldMyText(getString(R.string.same_day_flag_text),28,40)

        if(viewModel.getType() == "sameday"){
            binding.samedayFlagBottom.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_sameday_flag)
            /****val sdView: View? = null
            val sameDayFlag = sdView?.findViewById<MaterialCardView>(R.id.productsCardSamedayFlag)
            sameDayFlag?.cardForegroundColor(ContextCompat.getDrawable(requireContext(), R.drawable.background_sameday_flag))
            sameDayFlag.c**/

            /**val sdView: View? = null
            val sameDayFlag = sdView?.findViewById<MaterialCardView>(R.id.productsCardSamedayFlag)
            sameDayFlag?.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_sameday_flag)**/
            binding.textSamedayFlag.visibility = View.GONE
        }

        binding.samedayFlagBottom.visibility = View.VISIBLE

        listAdapter = CategoryItemAdapter(
            loadMoreTopProducts = {
                viewModel.loadMoreTopProducts()
            },
            onProductClickListener = {
                bundleOf(ProductDetailFragment.PARCELABLE_ARGS_PRODUCT to it.toProductDetailUiModel()).apply {
                    findNavController().navigate(
                        R.id.action_categoryProductsFragment_to_productDetailFragment,
                        this
                    )

                    println("it.combinations ${it.combinations}")
                    println("it.productId ${it.productId}")
                    var produtId = it.productId

                    println("it.combinations 1 ${it.combinations}")

                }
           },
            onCategoryClickListener = {
                println("selectCategory(it) : ${it.title} it: $it  isChecked: ${it.isChecked}")
                viewModel.selectCategory(it)
            },
            typeProduct = viewModel.getType()!!
        )


        val categoryAdapter = CategoryAdapter {
            println("selectCategory(it) title: ${it.title} it: $it  isChecked: ${it.isChecked}")
            viewModel.selectCategory(it)
        }

        //CategoryItemTagItem(categories=[Category(categories=[2}], Pienso, Comida húmeda, Dieta Veterinaria, Comida}], Comida húmeda], id=2}], name=Comida húmeda)], id=, name=Comida húmeda)

        for (item in categoryList) {
            val list = ArrayList<Category>()

            println(" CategoryProducts categoryList $categoryList categoryList[0] ${categoryList[0]}  item $item ")
            /**
             *CategoryProducts categoryList [2}], Pienso, Comida húmeda, Dieta Veterinaria, Comida}], Pienso] categoryList[0] 2}]  item 2}]
            CategoryProducts categoryList [2}], Pienso, Comida húmeda, Dieta Veterinaria, Comida}], Pienso] categoryList[0] 2}]  item Pienso
            CategoryProducts categoryList [2}], Pienso, Comida húmeda, Dieta Veterinaria, Comida}], Pienso] categoryList[0] 2}]  item Comida húmeda
            CategoryProducts categoryList [2}], Pienso, Comida húmeda, Dieta Veterinaria, Comida}], Pienso] categoryList[0] 2}]  item Dieta Veterinaria
            CategoryProducts categoryList [2}], Pienso, Comida húmeda, Dieta Veterinaria, Comida}], Pienso] categoryList[0] 2}]  item Comida}]
            CategoryProducts categoryList [2}], Pienso, Comida húmeda, Dieta Veterinaria, Comida}], Pienso] categoryList[0] 2}]  item Pienso
             * **/

            list.add(Category(categories = categoryList, id = categoryList[0], name = item))

            val listUnique = list.distinctBy { Category(it.categories, it.id, it.name) }

            categoryListAdapter.add(
                CategoryItemTagItem(
                    categories = list,
                    id = categoryList[0],
                    name = item)
            )
            //binding.titleProductsCategory.text = "$item ${getString(R.string.result_category_of, item)}"
            binding.titleProductsCategory.text = item
        }


        listCatAdapter = CategoryItemTagAdapter(categoryList = categoryListAdapter , object : CategoryItemTagAdapter.OptionsMenuClickListener{

            override fun onOptionsMenuClicked(position: Int, category: CategoryUiModel.CategoryListItem.Category) {
                //performOptionsMenuClick(position)

                //println(" selectCategory(it) title: ${it.title} it: $it  isChecked: ${it.isChecked}")
                println("CategoryItemTagAdapter position:::: $position")
                println("CategoryItemTagAdapter position:::: category $category")
                viewModel.selectCategoryTwo(category)
            }

        })
        listCatAdapter.notifyDataSetChanged()

        binding.brandRecyclerView.apply {

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = listCatAdapter

            itemAnimator = null
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
            addOnScrollListener(
                RecyclerViewLoadMoreListener(
                    loadMore = {
                        viewModel.loadMoreProducts()
                    }
                )
            )
            itemAnimator = null
        }


        binding.searchLayout.setOnClickListener {
            findNavController().navigate(R.id.action_categoryProductsFragment_to_searchProductsFragment)
        }

        viewModel.messageEvent.observe(viewLifecycleOwner) {
            it.consume()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.dataList.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
            println()
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }
    }

    companion object {
        const val KEY_LONG_CATEGORY_ID = "categoryId"
    }

    private fun boldMyText(inputText:String,startIndex:Int,endIndex:Int): Spannable {
        val outPutBoldText: Spannable = SpannableString(inputText)
        outPutBoldText.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldText
    }


}