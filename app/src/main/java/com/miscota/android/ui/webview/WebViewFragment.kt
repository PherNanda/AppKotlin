package com.miscota.android.ui.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.miscota.android.MainActivity
import com.miscota.android.databinding.FragmentWebViewBinding
import com.miscota.android.util.autoClean

class WebViewFragment : Fragment() {

    private var binding by autoClean<FragmentWebViewBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).binding.imageBack.isVisible = false
        //(requireActivity() as MainActivity).binding.imageBack.visibility = View.GONE

        val url = requireNotNull(requireArguments().getString(KEY_STRING_WEB_VIEW_URL))

        binding.webView.apply {
            settings.javaScriptEnabled = true

            loadUrl(url)
        }
    }



    companion object {
        const val KEY_STRING_WEB_VIEW_URL = "webViewUrl"
    }

}