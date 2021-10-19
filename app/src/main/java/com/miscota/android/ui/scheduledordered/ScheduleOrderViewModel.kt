package com.miscota.android.ui.scheduledordered

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miscota.android.api.autoship.response.AutoShipResponse
import com.miscota.android.repository.AutoShipRepository
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ScheduleOrderViewModel(
    private val autoShipRepository: AutoShipRepository, private val authStore: AuthStore
) : ViewModel() {

    private var pageNumber = 1
    private var shouldFetch = false
    private var job: Job? = null
    private val _items: MutableLiveData<List<ScheduledOrderUiModel>> = MutableLiveData()
    val items: LiveData<List<ScheduledOrderUiModel>> = _items

    private val _messageEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    init {
        loadOrders()
    }

    private fun loadOrders(forceLoad: Boolean = false) {
        val userId = authStore.getUser()?.userId
        if (!forceLoad) {
            val list = _items.value?.toMutableList() ?: mutableListOf()
            list.add(ScheduledOrderUiModel.LoaderItem)
            _items.value = list
        }

        job = viewModelScope.launch {
            val result = runCatching {
                val response = autoShipRepository
                    .fetchAutoShipData((userId ?: 0).toInt())

                val list = (
                        if (!forceLoad) {
                            (_items.value
                                ?: listOf()).filter { it !is ScheduledOrderUiModel.LoaderItem }
                        } else {
                            listOf()
                        }
                        ).toMutableList()

                if (response.isEmpty()) {
                    _items.value = list
                    return@launch
                }

                response.forEach {
                    list.add(it.toScheduledOrderUiModel(getEstimationFromItem(it)))
                    list.addAll(it.items.map { item ->
                        item.toScheduledOrderUiModel()
                    })
                    list.add(it.toScheduledOrderUiModelButtons())
                }

                _items.value = list.toList()
            }

            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                _messageEvent.value = Event(exception.message.toString())

                _items.value =
                    (_items.value
                        ?: listOf()).filter { item -> item !is ScheduledOrderUiModel.LoaderItem }
            }
        }
    }

    fun getEstimationFromItem(item: AutoShipResponse): Double {
        return item.items.sumByDouble { i ->
            i.quantity * i.price
        }
    }

    fun loadMore() {
        if (shouldFetch) {
            shouldFetch = false
            loadOrders()
        }
    }

    private fun forceReload() {
        job?.cancel()
        shouldFetch = false
        pageNumber = 1

        loadOrders(forceLoad = true)
    }

    fun sendNow(autoShipId: Int) {

        job = viewModelScope.launch {
            authStore.getUser()?.let { user ->

                autoShipRepository.forceAutoShipSend(user.userId.toInt(), autoShipId)
            }
        }
    }

    companion object {
        const val PAGE_LIMIT = 20
    }
}