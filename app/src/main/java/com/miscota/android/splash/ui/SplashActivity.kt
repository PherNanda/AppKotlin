package com.miscota.android.splash.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.databinding.ActivityAddressBinding
import com.miscota.android.databinding.FragmentSubCategoriesBinding
import com.miscota.android.util.Item
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by adrian on 3/6/21.
 * Copyright (c) 2021 EMMA Solutions SL. All rights reserved
 */

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewModel.openMainActivityLiveData.observe(this, {

            println("splash chibato 0")
            println("it ${it.data}") //api error true
            println("it $it")

            it.consume()?.let {
                println("splash chibato 1")

                goToMain()
            }
            println("splash chibato 3")
        })
        //goToMain()
        println("splash chibato oncreate")
    }

    private fun goToMain() {
        println("splash chibato 2")
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
