package com.miscota.android.ui.search

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getColorStateList
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
        println(" search requestDetailID $requestDetailID")

        listAdapter = SearchProductsAdapter(
            productClickListener = {
                bundleOf(ProductDetailFragment.PARCELABLE_ARGS_PRODUCT to it.toProductDetailUiModel()).apply {
                    findNavController().navigate(
                        R.id.action_searchProductsFragment_to_productDetailFragment,
                        this
                    )
                }
            },
        )

        binding.searchAutoComplete.requestFocus()
        showKeyboard(binding.searchAutoComplete)

        binding.recyclerView.apply {
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            layoutManager = gridLayoutManager
            adapter = listAdapter

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
            if (it != null) {

                viewModel.showLoading.observe(viewLifecycleOwner) {
                    if (!viewModel.showLoading.value!!) {
                        binding.loadingSearch.visibility = View.VISIBLE
                        binding.fragmentSearch.setBackgroundResource(R.drawable.ic_background_loading_transp)
                    }
                    if (viewModel.showLoading.value!!) {
                        binding.loadingSearch.visibility = View.GONE
                    }
                }

                if(it.length > 2){
                    viewModel.search(it?.toString() ?: "",viewModel.getType()!!)

                    println("it $it")
                    binding.titleSearch.visibility = View.VISIBLE

                    binding.titleSearch.text = getString(R.string.search_of, it)

                }
            }

            (requireActivity() as MainActivity).binding.navView.menu.getItem(1).isChecked = true

        }


        viewModel.messageEvent.observe(viewLifecycleOwner) {
            it.consume()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.topProductList.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
        }
    }

}