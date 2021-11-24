package com.miscota.android.ui.categories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.FragmentMainCategoriesBinding
import com.miscota.android.databinding.ItemLoaderBinding
import com.miscota.android.util.RecyclerViewLoadMoreListener
import com.miscota.android.util.autoClean
import okhttp3.internal.notify
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainCategoriesFragment : Fragment() {

    private val viewModel by viewModel<MainCategoriesViewModel>()
    private var binding by autoClean<FragmentMainCategoriesBinding>()
    private val bindingLoader by autoClean<ItemLoaderBinding>()

    private lateinit var listAdapter: MainCategoriesItemAdapter

    private var productSomeday: String? = ""
    private var productEcommerce: String? = ""
    private var productType: String = KEY_ECOMMERCE
    private lateinit var requestDetailID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val mainActivity = requireActivity() as MainActivity
        //mainActivity.binding.root.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.white_900))
        (requireActivity() as MainActivity).binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_900))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoriesBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).binding.navView.menu.getItem(1).isChecked = true
        (requireActivity() as MainActivity).binding.imageBack.isVisible = true

        (requireActivity() as MainActivity).binding.imageBack.setOnClickListener {

            //fragmentManager?.popBackStackImmediate()

            //OPTION 1
            startActivity(Intent(requireContext(), MainActivity::class.java))

            //OPTION 2
            /**val params: ViewGroup.LayoutParams = (requireActivity() as MainActivity).binding.headerMain.layoutParams!!
            params.height = 260
            (requireActivity() as MainActivity).binding.headerMain.layoutParams = params

            (requireActivity() as MainActivity).binding.samedayInfoMain.visibility = View.VISIBLE
            (requireActivity() as MainActivity).binding.locationLinearLayoutmain.visibility = View.VISIBLE

            findNavController().navigate(R.id.navigation_home)**/

            //OPTION 3

            /**val params: ViewGroup.LayoutParams = (requireActivity() as MainActivity).binding.headerMain.layoutParams!!
            params.height = 260
            (requireActivity() as MainActivity).binding.headerMain.layoutParams = params

            (requireActivity() as MainActivity).binding.samedayInfoMain.visibility = View.VISIBLE
            (requireActivity() as MainActivity).binding.locationLinearLayoutmain.visibility = View.VISIBLE**/
            //findNavController().navigateUp()

            println("test hacia atras desde de main categories ${requestDetailID}")

            //findNavController().navigate(R.id.action_homeProductsFragment_to_mainCategoriesFragment, bundleEcommerce)
        }


        val params: ViewGroup.LayoutParams = (requireActivity() as MainActivity).binding.logoImage.layoutParams!!
        params.height = 180
        params.width = 300
        (requireActivity() as MainActivity).binding.logoImage.layoutParams = params

        val bundleProduct: Bundle? = arguments
        productSomeday =  bundleProduct?.getString("productSameday")
        productEcommerce =  bundleProduct?.getString("productEcommerce")
        requestDetailID = bundleProduct?.get("requestDetailID").toString()

        println("requestDetailID ${requestDetailID}")

        if (productSomeday!=null)
            productType = productSomeday.toString()
        if (productEcommerce!=null)
            productType = productEcommerce.toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /** binding.allProductsCard.setOnClickListener {
        val ecommerceT = KEY_ECOMMERCE
        val bundleEcommerce = Bundle()
        bundleEcommerce.putString("productEcommerce", ecommerceT)
        bundleEcommerce.putString("requestDetailID", "5")
        viewModel.setType(KEY_ECOMMERCE)

        firebaseAnalytics.logEvent( "screen_ecommerce") {
        param(FirebaseAnalytics.Param.SCREEN_NAME, "screen_ecommerce")
        param(FirebaseAnalytics.Param.SCREEN_CLASS, "HomeProductsFragment")
        param(FirebaseAnalytics.Param.METHOD, "allProductsCard")
        }

        findNavController().navigate(R.id.action_homeProductsFragment_to_mainCategoriesFragment, bundleEcommerce)
        }***/

        //val item = findViewById<View>(R.id.navigation_product)
        //item.visibility = View.INVISIBLE

        /**(requireActivity() as MainActivity).binding.navView.menu.getItem(1).setOnMenuItemClickListener {

        }**/



        listAdapter = MainCategoriesItemAdapter { item ->
                bundleOf(SubCategoriesFragment.KEY_PARCELABLE_CATEGORY to item).also {
                    findNavController().navigate(
                        R.id.action_mainCategoriesFragment_to_subCategoriesFragment,
                        it
                    )
                    println("it maincat::::: $it")
                }
        }
        println("listAdapter.currentList.size onViewCreated ${listAdapter.currentList.size}")



        binding.searchLayout.setOnClickListener {

            val requestDetailId = Bundle()
            requestDetailId.putString("requestDetailID", requestDetailID)
            println("requestDetailId $requestDetailId")
            findNavController().navigate(R.id.action_mainCategoriesFragment_to_searchProductsFragment,requestDetailId)
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = listAdapter

            addOnScrollListener(
                RecyclerViewLoadMoreListener(
                    loadMore = {
                        viewModel.loadMore()
                    }
                )
            )
        }


        viewModel.loadCategoriesType(productType = productType, requestDetailID = requestDetailID)
        viewModel.setType(productType)

        viewModel.setRetailId(requestDetailID)

        println(" productType after loadCategoriesType $productType")
        viewModel.dataList.observe(
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

        viewModel.messageEvent.observe(viewLifecycleOwner) {
            it.consume()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }


        /**if(viewModel.categories.value != null){

        val bundle = Bundle()
        val list : List<MainCategoriesUiModel > = viewModel.categories.value!!
        bundle.putParcelableArrayList("listCategories", list as ArrayList<MainCategoriesUiModel.Item>)
        }**/
    }

    override fun onDestroy() {
        super.onDestroy()

        //println("listAdapter.currentList.size onDestroy ${listAdapter.currentList.size}")

        val mainActivity = requireActivity() as MainActivity
        mainActivity.binding.root.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.app_blue_alpha))
    }



    override fun onStart() {
        super.onStart()
        val mainActivity = requireActivity() as MainActivity
        mainActivity.binding.root.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.white_900))

        println("test volver atras onStartMainCategories")

        (requireActivity() as MainActivity).binding.samedayInfoMain.visibility = View.GONE
        (requireActivity() as MainActivity).binding.locationLinearLayoutmain.visibility = View.GONE

        val params: ViewGroup.LayoutParams = (requireActivity() as MainActivity).binding.headerMain.layoutParams!!
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        (requireActivity() as MainActivity).binding.headerMain.layoutParams = params

        //println("listAdapter.currentList.size onStart ${listAdapter.currentList.size}")

    }

    override fun onDetach() {
        super.onDetach()
        //println("listAdapter.currentList.size onDetach  ${listAdapter.currentList.size}")
    }

    override fun onResume() {
        super.onResume()

        //println("listAdapter.currentList.size onResume  ${listAdapter.currentList.size}")
        //println("listAdapter.currentList.size onResume it  ${listAdapter.currentList.map { it }}")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //println("listAdapter.currentList.size onAttach  no inicializada")
    }

    companion object {
        const val KEY_SAMEDAY: String = "sameday"
        const val KEY_ECOMMERCE: String = "ecommerce"
    }
}