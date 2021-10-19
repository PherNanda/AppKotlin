package com.miscota.android.util

class Event<T>(var data: T?) {

    private var handled = false

    fun consume(): T? {
        if (handled) {
            return null
        }
        handled = true
        return data
    }
}
