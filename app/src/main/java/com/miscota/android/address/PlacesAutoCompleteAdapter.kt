package com.miscota.android.address

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.BaseAdapter
import android.widget.Filter

/**
 * Adapter for [AutoCompleteTextView] for displaying placesSuggestion autocomplete suggestions.
 */
class PlacesAutoCompleteAdapter(context: Context) :
    ArrayAdapter<PlaceSuggestionUiModel>(context, android.R.layout.simple_dropdown_item_1line) {

    private val data = mutableListOf<PlaceSuggestionUiModel>()

    override fun getItem(position: Int): PlaceSuggestionUiModel? {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: -1L
    }

    override fun getCount(): Int {
        return data.count()
    }

    override fun getFilter(): Filter {
        return PlacesFilter(data, this)
    }

    override fun addAll(collection: Collection<PlaceSuggestionUiModel>) {
        data.addAll(collection)
    }

    override fun add(item: PlaceSuggestionUiModel?) {
        item?.let {
            data.add(item)
        }
    }

    override fun addAll(vararg items: PlaceSuggestionUiModel?) {
        data.addAll(items.filterNotNull())
    }

    override fun clear() {
        data.clear()
    }

    /**
     * Filter implementation for [PlaceSuggestionUiModel] which returns all results.
     */
    private class PlacesFilter(
        private val items: List<PlaceSuggestionUiModel>,
        private val adapter: BaseAdapter
    ) : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            constraint?.let {
                filterResults.values = items
                filterResults.count = items.count()
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (constraint != null) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyDataSetInvalidated()
            }
        }
    }
}
