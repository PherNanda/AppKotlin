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
    private var categoryList = ArrayList<CategoryOne>()
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

        categoryList = arrayListOf()

        val bundleCategory: Bundle? = arguments
        categoryList = bundleCategory?.getParcelableArrayList("listCategory")!!

        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (!viewModel.showLoading.value!!) {

                binding.loading.visibility = View.VISIBLE
                binding.fragmentProduct.setBackgroundResource(R.drawable.ic_background_loading_transp)
            }
            if (viewModel.showLoading.value!!) {

                binding.loading.visibility = View.GONE

                    viewModel.showEmpty.observe(viewLifecycleOwner) {
                        if (viewModel.showEmpty.value!!){
                            binding.emptyView.visibility = View.VISIBLE
                        }
                    }
            }
        }


        (requireActivity() as MainActivity).binding.imageBack.isVisible = true
        (requireActivity() as MainActivity).binding.imageBack.setOnClickListener {
            //startActivity(Intent(requireContext(), MainActivity::class.java))
            //fragmentManager?.popBackStackImmediate()
            findNavController().navigateUp()
        }

        //change widht search
        /**val params: ViewGroup.LayoutParams = binding.searchLayout.layoutParams!!
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.searchLayout.layoutParams = params**/

        //visibility off samedayFlagBottom
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

        //selected category
        val categoryListAdapterChanges = ArrayList<CategoryItemTagItem>()
        val t =  ArrayList<CategoryOne>()
        val listDistinctyNew = ArrayList<Category>()
        var categorie = CategoryUiModel.CategoryListItem.Category(0L,"",false)

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
                    it.productId

                    println("it.combinations 1 ${it.combinations}")

                }
           },
            onCategoryClickListener = {
                viewModel.selectCategoryTwo(it,t)
            },
            typeProduct = viewModel.getType()!!
        )



        val categoryAdapter = CategoryAdapter {
            viewModel.selectCategoryTwo(it, t)
        }
        val listDistincty = ArrayList<Category>()
        val list = ArrayList<Category>()
        for (item in categoryList) {

            categoryList[0].id?.let { item.name?.let { it1 -> Category(categories = categoryList, id = it, name = it1) } }?.let {
                list.add(
                    it
                )
            }
            println(" item:::: name ${item.name} id ${item.id} category ${item.category} checked ${item.checked}")
            list.distinctBy { Category(it.categories, it.id, it.name) }
        }
        listDistincty.add(list[0])

        categoryList[0].id?.let {
            categoryList[0].name?.let { it1 ->
                CategoryItemTagItem(
                    categories = listDistincty,
                    id = it,
                    name = it1
                )
            }
        }?.let {
            categoryListAdapter.add(
                it
            )
        }
        binding.titleProductsCategory.text = categoryList[0].name.toString()

        listCatAdapter = CategoryItemTagAdapter(categoryList = categoryListAdapter , object : CategoryItemTagAdapter.OptionsMenuClickListener{

            override fun onOptionsMenuClicked(position: Int, category: CategoryUiModel.CategoryListItem.Category, categoryOne: CategoryOne) {
                //performOptionsMenuClick(position)
                println("categoryListAdapter.map { it.categories } ${categoryListAdapter.map { it.categories }}")
                println("CategoryItemTagAdapter position:::: $position")
                println("CategoryItemTagAdapter position:::: category $category")
                println("CategoryItemTagAdapter position:::: categoryOne $categoryOne")

               for (item in categoryListAdapter){
                   for (items in item.categories){
                       for (it in items.categories){
                           if (it.name != category.title){
                               it.checked == "false"
                               println("itemmm two $it")
                               t.add(CategoryOne("",it.id,it.name,"false"))

                           }
                       }
                   }
               }

                t.add(CategoryOne("",categoryOne.id,categoryOne.name,categoryOne.checked))
                categorie = categoryOne.id?.let { it1 ->
                    CategoryUiModel.CategoryListItem.Category(
                        it1.toLong(),categoryOne.name!!,categoryOne.checked.toBoolean())
                }!!

                viewModel.selectCategoryTwo(category, t)

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
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }

       /** if (t.size > 0) {
            listDistinctyNew.add(Category(categories = t, categorie.uid.toString(), categorie.title))

            categoryListAdapterChanges.add(
                CategoryItemTagItem(
                    categories = listDistinctyNew,
                    id = categorie.uid.toString(),
                    name = categorie.title
                )
            )

            categoryListAdapter = categoryListAdapterChanges
            listCatAdapter.notifyDataSetChanged()
        }**/
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