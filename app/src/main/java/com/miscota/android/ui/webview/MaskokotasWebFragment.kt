package com.miscota.android.ui.webview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.miscota.android.MainActivity
import com.miscota.android.R
import com.miscota.android.auth.AuthActivity
import com.miscota.android.databinding.FragmentWebViewBinding
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class WebType {
    PROFILE, ORDERS
}

class MaskokotasWebFragment : Fragment() {

    private var binding by autoClean<FragmentWebViewBinding>()

    private val viewModel by viewModel<MaskokotasWebViewModel>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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

        //firebase
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        val mksAppHeaders: MutableMap<String, String> = HashMap()
        mksAppHeaders["x-buddy-client"] = "android-app"

        var destinationUrl = "https://www.miscota.es"

        val navController = findNavController()

        if (!viewModel.isLoggedIn()) {
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            //startActivity(Intent(requireContext(), MainActivity::class.java))
            //navController.navigate(R.id.navigation_home)
        }

        if (navController.currentDestination?.id == R.id.navigation_orders) {

            if (!viewModel.isLoggedIn()) {
                println("no logueado webview orders")
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            }else {
                println("si logueado webview orders")
                destinationUrl = viewModel.getOrdersURL()
                viewModel.setShowAuth("1")
            }

        } else if (navController.currentDestination?.id == R.id.navigation_profile) {

            if (!viewModel.isLoggedIn()) {
                println("no logueado webview profile")
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            }else {
                println("si logueado webview profile")
                destinationUrl = viewModel.getProfileURL()
                viewModel.setShowAuth("1")
            }
            
        }

        binding.webView.addJavascriptInterface(AjaxHandler(requireContext()), "ajaxHandler")

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            loadUrl(destinationUrl,mksAppHeaders)
        }





    }


    override fun onStart() {
        super.onStart()

        if(viewModel.isLoggedIn()) {
            println("si logueado on start webview")
            (requireActivity() as MainActivity).binding.samedayInfoMain.visibility = View.GONE
            (requireActivity() as MainActivity).binding.locationLinearLayoutmain.visibility =
                View.GONE
            (requireActivity() as MainActivity).binding.imageBack.visibility = View.GONE

            val params: ViewGroup.LayoutParams =
                (requireActivity() as MainActivity).binding.headerMain.layoutParams!!
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            (requireActivity() as MainActivity).binding.headerMain.layoutParams = params
        }
        if(!viewModel.isLoggedIn()) {
            println("no logueado on start webview")
            startActivity(Intent(requireContext(), MainActivity::class.java))
            //viewModel.setShowAuth("1")
        }

    }

}