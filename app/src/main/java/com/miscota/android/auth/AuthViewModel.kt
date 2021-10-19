package com.miscota.android.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel : ViewModel() {

    private val _screenState: MutableStateFlow<Screen> = MutableStateFlow(Screen.LoginScreen)
    val screenState = _screenState.asLiveData()

    fun setScreen(screen: Screen) {
        _screenState.value = screen
    }

    enum class Screen {
        IntroScreen,
        LoginScreen,
        SignUpScreen
    }
}