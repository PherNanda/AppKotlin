package com.miscota.android.api.auth

import com.miscota.android.api.auth.response.*
import okhttp3.RequestBody
import retrofit2.http.*

interface AuthApi {

    @POST("/token")
    suspend fun getToken(@Body request: RequestBody): TokenResponse

    @POST("/app/user/login")
    suspend fun login(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Header("x-request-retail-id") requestRetailId: String = "5",
        @Body request: RequestBody
    ): LoginResponse

    @POST("/app/user/recover")
    suspend fun recoverPassword(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Body request: RequestBody
    ): ForgotPasswordResponse

    @POST("/app/user/register")
    suspend fun signup(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Body request: RequestBody
    ): SignupResponse

    @POST("/app/user/login_social")
    suspend fun socialLogin(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Body request: RequestBody
    ): LoginResponse

    @GET("/app/user/{user_id}/info")
    suspend fun fetchUserInfo(
        @Header("Authorization") authHeader: String,
        @Header("x-request-domain") requestDomain: String = "www.miscota.es",
        @Path("user_id") userId: Int,
    ): LoginResponse


}