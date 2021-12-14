package com.miscota.android.ui.connection

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.miscota.android.MainActivityViewModel
import com.miscota.android.databinding.ConnectionStateFragmentBinding
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConnectionStateFragment : Fragment() {

    companion object {
        fun newInstance() = ConnectionStateFragment()
    }

    private lateinit var viewModel: ConnectionStateViewModel

    private var binding by autoClean<ConnectionStateFragmentBinding>()
    private val viewModelMain by viewModel<MainActivityViewModel>()

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
            if (viewModelMain.statusConnect.value!!) {
                binding.titleConnectOff.text =
                    "Ups parece que tenemos un problema con nuestro servicio"
            }
        }

        viewModel = ViewModelProvider(this)[ConnectionStateViewModel::class.java]
    }

}