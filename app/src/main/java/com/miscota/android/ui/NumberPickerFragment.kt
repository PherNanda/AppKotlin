package com.miscota.android.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.miscota.android.R
import timber.log.Timber


class NumberPickerFragment(private val maxValue: Int,
                           private val currentValue: Int,
                           private val cancelText: String,
                           private val acceptText: String,
                           private val valueChangeListener: (value: Int) -> Unit): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity?.layoutInflater?.inflate(R.layout.dialog_number_picker, null)
        val numberPicker = view?.findViewById<NumberPicker>(R.id.numberPicker)
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        numberPicker?.let {
            numberPicker.minValue = 1
            numberPicker.maxValue = maxValue
            numberPicker.value = currentValue

            val dividerHeight = 5
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                numberPicker.selectionDividerHeight = dividerHeight
            }else{
                numberPicker.setDividerHeight(dividerHeight)
            }

            numberPicker.setDividerColor(ContextCompat.getColor(requireContext(), R.color.app_blue))

            builder.setPositiveButton(acceptText) { _, _ ->
                valueChangeListener(numberPicker.value)
            }

            builder.setNegativeButton(cancelText) { _, _ ->
            }

            builder.setTitle(R.string.new_frequency)
        }

        builder.setView(view)
        return builder.create()
    }
}

fun NumberPicker.setDividerHeight(height: Int) {
    val pickerFields = NumberPicker::class.java.declaredFields
    for (pf in pickerFields) {
        if (pf.name == "mSelectionDividerHeight") {
            pf.isAccessible = true
            try {
                // set divider height in pixels
                pf.set(this, height)
            } catch (e: java.lang.IllegalArgumentException) {
                Timber.e(e)
            } catch (e: Resources.NotFoundException) {
                Timber.e(e)
            } catch (e: IllegalAccessException) {
                Timber.e(e)
            }
            break
        }
    }
}

fun NumberPicker.setDividerColor(color: Int) {
    val pickerFields = NumberPicker::class.java.declaredFields
    for (pf in pickerFields) {
        if (pf.name == "mSelectionDivider") {
            pf.isAccessible = true
            try {
                val colorDrawable = ColorDrawable(color)
                pf[this] = colorDrawable
            } catch (e: IllegalArgumentException) {
                Timber.e(e)
            } catch (e: Resources.NotFoundException) {
                Timber.e(e)
            } catch (e: IllegalAccessException) {
                Timber.e(e)
            }
            break
        }
    }
}
