package com.miscota.android.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.miscota.android.R
import com.miscota.android.auth.intro.IntroFragment
import com.miscota.android.auth.login.ui.LoginFragment
import com.miscota.android.auth.signup.SignupFragment
import com.miscota.android.connection.ConnectionManager
import com.miscota.android.databinding.ActivityAuthBinding
import com.miscota.android.ui.connection.ConnectionStateFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : FragmentActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val viewModel by viewModel<AuthViewModel>()

    private val broadcastReceiver by lazy {
        ConnectionManager.create({
            binding.connectionOff.visibility = View.GONE
        },  {
            viewDisconnected()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewpager.apply {
            adapter = AuthFragmentAdapter(this@AuthActivity)
        }

        binding.imageClose.setOnClickListener {
            finish()
        }

        binding.imageBackButton.setOnClickListener {
            //finish()
            onBackPressed()
        }

        viewModel.screenState.observe(this) {
            when (requireNotNull(it)) {
                AuthViewModel.Screen.IntroScreen -> {
                    binding.viewpager.currentItem = 0
                }
                AuthViewModel.Screen.LoginScreen -> {
                    binding.viewpager.currentItem = 1
                }
                AuthViewModel.Screen.SignUpScreen -> {
                    binding.viewpager.currentItem = 2
                }
            }
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    viewModel.setScreen(AuthViewModel.Screen.IntroScreen)
                }
                if (position == 1) {
                    viewModel.setScreen(AuthViewModel.Screen.LoginScreen)
                }
                if (position == 2) {
                    viewModel.setScreen(AuthViewModel.Screen.SignUpScreen)
                }
            }
        })

        TabLayoutMediator(binding.tabLayout, binding.viewpager) { _, _ -> }.attach()

        viewModel.statusConnect.observe(this){
            println("viewModel.statusConnect observe auth ${viewModel.statusConnect.value}")
            if (viewModel.statusConnect.value!!){
                viewErrorApi()
            }
            return@observe
        }
    }

    class AuthFragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                IntroFragment()
            } else if (position == 1){
                LoginFragment()
            }else{
                SignupFragment()
            }
        }

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

        binding.connectionOff.visibility = View.VISIBLE
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.add(R.id.connectionOff, ConnectionStateFragment())
        ft.commit()
    }

    private fun viewErrorApi(){
        viewDisconnected()
    }

}