package com.miscota.android.ui.paymentmethod

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.miscota.android.R
import com.miscota.android.databinding.PaymentMethodFragmentBinding
import com.miscota.android.ui.addcard.AddCardFragment
import com.miscota.android.ui.cart.CartViewModel
import com.miscota.android.ui.tramitarpedido.TramitarPedidoFragment
import com.miscota.android.util.autoClean
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentMethodFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentMethodFragment()

        const val maxLengthCard = 16
    }

    private lateinit var viewModel: PaymentMethodViewModel

    private val viewModelCart by viewModel<CartViewModel>()

    private var binding by autoClean<PaymentMethodFragmentBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PaymentMethodFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.cartHeader.text = getString(R.string.payment_method_header)


        val bundlePayment: Bundle? = arguments
        val cardOwner: String? = bundlePayment?.getString(getString(R.string.card_user_encrypt))
        val cardSecurity: String? = bundlePayment?.getString(getString(R.string.card_security_encrypt))
        val cardYear: String? = bundlePayment?.getString(getString(R.string.card_expire_year_encrypt))
        val cardNumber: String? = bundlePayment?.getString(getString(R.string.card_number_encrypt))
        val cardMonth: String? = bundlePayment?.getString(getString(R.string.card_expire_encrypt))

        if (cardOwner != null && cardSecurity != null &&
            cardYear != null && cardNumber != null && cardMonth != null){
            binding.cardOption.isChecked = true
        }
        if (viewModelCart.authStore.getCard() != null){

                binding.cardOption.isChecked = true

                binding.cardNumber.text = String.format(resources.getString(R.string.card_mask), viewModelCart.authStore.getCard()!!.card.substring(12,maxLengthCard))
                binding.cardExpirationDate.text = "${viewModelCart.authStore.getCard()!!.expireMonth} / ${viewModelCart.authStore.getCard()!!.expireYear}"
                binding.cardUserName.text = viewModelCart.authStore.getCard()!!.owner.toString()
        }
        if(!binding.cardOption.isChecked){
            binding.cardUser.visibility = View.GONE
            binding.myCardsTitle.visibility = View.GONE
        }

        binding.toolbar.imageBack.setOnClickListener {
            getFragmentManager()?.popBackStackImmediate()

        }

        binding.acceptButton.setOnClickListener {

            if (cardOwner != null && cardSecurity != null &&
                cardYear != null && cardNumber != null && cardMonth != null) {

                val bundlePaymentSave = Bundle()
                bundlePaymentSave.putString(
                    getString(R.string.card_security_encrypt),
                    cardSecurity
                )
                bundlePaymentSave.putString(
                    getString(R.string.card_expire_encrypt),
                    cardMonth
                )
                bundlePaymentSave.putString(
                    getString(R.string.card_number_encrypt),
                    cardNumber
                )
                bundlePaymentSave.putString(
                    getString(R.string.card_expire_year_encrypt),
                    cardYear
                )
                bundlePaymentSave.putString(
                    getString(R.string.card_user_encrypt),
                    cardOwner
                )


                val fragment = TramitarPedidoFragment()
                fragment.arguments = bundlePayment
                replaceFragment(fragment)
            }
            if(viewModelCart.authStore.getCard() != null){

                val fragment = TramitarPedidoFragment()
                fragment.arguments = bundlePayment
                replaceFragment(fragment)

            }
            else if(viewModelCart.authStore.getCard() == null  && cardNumber == null){
                Toast.makeText(requireContext(),getString(R.string.card_number_message),Toast.LENGTH_LONG).show()
            }

        }

        binding.addCardButton.setOnClickListener {

            if (binding.cardOption.isChecked) {
                val fragment: Fragment = AddCardFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.thankyou, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        if (cardNumber != null) {
            binding.cardNumber.text = String.format(resources.getString(R.string.card_mask), cardNumber.substring(12, maxLengthCard))

            if(binding.cardOption.isChecked){
                binding.cardUser.strokeColor = ContextCompat.getColor(requireContext(),R.color.cian)

            }
        }
        if (cardYear != null) {
            binding.cardExpirationDate.text = cardYear.toString()
        }
        if(cardOwner != null){
            binding.cardUserName.text = cardOwner.toString()
        }



        viewModel = ViewModelProvider(this).get(PaymentMethodViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.apply {
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.thankyou, fragment)
            }
            fragmentManager?.fragments?.forEach {
                if (it != fragment && it.isAdded) {
                    hide(it)
                }
            }
        }?.commit()
    }

}