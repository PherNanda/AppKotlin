package com.miscota.android.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Parcelable
import android.util.Log

object ConnectionManager {

    fun create(onNetworkUp: (()->Unit)?, onNetworkDown: (()->Unit)?): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                intent.extras?.getParcelable<Parcelable>("networkInfo")?.let {
                    val info = it as NetworkInfo
                    val state: NetworkInfo.State = info.state

                    Log.d("BroadcastReceiver", "$info $state")
                    if (state === NetworkInfo.State.CONNECTED) {
                        onNetworkUp?.invoke()
                    } else {
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