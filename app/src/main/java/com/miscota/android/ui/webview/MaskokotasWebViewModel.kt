package com.miscota.android.ui.webview

import androidx.lifecycle.ViewModel
import com.miscota.android.util.AuthStore

class MaskokotasWebViewModel(private val authStore: AuthStore) :
    ViewModel() {

    fun getProfileURL(): String {

        var url = "https://www.miscota.es/user"

            //var url = "http://192.168.100.117/user?tab=resume&var="+authStore.getAutoLoginParam()+"&domain=www.miscota.es"
            if (!authStore.getAutoLoginParam().isNullOrEmpty()) {
                url += "?tab=resume&var=" + authStore.getAutoLoginParam()
            }

        return url
    }



    fun getOrdersURL(): String {

        var url = "https://www.miscota.es/user?tab=orders"
        //var url = "http://192.168.100.117/user?domain=www.miscota.es"
       if ( !authStore.getAutoLoginParam().isNullOrEmpty() ) {
            url += "&var=" + authStore.getAutoLoginParam()
        }
        return url
    }

    fun isLoggedIn(): Boolean {
        return authStore.isLoggedIn()
    }


}