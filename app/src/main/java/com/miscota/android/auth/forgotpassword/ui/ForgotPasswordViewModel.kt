package com.miscota.android.auth.forgotpassword.ui


import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miscota.android.R
import com.miscota.android.repository.AuthRepository
import com.miscota.android.util.AuthStore
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val authRepository: AuthRepository, private val authStore: AuthStore) :
    ViewModel() {

    private val _forgotPasswordForm = MutableLiveData<ForgotPasswordState>()
    val forgotPasswordForm: LiveData<ForgotPasswordState> = _forgotPasswordForm

    private val _forgotPasswordResult = MutableLiveData<ForgotPasswordResult>()
    val forgotPasswordResult: LiveData<ForgotPasswordResult> = _forgotPasswordResult

    fun recoverPassword(email: String) {
        viewModelScope.launch {
            val result = runCatching {
                authRepository.recover(
                    email = email,
                )
            }

            if (result.isSuccess) {
                val response = result.getOrThrow()
                _forgotPasswordResult.value =
                    ForgotPasswordResult(success = ForgotPasswordUserView(send = response.send))
            } else {
                _forgotPasswordResult.value = ForgotPasswordResult(error = R.string.login_failed)
            }
        }
    }

    fun dataChanged(email: String) {
        if (!isUserNameValid(email)) {
            _forgotPasswordForm.value = ForgotPasswordState(emailError = R.string.invalid_username)
        } else {
            _forgotPasswordForm.value = ForgotPasswordState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }
}
