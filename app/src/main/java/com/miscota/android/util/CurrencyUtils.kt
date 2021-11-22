package com.miscota.android.util

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.NumberFormat
import java.util.*

class CurrencyUtils {

    companion object {
        fun formatPrice(price: Double?): String {
            price?.let{
                val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
                format.currency = Currency.getInstance("EUR")
                return format.format(price)
            }
            return ""
        }


        fun fixEncoding(response: String): String {
            var response = response
            response = try {
                val u = response.toByteArray(charset("ISO-8859-1"))
                String(u, Charset.forName("UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return e.printStackTrace().toString()
            }
            return response
        }



    }
}