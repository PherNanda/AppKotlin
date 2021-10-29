package com.miscota.android.auth.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miscota.android.R
import com.miscota.android.auth.login.ui.LoggedInUserView
import com.miscota.android.repository.AuthRepository
import com.miscota.android.util.AuthStore
import com.miscota.android.util.toUserModel
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authRepository: AuthRepository,
    private val authStore: AuthStore
) : ViewModel() {

    private val _signupForm = MutableLiveData<SignupFormState>()
    val signupFormState: LiveData<SignupFormState> = _signupForm

    private val _selectedAddress = MutableLiveData<String>()
    val selectedAddress: LiveData<String> = _selectedAddress

    private val _signupResult = MutableLiveData<SignUpResult>()
    val signUpResult: LiveData<SignUpResult> = _signupResult

    fun signUp(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        terms: Boolean,
        newsLetter: Boolean,
    ) {
        if (!terms) {
            _signupForm.value = SignupFormState(termsAcceptedError = R.string.terms_error)
            return
        } else {
            _signupForm.value = SignupFormState(isDataValid = true)
        }
        viewModelScope.launch {
            val result = runCatching {
                authRepository.signup(
                    username = username,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    terms = terms,
                    newsLetter = newsLetter,
                )
            }

            if (result.isSuccess) {
                val user = result.getOrThrow().info.toUserModel()
                authStore.setUser(user)
                authStore.setAutoLoginParam(result.getOrThrow().autologin_param)
                authStore.setAutoLoginParamExpire(result.getOrThrow().autologin_param_expire)
                _signupResult.value =
                    SignUpResult(success = LoggedInUserView(displayName = user.name?:"invitado"))
            } else {
                _signupResult.value = SignUpResult(error = R.string.sign_up_error)
            }
        }
    }

    fun loginDataChanged(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        address: String,
        terms: Boolean,
    ) {
        if (!isUserNameValid(username)) {
            _signupForm.value = SignupFormState(usernameError = R.string.invalid_username)
        } else if (!isUserEmailValid(email)) {
            _signupForm.value = SignupFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _signupForm.value = SignupFormState(passwordError = R.string.invalid_password)
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            _signupForm.value =
                SignupFormState(confirmPasswordError = R.string.invalid_confirm_password)
        } else {
            _signupForm.value = SignupFormState(isDataValid = true)
        }
    }

    fun loadAddress() {
        _selectedAddress.value = authStore.getAddress()?.address ?: return
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.length > 5
    }

    // A placeholder username validation check
    private fun isUserEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    // A placeholder password validation check
    val pattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    //^(?=\w*\d)(?=\w*[A-Za-z])\S{6,}$
    private fun isPasswordValid(password: String): Boolean {
      return pattern.containsMatchIn(password) && password.length > 5
    }

    // A placeholder username validation check
    private fun isConfirmPasswordValid(
        password: String,
        confirmPassword: String,
    ): Boolean {
        return password == confirmPassword
    }

    fun setCurrentLocationFromGeocode(address: android.location.Address) {
        val addressString = address.getAddressLine(0)?.split(",")?.firstOrNull() ?: ""
        _selectedAddress.value = addressString
    }
}
