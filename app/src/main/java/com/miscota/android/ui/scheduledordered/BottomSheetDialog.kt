package com.miscota.android.ui.scheduledordered

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miscota.android.databinding.PauseBottomSheetBinding


class BottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: PauseBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PauseBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }
}