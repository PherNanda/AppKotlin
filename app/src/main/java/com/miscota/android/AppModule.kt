package com.miscota.android

import com.miscota.android.address.PlacesRepository
import com.miscota.android.addressold.PlacesRepositoryOld
import com.miscota.android.api.ApiProvider
import com.miscota.android.api.ApiProviderDecode
import com.miscota.android.events.DefaultEventsManager
import com.miscota.android.events.EventsManager
import com.miscota.android.repository.*
import com.miscota.android.util.AuthStore
import com.miscota.android.util.DefaultAuthStore
import com.miscota.android.util.DefaultSharedPreferencesStore
import com.miscota.android.util.SharedPreferencesStore
import com.snapshop.consumer.utils.DefaultDispatcher
import com.snapshop.consumer.utils.Dispatcher
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single {
        ApiProvider(get())
    }

    single {
        ApiProviderDecode()
    }

    single<Dispatcher> {
        DefaultDispatcher()
    }

    single {
        Moshi.Builder().build()
    }

    single<AuthStore> {
        DefaultAuthStore(get())
    }

    single<SharedPreferencesStore> {
        DefaultSharedPreferencesStore(androidApplication())
    }

    single<EventsManager> {
        DefaultEventsManager(get())
    }

    factory {
        AuthRepository(
            authApi = get<ApiProvider>().authApi,
            authStore = get(),
        )
    }

    factory {
        PlacesRepository(
            context = androidApplication(),
        )
    }

    factory {
        CategoryRepository(
            categoryApi = get<ApiProvider>().categoryApi,
            authStore = get(),
        )
    }

    factory {
        ProductRepository(
            productApi = get<ApiProvider>().productApi,
            authStore = get(),
        )
    }

    factory {
        StoreLocationRepository(
            storeLocationApi = get<ApiProvider>().storeLocationApi,
            authStore = get(),
        )
    }

    factory {
        AutoShipRepository(
            get<ApiProvider>().autoShipApi,
            authStore = get(),
        )
    }

    factory {
        CheckoutRepository(
            checkoutApi = get<ApiProvider>().checkoutApi,
            authStore = get(),
            checkoutApiPayment = get<ApiProviderDecode>().checkoutApiPayment,
        )
    }


    factory {
        PlacesRepositoryOld(
            context = androidApplication(),
        )
    }
}