package com.miscota.android.ui.scheduledordered

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val scheduledOrderModule = module {
    viewModel {
        ScheduleOrderViewModel(
            autoShipRepository = get(),
            authStore = get(),
        )
    }
}