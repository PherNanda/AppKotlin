package com.miscota.android.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.FragmentAllProductsCategoriesBinding
import com.miscota.android.ui.webview.WebViewFragment
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllProductsCategoryFragment : Fragment() {

    private val viewModel by viewModel<AllProductsCategoryViewModel>()
    private var binding by autoClean<FragmentAllProductsCategoriesBinding>()

    private lateinit var listAdapter: AllProductsCategoryItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.binding.root.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.white_900))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllProductsCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadSubCategories()

        listAdapter = AllProductsCategoryItemAdapter {
            findNavController().navigate(
                R.id.action_allProductsCategoryFragment_to_webViewFragment, bundleOf(
                    WebViewFragment.KEY_STRING_WEB_VIEW_URL to it.categoryUrl
                )
            )
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            //layoutManager = GridLayoutManager(requireContext(),2)
            adapter = listAdapter
        }

        viewModel.categories.observe(
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

    override fun onDestroy() {
        super.onDestroy()
        val mainActivity = requireActivity() as MainActivity
        mainActivity.binding.root.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.app_blue_alpha))
    }

    companion object {
        const val KEY_PARCELABLE_CATEGORY = "category"
    }
}