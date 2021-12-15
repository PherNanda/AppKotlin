package com.miscota.android.auth.forgotpassword.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.miscota.android.R
import com.miscota.android.afterTextChanged
import com.miscota.android.connection.ConnectionManager
import com.miscota.android.databinding.ActivityForgotPasswordBinding
import com.miscota.android.ui.connection.ConnectionStateFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    private val viewModel by viewModel<ForgotPasswordViewModel>()

    private val broadcastReceiver by lazy {
        ConnectionManager.create({
            binding.connectionOff.visibility = View.GONE
            binding.doneButton.visibility = View.VISIBLE
            binding.emailEditText.visibility = View.VISIBLE
        }, {
            viewDisconnected()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.forgotPasswordForm.observe(this) {
            val state = it ?: return@observe

            binding.doneButton.isEnabled = state.isDataValid

            if (state.emailError != null) {
                binding.emailLayout.error = getString(state.emailError)
            } else {
                binding.emailLayout.error = null
            }
        }

        DrawableCompat.setTint(
            binding.loading.indeterminateDrawable,
            Color.parseColor("#4FC3F7")
        )

        viewModel.showLoading.observe(this) {
            if (!viewModel.showLoading.value!!) {
                binding.loadingText.visibility = View.GONE
                binding.loadingLayout.visibility = View.GONE
                binding.loading.visibility = View.GONE


            }
            if (viewModel.showLoading.value!!) {
                binding.loadingText.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.VISIBLE
                binding.loading.visibility = View.VISIBLE


            }
        }

        with(binding) {
            backImage.setOnClickListener {
                finish()
            }

            doneButton.setOnClickListener {
                if ( emailEditText.text?.isNotEmpty() == true ) {
                    loadingState()
                    viewModel.recoverPassword(emailEditText.text.toString())
                    viewModel.showLoading.value = true
                    //binding.emailLayout.visibility = View.INVISIBLE
                }
                else{
                    Toast.makeText(this@ForgotPasswordActivity,getString(R.string.recover_failed),Toast.LENGTH_LONG).show()
                }
            }

            emailEditText.afterTextChanged {
                viewModel.dataChanged(
                    emailEditText.text.toString(),
                )
            }
        }

        viewModel.forgotPasswordResult.observe(this) {
            val result = it ?: return@observe

            defaultState()
            if (result.error != null) {
                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                viewModel.showLoading.value = false
            }
            if (result.success != null) {
                    Toast.makeText(this, getString(R.string.restore_password_email_sent), Toast.LENGTH_SHORT).show()
                viewModel.showLoading.value = false
                //binding.emailLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun loadingState() {
        binding.emailLayout.error = null
        binding.emailEditText.isEnabled = false
        binding.doneButton.text = getString(R.string.loading)
        binding.doneButton.isEnabled = false
    }

    private fun defaultState() {
        binding.emailLayout.error = null
        binding.emailEditText.isEnabled = true
        binding.doneButton.text = getString(R.string.restore_password)
        binding.doneButton.isEnabled = true
    }
    override fun onResume() {
        super.onResume()
        ConnectionManager.register(this, broadcastReceiver)

    }

    override fun onPause() {
        super.onPause()
        ConnectionManager.unregister(this, broadcastReceiver)
    }


    private fun viewDisconnected(){
        binding.doneButton.visibility = View.GONE
        binding.connectionOff.visibility = View.VISIBLE
        binding.emailEditText.visibility = View.GONE
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.add(R.id.connectionOff, ConnectionStateFragment())
        ft.commit()
    }

    private fun viewErrorApi(){
        viewDisconnected()
    }
}