package com.miscota.android.ui.connection

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.miscota.android.MainActivity
import com.miscota.android.MainActivityViewModel
import com.miscota.android.R
import com.miscota.android.databinding.ConnectionStateFragmentBinding
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel


class ConnectionStateFragment : Fragment() {

    companion object {
        fun newInstance() = ConnectionStateFragment()
    }

    private var binding by autoClean<ConnectionStateFragmentBinding>()
    private val viewModelMain by viewModel<MainActivityViewModel>()
    private val viewModel by viewModel<ConnectionStateViewModel>()
    private var tryApi = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConnectionStateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModelMain.statusConnect.value != null) {
            if (viewModelMain.statusConnect.value!! && viewModelMain.authStore.getInternetOn()) {
                binding.titleConnectOff.text = getString(R.string.api_error_message)
                binding.tryConnectApi.visibility = View.VISIBLE
            }
        }

        binding.tryConnectApi.setOnClickListener {
            viewModel.showLoading.value = true
            viewModel.tryConnectApiAgain.value = true
            tryApi = true
            viewModel.checkAndChangeStatus()
        }

        viewModel.tryConnectApifailed.observe(this) {
            if (!it && tryApi){
                startActivity(Intent(requireActivity(), MainActivity::class.java))
            }
        }

        viewModel.showLoading.observe(this) {
            viewModel.tryConnectApiAgain.observe(this) {
                if (viewModel.tryConnectApiAgain.value == true) {
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
                }else{
                    binding.loadingText.visibility = View.GONE
                    binding.loadingLayout.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                }
            }
            binding.imageClose.setOnClickListener {
                //activity?.finish() //cierra la app entera
                activity?.finishAffinity() //cierra la app entera
                //requireActivity().finish()
               /**val thisActivity: Activity? = activity
                if (thisActivity != null) {
                    startActivity(Intent(thisActivity, MainActivity::class.java)) // if needed
                    thisActivity.finish()
                }**/
            }

            viewModel.totalRetry.observe(this) {
                if ( it == 3 ) {

                    viewModel.tryConnectApiAgain.value = false

                    if (viewModel.tryConnectApifailed.value == true) {
                        Toast.makeText(
                            requireContext(),
                            "No ha sido posible conectar a nuestro servicio",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }


        }
    }

}