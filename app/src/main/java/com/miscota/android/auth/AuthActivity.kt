package com.miscota.android.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.miscota.android.auth.intro.IntroFragment
import com.miscota.android.auth.login.ui.LoginFragment
import com.miscota.android.auth.signup.SignupFragment
import com.miscota.android.databinding.ActivityAuthBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : FragmentActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val viewModel by viewModel<AuthViewModel>()

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


}