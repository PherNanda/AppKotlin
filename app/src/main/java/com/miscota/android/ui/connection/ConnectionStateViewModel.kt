package com.miscota.android.ui.connection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miscota.android.api.auth.AuthApi
import com.miscota.android.util.AuthStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber

class ConnectionStateViewModel
       ( val authStore: AuthStore,
         val  authApi: AuthApi) : ViewModel() {

    private val _statusConnectApi: MutableLiveData<Boolean> = MutableLiveData(false)
    val statusConnectApi: LiveData<Boolean> = _statusConnectApi

    private val _checkAndChangeResult = MutableLiveData(false)
    val checkAndChangeResult: LiveData<Boolean> = _checkAndChangeResult

    var showLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    var tryConnectApiAgain: MutableLiveData<Boolean> = MutableLiveData(false)
    private var _tryConnectApifailed: MutableLiveData<Boolean> = MutableLiveData(false)
    var tryConnectApifailed: MutableLiveData<Boolean> = _tryConnectApifailed
    var totalRetry: MutableLiveData<Int> = MutableLiveData(0)
    val sumaIntento = 1

    init {

        if (authStore.getStatus() && authStore.getInternetOn())
        {
           totalRetry.value = totalRetry.value?.plus(sumaIntento)
           checkAndChangeStatus()
        }
    }

    fun checkAndChangeStatus() {

        viewModelScope.launch {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "app")
                .addFormDataPart("password", "n>cV4f}8[*Get8*K")
                .build()
            val result = runCatching {
                authApi.getToken(requestBody)
            }
            delay(1000L)
            if (requestBody.size == 0) {
                showLoading.value = false
                return@launch
            }

            if (result.isSuccess) {
                totalRetry.value = totalRetry.value?.plus(sumaIntento)
                showLoading.value = false
                tryConnectApiAgain.value = false
                _tryConnectApifailed.value = false
                authStore.setBearerToken(result.getOrThrow().token)
                Timber.e(result.isSuccess.toString())
            }
            if (result.isFailure){
                showLoading.value = false
                _tryConnectApifailed.value = true
                Timber.e(result.isFailure.toString())
            }
            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                totalRetry.value = totalRetry.value?.plus(sumaIntento)
                showLoading.value = false
                _tryConnectApifailed.value = true
                Timber.e(exception.message.toString())
            }
        }
        _statusConnectApi.value = authStore.getStatus()
    }
}