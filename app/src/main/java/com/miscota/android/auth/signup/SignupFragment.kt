package com.miscota.android.auth.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.miscota.android.R
import com.miscota.android.address.AddressActivity
import com.miscota.android.afterTextChanged
import com.miscota.android.auth.AuthViewModel
import com.miscota.android.auth.login.ui.LoggedInUserView
import com.miscota.android.databinding.FragmentSignupBinding
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class SignupFragment : Fragment() {

    private var binding by autoClean<FragmentSignupBinding>()

    private val signupViewModel by viewModel<SignupViewModel>()

    private val authViewModel by sharedViewModel<AuthViewModel>()

    private lateinit var geoCoder: Geocoder

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    /**private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (!permissions.any { !it.value }) {
                setCurrentLocation()
            }
        }**/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        geoCoder = Geocoder(requireContext(), Locale.getDefault())**/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        with(binding) {
            signupViewModel.signupFormState.observe(viewLifecycleOwner) {
                if (!it.isDataValid) {
                    defaultState()
                }
                val loginState = it ?: return@observe

                // disable login button unless both username / password is valid
                loginButton.isEnabled = loginState.isDataValid

                if (loginState.usernameError != null) {
                    usernameLayout.error = getString(loginState.usernameError)
                } else {
                    usernameLayout.error = null
                }
                if (loginState.emailError != null) {
                    emailLayout.error = getString(loginState.emailError)
                } else {
                    emailLayout.error = null
                }
                if (loginState.passwordError != null) {
                    passwordLayout.error = getString(loginState.passwordError)
                } else {
                    passwordLayout.error = null
                }
                if (loginState.confirmPasswordError != null) {
                    confirmPasswordLayout.error = getString(loginState.confirmPasswordError)
                } else {
                    confirmPasswordLayout.error = null
                }
                if (loginState.termsAcceptedError != null) {
                    Toast.makeText(
                        requireContext(),
                        getString(loginState.termsAcceptedError),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            signupViewModel.signUpResult.observe(viewLifecycleOwner) {
                val signupResult = it ?: return@observe

                defaultState()
                if (signupResult.error != null) {
                    showSignUpFailed(signupResult.error)
                }
                if (signupResult.success != null) {
                    updateUiWithUser(signupResult.success)
                }
            }

            username.afterTextChanged {
                signupViewModel.loginDataChanged(
                    username = username.text.toString(),
                    password = password.text.toString(),
                    confirmPassword = confirmPassword.text.toString(),
                    email = email.text.toString(),
                    address = address.text.toString(),
                    terms = termsSwitch.isChecked,
                )
            }
            email.afterTextChanged {
                signupViewModel.loginDataChanged(
                    username = username.text.toString(),
                    password = password.text.toString(),
                    confirmPassword = confirmPassword.text.toString(),
                    email = email.text.toString(),
                    address = address.text.toString(),
                    terms = termsSwitch.isChecked,
                )
            }
            password.afterTextChanged {
                signupViewModel.loginDataChanged(
                    username = username.text.toString(),
                    password = password.text.toString(),
                    confirmPassword = confirmPassword.text.toString(),
                    email = email.text.toString(),
                    address = address.text.toString(),
                    terms = termsSwitch.isChecked,
                )
            }
            confirmPassword.afterTextChanged {
                signupViewModel.loginDataChanged(
                    username = username.text.toString(),
                    password = password.text.toString(),
                    confirmPassword = confirmPassword.text.toString(),
                    email = email.text.toString(),
                    address = address.text.toString(),
                    terms = termsSwitch.isChecked,
                )
            }

            signUpButton.setOnClickListener {
                loadingState()
                signupViewModel.signUp(
                    username = username.text.toString(),
                    password = password.text.toString(),
                    confirmPassword = confirmPassword.text.toString(),
                    email = email.text.toString(),
                    terms = termsSwitch.isChecked,
                    newsLetter = newsLetterSwitch.isChecked,
                )
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "signUpButton")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
            }

            signupViewModel.selectedAddress.observe(viewLifecycleOwner) {
                binding.address.text = it ?: getString(R.string.address)
            }
        }

        binding.address.setOnClickListener {
            startActivity(Intent(requireContext(), AddressActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            authViewModel.setScreen(AuthViewModel.Screen.LoginScreen)
        }
    }

    override fun onStart() {
        super.onStart()

        signupViewModel.loadAddress()
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
        requireActivity().setResult(Activity.RESULT_OK)

        requireActivity().finish()
    }

    private fun showSignUpFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
    }

    private fun loadingState() {
        binding.usernameLayout.error = null
        binding.username.isEnabled = false
        binding.passwordLayout.error = null
        binding.password.isEnabled = false
        binding.signUpButton.text = getString(R.string.loading)
        binding.loginButton.isEnabled = false
        binding.signUpButton.isEnabled = false
    }

    private fun defaultState() {
        binding.usernameLayout.error = null
        binding.username.isEnabled = true
        binding.passwordLayout.error = null
        binding.password.isEnabled = true
        binding.signUpButton.text = getString(R.string.sign_up)
        binding.loginButton.isEnabled = true
        binding.signUpButton.isEnabled = true
    }

    private fun setCurrentLocation() {
        lifecycleScope.launchWhenResumed {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(location.latitude, location.longitude)

                        val listOfAddress =
                            geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                        listOfAddress.forEach { address ->
                            signupViewModel.setCurrentLocationFromGeocode(address)
                        }
                    }
                }
            } catch (e: SecurityException) {
                Timber.e("Location permission not granted")
            }
        }
    }

}
