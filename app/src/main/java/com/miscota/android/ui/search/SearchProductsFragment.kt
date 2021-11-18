package com.miscota.android.ui.search

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.FragmentSearchProductsBinding
import com.miscota.android.ui.productdetail.ProductDetailFragment
import com.miscota.android.ui.productdetail.toProductDetailUiModel
import com.miscota.android.util.RecyclerViewLoadMoreListener
import com.miscota.android.util.autoClean
import com.miscota.android.util.showKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchProductsFragment : Fragment() {
    private var binding by autoClean<FragmentSearchProductsBinding>()

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var listAdapter: SearchProductsAdapter
    private lateinit var listAdapterChanged: SearchProductsAdapterChanged
    private lateinit var requestDetailID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).binding.imageBack.isVisible = true

        val bundleProduct: Bundle? = arguments
        requestDetailID = bundleProduct?.get("requestDetailID").toString()
        viewModel.setRetailId(requestDetailID)

       /** listAdapter = SearchProductsAdapter(
            productClickListener = {
                bundleOf(ProductDetailFragment.PARCELABLE_ARGS_PRODUCT to it.toProductDetailUiModel()).apply {
                    findNavController().navigate(
                        R.id.action_searchProductsFragment_to_productDetailFragment,
                        this
                    )
                }
            },
            loadMoreTopProducts = {
                viewModel.loadMoreTopProducts(viewModel.getType()!!)
            }
        )**/


        listAdapterChanged = SearchProductsAdapterChanged(
            onCategoryClickListener = {
                viewModel.showLoading
            },
            loadMoreTopProducts = {
                viewModel.loadMoreTopProducts(viewModel.getType()!!)
            },
            onProductClickListener = {
                bundleOf(ProductDetailFragment.PARCELABLE_ARGS_PRODUCT to it.toProductDetailUiModel()).apply {
                    findNavController().navigate(
                        R.id.action_searchProductsFragment_to_productDetailFragment,
                        this
                    )
                }
            },
            typeProduct = viewModel.getType()!!
        )

        binding.searchAutoComplete.requestFocus()
        showKeyboard(binding.searchAutoComplete)

        binding.recyclerView.apply {
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            layoutManager = gridLayoutManager
            adapter = listAdapterChanged

            addOnScrollListener(
                RecyclerViewLoadMoreListener(
                    loadMore = {
                        viewModel.loadMoreTopProducts(viewModel.getType()!!)
                    }
                )
            )
            itemAnimator = null
        }

        binding.searchAutoComplete.doAfterTextChanged {
            if (it != null && it.length > MIN_SEARCH) {

                viewModel.showLoading.observe(viewLifecycleOwner) {
                    if (!viewModel.showLoading.value!!) {
                        binding.loadingSearch.visibility = View.VISIBLE
                        binding.fragmentSearch.setBackgroundResource(R.drawable.ic_background_loading_transp)
                    }
                    if (viewModel.showLoading.value!!) {
                        binding.loadingSearch.visibility = View.GONE
                    }
                }

                if(it.length > MIN_SEARCH){
                    viewModel.search(it.toString(),viewModel.getType()!!)
                    binding.titleSearch.visibility = View.VISIBLE

                    binding.titleSearch.text = getString(R.string.search_of, it)

                }

            }
            if(it != null &&  it.length < NOT_SEARCH){
                viewModel.search(it.toString(),viewModel.getType()!!)
                binding.titleSearch.visibility = View.INVISIBLE
                binding.titleSearch.visibility =  View.INVISIBLE

            }

            (requireActivity() as MainActivity).binding.navView.menu.getItem(1).isChecked = true

        }


        viewModel.messageEvent.observe(viewLifecycleOwner) {
            it.consume()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.dataList.observe(viewLifecycleOwner) {
            //listAdapter.submitList(it)
            return@observe
        }

        viewModel.topProductList.observe(viewLifecycleOwner) {
            listAdapterChanged.submitList(it)
        }

    }

    companion object {
        const val MIN_SEARCH = 2
        const val NOT_SEARCH = 3
    }

}