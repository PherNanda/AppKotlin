package com.miscota.android.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.miscota.android.MainActivity
import com.miscota.android.databinding.FragmentStoreWebViewBinding
import com.miscota.android.util.autoClean

class StoreWebViewFragment : Fragment() {

    private var binding by autoClean<FragmentStoreWebViewBinding>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreWebViewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mksAppHeaders: MutableMap<String, String> = HashMap()
        mksAppHeaders["x-buddy-client"] = "android-app"
        val destinationUrl = "https://www.miscota.es/nuestras-tiendas"

        binding.webViewStore.isVerticalScrollBarEnabled = true

        binding.webViewStore.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            isVerticalScrollBarEnabled = true
            webViewClient = WebViewClient()

           loadUrl(destinationUrl, mksAppHeaders)

        }

    }

    override fun onStart() {
        super.onStart()

        (requireActivity() as MainActivity).binding.samedayInfoMain.visibility = View.GONE
        (requireActivity() as MainActivity).binding.locationLinearLayoutmain.visibility = View.GONE

        val params: ViewGroup.LayoutParams = (requireActivity() as MainActivity).binding.headerMain.layoutParams!!
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        (requireActivity() as MainActivity).binding.headerMain.layoutParams = params

    }
}