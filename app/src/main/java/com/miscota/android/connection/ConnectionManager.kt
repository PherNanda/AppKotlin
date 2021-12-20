package com.miscota.android.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Parcelable
import com.miscota.android.util.DefaultAuthStore
import timber.log.Timber

object ConnectionManager {

    fun create(onNetworkUp: (()->Unit)?, onNetworkDown: (()->Unit)?): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent.extras?.getParcelable<Parcelable>("networkInfo")?.let {
                    val info = it as NetworkInfo
                    val state: NetworkInfo.State = info.state
                    val authPreference: DefaultAuthStore = DefaultAuthStore(context)

                    Timber.tag("BroadcastReceiver").e("$info $state")
                    if ( state === NetworkInfo.State.CONNECTED && !authPreference.getStatus()) {
                        authPreference.setInternetOn(true)
                            onNetworkUp?.invoke()
                    }
                    if ( state === NetworkInfo.State.CONNECTED && authPreference.getStatus()) {
                        authPreference.setInternetOn(true)
                        onNetworkDown?.invoke()
                    }
                    if (state === NetworkInfo.State.DISCONNECTED && authPreference.getStatus()) {
                        authPreference.setInternetOn(false)
                        onNetworkDown?.invoke()
                    }
                    if (state === NetworkInfo.State.DISCONNECTED && !authPreference.getStatus()) {
                        authPreference.setInternetOn(false)
                        onNetworkDown?.invoke()
                    }
                }

            }
        }
    }

    fun register(context: Context, broadcastReceiver: BroadcastReceiver) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun unregister(context: Context, broadcastReceiver: BroadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver)
    }
}