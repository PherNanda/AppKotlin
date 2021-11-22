package com.miscota.android.auth.login.ui

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miscota.android.R
import com.miscota.android.api.auth.response.LoginResponse
import com.miscota.android.repository.AuthRepository
import com.miscota.android.util.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val authRepository: AuthRepository, private val authStore: AuthStore) :
    ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    var showLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    fun login(username: String, password: String) {

        try{

        viewModelScope.launch {
            val result = runCatching {
                authRepository.login(
                    userName = username,
                    password = password,
                )
            }


            if (result != null ) {

                if (result.isSuccess) {

                    val user = result.getOrThrow().info.toUserModel()

                    val error = result.getOrThrow().error?.toErrorModel()

                    if (user.name?.isNotEmpty() == true) {

                        println(" user ${user.name}")
                        authStore.setUser(user)
                        authStore.setAutoLoginParam(result.getOrThrow().autologin_param)
                        authStore.setAutoLoginParamExpire(result.getOrThrow().autologin_param_expire)
                        //authStore.setAutoLoginParamExpire("2021-11-11 11:15:59")

                        val address: List<LoginResponse.Address>? =
                            result.getOrThrow().address ?: return@launch

                        if (address != null && address.size > 0) {
                            authStore.setAddressInfo(address.get(0).address?.let {
                                address[0].region?.let { it1 ->
                                    address.get(0).postcode?.let { it2 ->
                                        address[0].city?.let { it3 ->
                                            Address(
                                                address = it,
                                                lat = 0.0,
                                                lng = 0.0,
                                                state = it1,
                                                postalCode = it2,
                                                city = it3,
                                                region = address[0].region,
                                                phone = address[0].phone,
                                                countryId = address[0].countryId,
                                                countryCode = address[0].countryCode,
                                                countrylang = address[0].countrylang,
                                                countryName = address[0].countryName,
                                                addressNumber = it
                                            )
                                        }
                                    }
                                }

                            })

                            authStore.setAddress(address.get(0).address?.let {
                                address[0].region?.let { it1 ->
                                    address.get(0).postcode?.let { it2 ->
                                        address[0].city?.let { it3 ->
                                            Address(
                                                address = it,
                                                lat = 0.0,
                                                lng = 0.0,
                                                state = it1,
                                                postalCode = it2,
                                                city = it3,
                                                region = address[0].region,
                                                phone = address[0].phone,
                                                countryId = address[0].countryId,
                                                countryCode = address[0].countryCode,
                                                countrylang = address[0].countrylang,
                                                countryName = address[0].countryName,
                                                addressNumber = it
                                            )
                                        }
                                    }
                                }

                            })

                            authStore.addRecentAddressInfo(address.get(0).address?.let {
                                address[0].region?.let { it1 ->
                                    address.get(0).postcode?.let { it2 ->
                                        address[0].city?.let { it3 ->
                                            Address(
                                                address = it,
                                                lat = 0.0,
                                                lng = 0.0,
                                                state = it1,
                                                postalCode = it2,
                                                city = it3,
                                                region = address[0].region,
                                                phone = address[0].phone,
                                                countryId = address[0].countryId,
                                                countryCode = address[0].countryCode,
                                                countrylang = address[0].countrylang,
                                                countryName = address[0].countryName,
                                                addressNumber = it
                                            )
                                        }
                                    }
                                }

                            })
                        }
                        _loginResult.value =
                            LoginResult(
                                success = LoggedInUserView(
                                    displayName = user.name ?: "invitado"
                                )
                            )
                    }
                    if (error != null) {

                        if (error.error.isNotEmpty()) {
                            println(" error.error ${error.error}")
                        }

                    }
                }
                if (result.isFailure){

                    _loginResult.value = LoginResult(error = R.string.login_with_email_failed)
                    println(" failure login_with_email_failed ${_loginResult.value}")
                }
            } else {
                _loginResult.value = LoginResult(error = R.string.login_with_email_failed)
            }
        }

        }catch (e: HttpException){
            println(" Error Login ${e.message}")
        }
    }

    fun loginWithFacebook(name: String, email: String, accessToken: String) {
        viewModelScope.launch {
            val result = runCatching {
                authRepository.facebookLogin(
                    username = name,
                    email = email,
                    token = accessToken,
                )
            }

            //if (result.isSuccess) {
           if (result!=null) {
                val user = result.getOrThrow().info.toUserModel()
                authStore.setUser(user)
                authStore.setAutoLoginParam(result.getOrThrow().autologin_param)
                authStore.setAutoLoginParamExpire(result.getOrThrow().autologin_param_expire)

                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = user.name?:"invitado"))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginWithGoogle(name: String, email: String, accessToken: String) {
        viewModelScope.launch {
            val result = runCatching {
                authRepository.googleLogin(
                    username = name,
                    email = email,
                    token = accessToken,
                )
            }

            //if (result.isSuccess) {
            if (result!=null) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = name))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password_msg)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
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

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }
}
