package com.miscota.android.ui.termsprivacity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.miscota.android.databinding.FragmentTermsPrivacityBinding
import com.miscota.android.util.autoClean


class TermsPrivacityFragment : Fragment() {

    private var binding by autoClean<FragmentTermsPrivacityBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTermsPrivacityBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mksAppHeaders: MutableMap<String, String> = HashMap()
        mksAppHeaders["x-buddy-client"] = "android-app"
        val destinationUrl = "https://www.miscota.es/corp/politica-de-privacidad"


        binding.webViewTerms.isVerticalScrollBarEnabled = true

        binding.webViewTerms.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            isVerticalScrollBarEnabled = true
            webViewClient = WebViewClient()

            loadUrl(destinationUrl, mksAppHeaders)

        }

    }
}