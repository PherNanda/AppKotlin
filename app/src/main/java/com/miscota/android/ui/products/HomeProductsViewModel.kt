package com.miscota.android.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miscota.android.repository.StoreLocationRepository
import com.miscota.android.ui.store.Store
import com.miscota.android.ui.store.StoreLocationUiModel
import com.miscota.android.ui.store.toStoreUiModel
import com.miscota.android.util.Address
import com.miscota.android.util.AuthStore
import kotlinx.coroutines.launch

class HomeProductsViewModel(private val storeLocationRepository: StoreLocationRepository,
                            private val authStore: AuthStore) : ViewModel() {

    private val _selectedLocation: MutableLiveData<Address> =
        MutableLiveData(authStore.getAddress())
    val selectedLocation: LiveData<Address> = _selectedLocation

    val isSameDayEnabled = MutableLiveData<Boolean>()

    private var _requestID: MutableLiveData<Store> = MutableLiveData()
    val requestID: LiveData<Store> = _requestID

    fun loadSelectedLocation() {
        _selectedLocation.value = authStore.getAddress()
        println(" authStore.getAddress() HomeProductsViewModel ${authStore.getAddressInfo()}")
    }

    fun checkPostalCode(postalCode: String) {
        /**if (postalCode == "25199" || postalCode == "94043") {
            isSameDayEnabled.value = true
            return
        }**/
        println(" postalCode: $postalCode")
        viewModelScope.launch {
            val result = runCatching {
                val response = storeLocationRepository.getSameDayShops(postalCode)
                if (!response.isEmpty()) {
                    isSameDayEnabled.value = true

                    val llist: ArrayList<Store> = arrayListOf(
                        Store(
                            name= response.get(0).name,
                            retail_shop_id = response.get(0).retail_shop_id
                        )
                    )

                    llist.map { it

                        _requestID.value = it
                        println(" List Retail   ${it.name} -- ${it.retail_shop_id}")
                    }

                    authStore.setRetailID(_requestID.value?.retail_shop_id)
                    return@launch
                }
            }
        }

        isSameDayEnabled.value = false
    }




    fun setType(
        type: String,
    ){
        type.let {
            authStore.setType(it)
        }
    }

    fun setRetailId(
        retailId: String
    ){
        retailId.let {
            authStore.setRetailID(it)
        }
    }
}