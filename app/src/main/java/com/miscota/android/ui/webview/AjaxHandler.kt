package com.miscota.android.ui.webview

import android.content.Context
import android.util.Log
import android.widget.Toast


class AjaxHandler(requireContext: Context) {

    private val TAG = "AjaxHandler"
    private var context: Context? = null

    fun AjaxHandler(context: Context?) {
        this.context = context
    }

    fun ajaxBegin() {
        Log.w(TAG, "AJAX Begin")
        Toast.makeText(context, "AJAX Begin", Toast.LENGTH_SHORT).show()
    }

    fun ajaxDone() {
        Log.w(TAG, "AJAX Done")
        Toast.makeText(context, "AJAX Done", Toast.LENGTH_SHORT).show()
    }
}