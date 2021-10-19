package com.miscota.android.api

import android.util.Log
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.Logger
import com.ihsanbal.logging.LoggingInterceptor
import com.miscota.android.BuildConfig
import com.miscota.android.api.checkout.CheckoutApi
import com.miscota.android.ui.checkoutpayment.PaymentRequestAdyen
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ApiProviderDecode {

    private val httpClient: OkHttpClient
    private val retrofit: Retrofit

    val checkoutApiPayment: CheckoutApi

    init {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            var msj =""
            val loggingInterceptor = LoggingInterceptor.Builder()
                .log(Platform.INFO)
                .setLevel(Level.BASIC)
                .logger(object : Logger {

                    override fun log(level: Int, tag: String?, msg: String?) {
                        Timber.tag(TAG).d(msg)
                       msj += msg
                    }

                })
                .build()
            println("msj $msj")

            builder.addInterceptor(loggingInterceptor)
        }

        httpClient = builder.build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.MERCHANT_SERVER_URL_MIS)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        checkoutApiPayment = retrofit.create(CheckoutApi::class.java)
    }

    companion object {
        const val CONNECTION_TIMEOUT_SECONDS = 30L
        const val READ_TIMEOUT_SECONDS = 120L
        const val WRITE_TIMEOUT_SECONDS = 120L

        private const val TAG = "Okttp "
    }
}