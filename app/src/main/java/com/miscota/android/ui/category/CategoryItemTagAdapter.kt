package com.miscota.android.ui.category

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miscota.android.databinding.ItemCategoryTagBinding

class CategoryItemTagAdapter (
    private var categoryList: List<CategoryItemTagItem>,
    private var optionsMenuClickListener: OptionsMenuClickListener
) : RecyclerView.Adapter<CategoryItemTagAdapter.ViewHolder>(){

    interface OptionsMenuClickListener {
        fun onOptionsMenuClicked(position: Int, category: CategoryUiModel.CategoryListItem.Category)
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
            with(categoryList[position]){
                //binding.checkBox.text = this.name.split("}").firstOrNull()

                if ( this.categories[0].categories[position].split("}").firstOrNull() != this.id.split("}").firstOrNull() ) {

                    binding.checkBox.text =
                        this.categories[0].categories[position].split("}").firstOrNull()

                }else{
                    binding.checkBox.visibility = View.GONE
                }

                //binding.checkBox.text = this.categories[position].name
                //binding.checkBoxCard.tag = this.categories[position].categories[position]
                //binding.categoryName.text = this.name.split("}").firstOrNull()
                //binding.categoryName.text = this.categories[position].categories[position]
                println(" this ${this.name}")

                if (this.categories!=null){
                println(" this.categories ${this.categories}") // this 2}], this Pienso, this Comida húmeda, this Dieta Veterinaria
                    println(" categoryList[position]    ${categoryList[position]}")
                    println(" this.id    ${this.id.split("}").firstOrNull()}")
                    println(" categoryList[position].categories    ${categoryList[position].categories}")
                    println(" categoryList[position].name    ${categoryList[position].name.split("}").firstOrNull()}")
                    println(" categoryList[position].id    ${categoryList[position].id.split("}").firstOrNull()}")
                    println(" this.categories[0].categories    ${this.categories[0].categories}")
                    println(" this.categories[0].categories[0]    ${this.categories[0].categories[0].split("}").firstOrNull()}")
                    println(" this.categories[0].categories[position].split    ${this.categories[0].categories[position].split("}").firstOrNull()}")
                }
                binding.checkBox.setOnClickListener {
                    println(" position checkeada $position")

                    //binding.checkBox.text = boldMyText(this.name.split("}").firstOrNull()?:"",0,this.name.split("}").firstOrNull()?.length?:0)
                    this.categories[0].categories[position].split("}").firstOrNull()?.let { it1 ->
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
                    }
                    println(" position it $it $position")

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
        return categoryList.size
    }
}