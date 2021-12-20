package com.miscota.android.splash.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.miscota.android.MainActivity
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

            it.consume()?.let {

                goToMain()
            }
        })
        //goToMain()
    }

    private fun goToMain() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
