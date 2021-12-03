package com.miscota.android.ui.connection

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.miscota.android.databinding.ConnectionStateFragmentBinding
import com.miscota.android.util.autoClean

class ConnectionStateFragment : Fragment() {

    companion object {
        fun newInstance() = ConnectionStateFragment()
    }

    private lateinit var viewModel: ConnectionStateViewModel

    private var binding by autoClean<ConnectionStateFragmentBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConnectionStateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[ConnectionStateViewModel::class.java]
    }

}