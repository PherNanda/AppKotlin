package com.miscota.android.api

import com.ihsanbal.logging.Level
import com.ihsanbal.logging.Logger
import com.ihsanbal.logging.LoggingInterceptor
import com.miscota.android.BuildConfig
import com.miscota.android.api.auth.AuthApi
import com.miscota.android.api.autoship.AutoShipApi
import com.miscota.android.api.category.CategoryApi
import com.miscota.android.api.checkout.CheckoutApi
import com.miscota.android.api.product.ProductApi
import com.miscota.android.api.store.StoreLocationApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ApiProvider {

    private val httpClient: OkHttpClient
    private val retrofit: Retrofit

    val authApi: AuthApi
    val categoryApi: CategoryApi
    val productApi: ProductApi
    val storeLocationApi: StoreLocationApi
    val autoShipApi: AutoShipApi
    val checkoutApi: CheckoutApi

    init {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .header("User-Agent", "Android:${BuildConfig.MISCOTA_USER_AGENT} Version:${BuildConfig.VERSION_NAME} Package:${BuildConfig.PACKAGE_NAME} Framework:okhttp/4.4.0")
                        .build()
                )
            }

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = LoggingInterceptor.Builder()
                .log(Platform.INFO)
                .setLevel(Level.BASIC)
                .logger(object : Logger {
                    override fun log(level: Int, tag: String?, msg: String?) {
                        Timber.tag(TAG).d(msg)
                    }
                })
                .build()

            builder.addInterceptor(loggingInterceptor)
            builder.addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .header("User-Agent", "Android:${BuildConfig.MISCOTA_USER_AGENT} Version:${BuildConfig.VERSION_NAME} Package:${BuildConfig.PACKAGE_NAME} Framework:okhttp/4.4.0")
                        .build()
                )
            }
        }

        httpClient = builder.build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.MERCHANT_SERVER_URL_MIS)
            //.baseUrl(BuildConfig.API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        categoryApi = retrofit.create(CategoryApi::class.java)
        productApi = retrofit.create(ProductApi::class.java)
        storeLocationApi = retrofit.create(StoreLocationApi::class.java)
        autoShipApi = retrofit.create(AutoShipApi::class.java)
        checkoutApi = retrofit.create(CheckoutApi::class.java)
    }

    companion object {
        const val CONNECTION_TIMEOUT_SECONDS = 30L
        const val READ_TIMEOUT_SECONDS = 120L
        const val WRITE_TIMEOUT_SECONDS = 120L

        private const val TAG = "OkHttp"
    }
}
