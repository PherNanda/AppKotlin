package com.miscota.android.util

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes

interface SharedPreferencesStore {

    var sharedPreferences: SharedPreferences

    fun putBoolean(key: String, value: Boolean)

    fun putInt(key: String, value: Int)

    fun putFloat(key: String, value: Float)

    fun putString(key: String, value: String)

    fun putLong(key: String, value: Long)

    fun getInt(key: String, default: Int): Int

    fun getFloat(key: String, default: Float): Float

    fun getBoolean(key: String, default: Boolean): Boolean

    fun getLong(key: String, default: Long): Long

    fun getString(key: String, default: String?): String?

    fun getAll(): Map<String, *>

    fun clear()

    companion object {
        const val NAME_PREFERENCES = "maskokotas"
    }
}

class DefaultSharedPreferencesStore(context: Context) : SharedPreferencesStore {

    override var sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SharedPreferencesStore.NAME_PREFERENCES, Context.MODE_PRIVATE
    )

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    override fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    override fun getInt(key: String, default: Int): Int {
        return sharedPreferences.getInt(key, default)
    }

    override fun getFloat(key: String, default: Float): Float {
        return sharedPreferences.getFloat(key, default)
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    override fun getLong(key: String, default: Long): Long {
        return sharedPreferences.getLong(key, default)
    }

    override fun getString(key: String, default: String?): String? {
        return sharedPreferences.getString(key, default) ?: default
    }

    override fun getAll(): Map<String, *> {
        return sharedPreferences.all
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}
