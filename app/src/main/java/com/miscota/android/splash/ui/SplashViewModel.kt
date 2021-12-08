package com.miscota.android.splash.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miscota.android.api.auth.AuthApi
import com.miscota.android.util.AuthStore
import com.miscota.android.util.Event
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber

/**
 * Created by adrian on 3/6/21.
 * Copyright (c) 2021 EMMA Solutions SL. All rights reserved
 */

class SplashViewModel(
    authApi: AuthApi,
    authStore: AuthStore,
) : ViewModel() {
    val openMainActivityLiveData: LiveData<Event<Boolean>>
        get() = _openMainActivityLiveData
    private val _openMainActivityLiveData = MutableLiveData<Event<Boolean>>()

    init {
        viewModelScope.launch {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "app")
                .addFormDataPart("password", "n>cV4f}8[*Get8*K")
                .build()
            val result = runCatching {
                authApi.getToken(requestBody)
            }

            if (result.isSuccess) {
                authStore.setBearerToken(result.getOrThrow().token)
                Timber.e(result.isSuccess.toString())
            }
            if (result.isFailure){
                Timber.e(result.isFailure.toString())
            }
            val exception = result.exceptionOrNull()
            if (exception != null && exception !is CancellationException) {
                Timber.e(exception.message.toString())
            }
            _openMainActivityLiveData.value = Event(true)
        }
    }
}
