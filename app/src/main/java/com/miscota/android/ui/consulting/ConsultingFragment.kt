package com.miscota.android.ui.consulting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.miscota.android.MainActivity
import com.miscota.android.databinding.FragmentConsultingBinding
import com.miscota.android.util.autoClean
import zendesk.chat.Chat
import zendesk.chat.ChatConfiguration


class ConsultingFragment : Fragment() {
    private var binding by autoClean<FragmentConsultingBinding>()
    private lateinit var chatConfiguration: ChatConfiguration

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    //private var supportEngine: Engine = SupportEngine.engine()
    //private var answerBotEngine: Engine = AnswerBotEngine.engine()
    //private var chatEngine: Engine? = ChatEngine.engine()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConsultingBinding.inflate(inflater, container, false)

        Chat.INSTANCE.init(
            requireContext(),
            "0c1ce8097a5c49ffcf2a3a08e2eabce49a1f534a044f695f",
            "mobile_sdk_client_34e9f4adfefbae2d9072"
        )

        /****
         * Android:
        Zendesk.INSTANCE.init(context, "https://pcrg.zendesk.com",
        "0c1ce8097a5c49ffcf2a3a08e2eabce49a1f534a044f695f",
        "mobile_sdk_client_34e9f4adfefbae2d9072");
        Support.INSTANCE.init(Zendesk.INSTANCE);
         ***/

        //"4fbaa9aa29fb764f77face3954c078f4d4e134f6ffd24eb5",
        //            "mobile_sdk_client_4ea97664ea7a627772c3"
        chatConfiguration = ChatConfiguration.builder()
            .withAgentAvailabilityEnabled(false)
            .build()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebase
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        recordScreenSupport()

       /**binding.ordersCard.setOnClickListener {
            MessagingActivity.builder()
                .withEngines(ChatEngine.engine())
                .show(requireContext(), chatConfiguration)
        }**/

       val mksAppHeaders: MutableMap<String, String> = HashMap()
        mksAppHeaders["x-buddy-client"] = "android-app"
        var destinationUrl = "https://www.miscota.es/contact-form"


        binding.webViewContact.isVerticalScrollBarEnabled = true

        binding.webViewContact.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            isVerticalScrollBarEnabled = true
            webViewClient = WebViewClient()
            /**loadUrl("javascript:$(document).ajaxStart(function (event, request, settings) { " +
            "ajaxHandler.ajaxBegin(); " + // Event called when an AJAX call begins
            "});")
            loadUrl("javascript:$(document).ajaxComplete(function (event, request, settings) { " +
            "ajaxHandler.ajaxDone(); " + // Event called when an AJAX call ends
            "});")**/


                loadUrl(destinationUrl, mksAppHeaders)



        }


    }


    private fun recordScreenSupport() {
        val screenName = "screen_support"

        firebaseAnalytics.logEvent(screenName) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "ConsultingFragment")
            param(FirebaseAnalytics.Param.METHOD, "recordScreenSupport")
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