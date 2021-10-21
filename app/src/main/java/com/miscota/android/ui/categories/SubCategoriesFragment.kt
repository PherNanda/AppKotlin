package com.miscota.android.ui.categories


import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.FragmentSubCategoriesBinding
import com.miscota.android.ui.category.CategoryProductsFragment
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel


class SubCategoriesFragment : Fragment() {

    private val viewModel by viewModel<SubCategoriesViewModel>()
    private var binding by autoClean<FragmentSubCategoriesBinding>()

    private lateinit var listAdapter: SubCategoriesItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item =
            requireNotNull(
                requireArguments().getParcelable<MainCategoriesUiModel.Item>(
                    KEY_PARCELABLE_CATEGORY
                )
            )

        (requireActivity() as MainActivity).binding.imageBack.isVisible = true
        (requireActivity() as MainActivity).binding.imageBack.setOnClickListener {
            //startActivity(Intent(requireContext(), MainActivity::class.java))
            //fragmentManager?.popBackStackImmediate()
            findNavController().navigateUp()
        }

        binding.allCategoriesLabel.text = getString(R.string.all_categories_of, item.title)


        println(" item.subItens sucategoryFragment: ${item.subItems}")

        println(" item.title: ${item.title}")

        println(" item.subItems.get(0).title: ${item.subItems[0].title}")
        println(" item.subItems.get(1).title: ${item.subItems[1].title}")
        println(" item.subItems.size: ${item.subItems.size}")


        val subCategory = ArrayList<String>()


        val subCat = ArrayList<MainCategoriesUiModel.Item>()
        for(iten in item.subItems)

             subCat.add(iten)




        viewModel.loadSubCategories(item.subItems)

       /** listAdapter = SubCategoriesItemAdapter {
            bundleOf(CategoryProductsFragment.KEY_LONG_CATEGORY_ID to it.id).also {
                findNavController().navigate(
                    R.id.action_subCategoriesFragment_to_categoryProductsFragment,
                    it
                )
                println("it subcat: $it")

            }
        }**/


        val test: MutableMap<String, List<MainCategoriesUiModel.Item>> = mutableMapOf()
        val testTwo = ArrayList<MainCategoriesUiModel.Item>()
        val testT = ArrayList<String>()
        subCat.map { it1->
            it1.title


            test[it1.title] = it1.subItems

            println(" it.title... ${it1.title}")
            println(" it.subItems... ${it1.subItems}")

            it1.subItems.map { it2->
                testT.add(it2.title)
            }


        listAdapter = SubCategoriesItemAdapter { it ->
            bundleOf(CategoryProductsFragment.KEY_LONG_CATEGORY_ID to it.id).also {
               /** findNavController().navigate(
                    R.id.action_subCategoriesFragment_to_categoryProductsFragment,
                    it
                )**/
                println("it subcatsss:::::1 $it")
                println(" subcatsss:::::2  $id")
                println(" subcatsss sub  $it")
                it.toString().split("=")[1].let { it2 -> subCategory.add(it2) }

            }

            //val popup = PopupMenu(requireActivity(), binding.recyclerView)
            val popup = PopupMenu(requireActivity(), binding.subCatPopMenuCard)

           popup.menuInflater
                .inflate(R.menu.sub_category, popup.menu)

            popup.menu.add(boldColorMyText(it.title,0,it.title.length, ContextCompat.getColor(requireContext(), R.color.blue_new_app)))
            for (itens in it.subItems) {
                popup.menu.add(itens.title)
                subCategory.add(itens.title)
            }
            //subCategory.add(itens.title)
            val text = "Todo ${it.title}"
            popup.menu.add(colorMyText(text,0,text.length, ContextCompat.getColor(requireContext(), R.color.blue_new_app)))


            popup.setOnMenuItemClickListener { item ->

                bundleOf(CategoryProductsFragment.KEY_LONG_CATEGORY_ID to it.title).also { it ->
                    println("  itCategoryProductsFragment $it")
                    subCategory.add(it.toString().split("=")[1])
                    subCategory.add(item.title.toString())
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("listCategory", subCategory as java.util.ArrayList<out Parcelable> )

                    findNavController().navigate(
                        R.id.action_subCategoriesFragment_to_categoryProductsFragment,
                        bundle
                    )
                    println("it subcatssSetOnClicks: $it")
                }

                //val fragment = CategoryProductsFragment()
                //fragment.arguments = bundle

                /**Toast.makeText(
                    requireActivity(),
                    "You Clicked : " + item.title,
                    Toast.LENGTH_SHORT
                ).show()+*/
                true
            }


            popup.show()

            println("it subcatsss dosss: $it")
            println(" it.subItems.size ${it.subItems.size}")
            println("  subbsssItem ${it.subItems}")

        }

        }
        /**listAdapter = SubCategoriesItemAdapter {
            bundleOf(CategoryProductsFragment.KEY_LONG_CATEGORY_ID to it.subItems).also {
                findNavController().navigate(
                    R.id.action_subCategoriesFragment_to_categoryProductsFragment,
                    it
                )
                println("it subcatsss: $it")

            }
        }**/


        val adapterT = ArrayAdapter(
            requireActivity(), // Context
            android.R.layout.simple_spinner_item, // Layout
            testT
        )

        adapterT.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        binding.recyclerViewSub.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter

        }

        binding.searchLayout.setOnClickListener {
            findNavController().navigate(R.id.action_subCategoriesFragment_to_searchProductsFragment)
        }

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

        viewModel.messageEvent.observe(
            viewLifecycleOwner,
            {
                it.consume()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun colorMyText(inputText:String,startIndex:Int,endIndex:Int,textColor:Int): Spannable {
        val outPutColoredText: Spannable = SpannableString(inputText)
        outPutColoredText.setSpan(
            ForegroundColorSpan(textColor), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return outPutColoredText
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
        const val KEY_PARCELABLE_CATEGORY = "category"
    }



}
