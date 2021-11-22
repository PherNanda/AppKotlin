package com.miscota.android.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import timber.log.Timber

class RecyclerViewLoadMoreListener(
    private val loadMore: () -> Unit,
    private val onScrollStateChange: ((recyclerView: RecyclerView, newState: Int) -> Unit)? = null,
    private val onScrolled: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit)? = null
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        onScrolled?.invoke(recyclerView, dx, dy)

        recyclerView.layoutManager?.let {
            val findLastVisibleItem = fun(lastVisibleItemPositions: IntArray): Int {
                var maxSize = 0
                for (i in lastVisibleItemPositions.indices) {
                    if (i == 0) {
                        maxSize = lastVisibleItemPositions[i]
                    } else if (lastVisibleItemPositions[i] > maxSize) {
                        maxSize = lastVisibleItemPositions[i]
                    }
                }
                return maxSize
            }

            val lastVisibleItem = when (it) {
                is LinearLayoutManager -> {
                    it.findLastVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    findLastVisibleItem(it.findLastVisibleItemPositions(null))
                }
                is GridLayoutManager -> {
                    it.findLastVisibleItemPosition()
                }
                else -> error("Unsupported layout manager class ${it.javaClass}")
            }

            if (lastVisibleItem >= it.itemCount - THRESHOLD) {
                loadMore.invoke()
            }
        } ?: Timber.e("LayoutManager is null")
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        onScrollStateChange?.invoke(recyclerView, newState)
    }

    companion object {
        const val THRESHOLD = 6
    }
}
