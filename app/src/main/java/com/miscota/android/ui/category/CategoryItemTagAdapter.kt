package com.miscota.android.ui.category


import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miscota.android.databinding.ItemCategoryTagBinding

class CategoryItemTagAdapter (
    private var categoryList: List<CategoryItemTagItem>,
    private var optionsMenuClickListener: OptionsMenuClickListener
) : RecyclerView.Adapter<CategoryItemTagAdapter.ViewHolder>(){

    interface OptionsMenuClickListener {
        fun onOptionsMenuClicked(position: Int, category: CategoryUiModel.CategoryListItem.Category, categoryOne: CategoryOne)
    }

    inner class ViewHolder(val binding: ItemCategoryTagBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryItemTagAdapter.ViewHolder {
        val binding = ItemCategoryTagBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryItemTagAdapter.ViewHolder, position: Int) {
        with(holder){
            with(categoryList[0].categories[0].categories[0]) {

                binding.checkBox.text = categoryList[0].categories[0].categories[position].name

                println("::::categoryList[0].categories[0].categories[position] ${categoryList[0].categories[0].categories[position]}")

                if (categoryList[0].categories[0].categories[position].checked == "true"){
                    binding.checkBox.isChecked = true
                }
                if (categoryList[0].categories[0].categories[position].checked == "false") {
                    binding.checkBox.isChecked = false
                }

            }

            binding.checkBox.setOnClickListener {
                println(" position checkeada $position")

                //binding.checkBox.text = boldMyText(this.name.split("}").firstOrNull()?:"",0,this.name.split("}").firstOrNull()?.length?:0)
                /**this.categories[0].categories[position].name.split("}").firstOrNull()?.let { it1 ->
                categoryList[position].id.split("}").firstOrNull()?.let { it2 ->
                CategoryUiModel.CategoryListItem.Category(
                uid = it2.toLong(),
                title = it1,
                isChecked = true

                )
                }
                }?.let { it2 ->
                optionsMenuClickListener.onOptionsMenuClicked(position,
                category = it2
                )
                }**/
                val cOne: CategoryOne
                println(" position it $it $position")
                categoryList[0].categories[0].categories[position].name.let { it1 ->
                    categoryList[0].categories[0].categories[position].id.let { it2 ->
                        cOne = CategoryOne(
                            category = it1,
                            id = it2!!,
                            name = it1!!,
                            checked = "true"

                        )
                    }
                }


                categoryList[0].categories[0].categories[position].name.let { it1 ->
                    categoryList[0].categories[0].categories[position].id.let { it2 ->
                        CategoryUiModel.CategoryListItem.Category(
                            uid = it2!!.toLong(),
                            title = it1!!,
                            isChecked = true

                        )
                    }
                }.let { it2 ->
                    optionsMenuClickListener.onOptionsMenuClicked(
                        position,
                        category = it2,
                        categoryOne = cOne
                    )
                }

            }
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

    private fun boldMyText(inputText:String,startIndex:Int,endIndex:Int): Spannable {
        val outPutBoldColorText: Spannable = SpannableString(inputText)
        outPutBoldColorText.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldColorText
    }

    private fun boldOffMyText(inputText:String,startIndex:Int,endIndex:Int): Spannable {
        val outPutBoldColorText: Spannable = SpannableString(inputText)
        outPutBoldColorText.setSpan(
            StyleSpan(Typeface.NORMAL), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldColorText
    }


    override fun getItemCount(): Int {
        println(" categoryList[0].categories[0].categories.size ${categoryList[0].categories[0].categories.size}")
        return categoryList[0].categories[0].categories.size
    }
}