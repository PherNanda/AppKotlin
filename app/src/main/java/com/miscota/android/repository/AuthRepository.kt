package com.miscota.android.repository

import com.miscota.android.api.auth.AuthApi
import com.miscota.android.api.auth.response.ForgotPasswordResponse
import com.miscota.android.api.auth.response.LoginResponse
import com.miscota.android.util.AuthStore
import okhttp3.MultipartBody

class AuthRepository(private val authApi: AuthApi, private val authStore: AuthStore) {

    suspend fun login(userName: String, password: String): LoginResponse {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("user", userName)
            .addFormDataPart("password", password)
            .build()

        return authApi.login(
            authHeader = "Bearer " + authStore.getBearerToken(),
            request = requestBody
        )
    }

    suspend fun recover(email: String): ForgotPasswordResponse {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", email)
            .build()

        return authApi.recoverPassword(
            authHeader = "Bearer " + authStore.getBearerToken(),
            request = requestBody
        )
    }

    suspend fun signup(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        terms: Boolean,
        newsLetter: Boolean,
    ): LoginResponse {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", username)
            .addFormDataPart("email", email)
            .addFormDataPart("passwd", password)
            .addFormDataPart("passwd2", confirmPassword)
            .addFormDataPart("conditions", terms.toString())
            .addFormDataPart("newsletter", newsLetter.toString())
            .addFormDataPart("source", "44")
            .build()
        val userId = authApi.signup(
            authHeader = "Bearer " + authStore.getBearerToken(),
            request = requestBody
        ).userId

        return authApi.fetchUserInfo(
            authHeader = "Bearer " + authStore.getBearerToken(),
            userId = userId
        )
    }

    suspend fun facebookLogin(
        username: String,
        email: String,
        token: String,
    ): LoginResponse {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", username)
            .addFormDataPart("email", email)
            .addFormDataPart("token", token)
            .addFormDataPart("type", "5")
            .addFormDataPart("newsletter", "true")
            .build()
        return authApi.socialLogin(
            authHeader = "Bearer " + authStore.getBearerToken(),
            request = requestBody
        )
    }

    suspend fun googleLogin(
        username: String,
        email: String,
        token: String,
    ): LoginResponse {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", username)
            .addFormDataPart("email", email)
            .addFormDataPart("token", token)
            .addFormDataPart("type", "32")
            .addFormDataPart("newsletter", "true")
            .build()
        return authApi.socialLogin(
            authHeader = "Bearer " + authStore.getBearerToken(),
            request = requestBody
        )
    }


}