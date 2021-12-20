package com.miscota.android.auth


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.miscota.android.util.AuthStore
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel(val authStore: AuthStore) : ViewModel() {

    private val _screenState: MutableStateFlow<Screen> = MutableStateFlow(Screen.LoginScreen)
    val screenState = _screenState.asLiveData()

    private val _statusConnect: MutableLiveData<Boolean> = MutableLiveData(false)
    val statusConnect: LiveData<Boolean> = _statusConnect

    fun setScreen(screen: Screen) {
        _screenState.value = screen
    }

    enum class Screen {
        IntroScreen,
        LoginScreen,
        SignUpScreen
    }

    init {
        _statusConnect.value = authStore.getStatus()
        println("_statusConnect.value authviewmodel ${_statusConnect.value}")
    }
}