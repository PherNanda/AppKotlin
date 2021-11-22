package com.miscota.android.ui.pedido


import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.miscota.android.MainActivity
import com.miscota.android.databinding.PedidoNoProcesadoFragmentBinding
import com.miscota.android.util.autoClean

class PedidoNoProcesado : Fragment() {

    companion object {
        fun newInstance() = PedidoNoProcesado()
    }

    private lateinit var viewModel: PedidoNoProcesadoViewModel

    private var binding by autoClean<PedidoNoProcesadoFragmentBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //return inflater.inflate(R.layout.pedido_no_procesado_fragment, container, false)
        binding = PedidoNoProcesadoFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.pedidoNoRealizado.visibility = View.VISIBLE

        binding.volverAmiscota.setOnClickListener {
            startActivity(Intent(requireContext(),MainActivity::class.java))
        }

        viewModel = ViewModelProvider(this).get(PedidoNoProcesadoViewModel::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pedidoNoRealizado.visibility = View.VISIBLE

        binding.volverAmiscota.setOnClickListener {
            startActivity(Intent(requireContext(),MainActivity::class.java))
        }


    }

}