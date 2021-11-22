package com.miscota.android.ui.discount

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.miscota.android.R
import com.miscota.android.databinding.DiscountFragmentBinding
import com.miscota.android.databinding.TipoEnvioFragmentBinding
import com.miscota.android.util.autoClean

class DiscountFragment : Fragment() {

    companion object {
        fun newInstance() = DiscountFragment()
    }

    private lateinit var viewModel: DiscountViewModel
    private var binding by autoClean<DiscountFragmentBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DiscountFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.cartHeader.text = getString(R.string.discount_title)

        binding.toolbar.imageBack.setOnClickListener {
            //goToCartScreen()
            getFragmentManager()?.popBackStackImmediate()
            //requireActivity().finish()

        }

        viewModel = ViewModelProvider(this).get(DiscountViewModel::class.java)
        // TODO: Use the ViewModel
    }

}