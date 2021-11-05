package com.miscota.android.ui.addcard

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.miscota.android.R
import com.miscota.android.databinding.AddCardFragmentBinding
import com.miscota.android.ui.checkoutpayment.PaymentMethod
import com.miscota.android.ui.paymentmethod.PaymentMethodFragment
import com.miscota.android.util.autoClean
import io.card.payment.CardIOActivity


class AddCardFragment : Fragment() {

    companion object {
        fun newInstance() = AddCardFragment()
        const val maxLengthCard = 16
    }

    private lateinit var viewModel: AddCardViewModel

    private var binding by autoClean<AddCardFragmentBinding>()

    private var paymentMethod: PaymentMethod? = null
    private var paymentMethodWithout: PaymentMethod? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.cartHeader.text = getString(R.string.add_card_number)

        binding.toolbar.imageBack.setOnClickListener {
            fragmentManager?.popBackStackImmediate()

        }

        binding.cardNumberInput.requestFocus()
        //showKeyboard(binding.cardNumberInput)

        val inputCardNumber = binding.cardNumberInput.text
        val inputDateCardNumber = binding.expirationCardInput.text
        val inputOwnerName = binding.cardUserInput.text
        val inputSecurity = binding.securityCardInput.text

        binding.expirationCardLayout.editText?.text = inputDateCardNumber
        binding.securityCardLayout.editText?.text = inputSecurity
        binding.userNameCardLayout.editText?.text = inputOwnerName
        binding.cardNumberLayout.editText?.text = inputCardNumber

        var inputCard = inputCardNumber
        var inputDateCard = inputDateCardNumber
        var inputOwnerN = inputOwnerName
        var inputSecure = inputSecurity

        binding.cardNumberInput.doAfterTextChanged { inputCardNumbers ->
            //(requireActivity() as MainActivity).binding.navView.menu.getItem(1).isChecked = true

            if (inputCardNumbers != null && inputCardNumbers.length < maxLengthCard) {
                var cardNumberError = ""


                if (TextUtils.isEmpty(binding.cardNumberInput.text) || inputCardNumbers.length < 17) {
                    cardNumberError = getString(R.string.not_valid_card)

                    /**binding.cardNumberInput.setError(
                        getString(R.string.not_valid_card),
                        getDrawable(requireContext(), R.drawable.ic_card_check_off)
                    )**/
                }
                binding.cardNumberLayout.error = cardNumberError
                binding.cardNumberLayout.isErrorEnabled = true

            } else if (inputCardNumbers != null && inputCardNumbers.length == maxLengthCard) {

                var cardNumberMsgOk = ""
                if (!TextUtils.isEmpty(binding.cardNumberInput.text) && inputCardNumbers.length == maxLengthCard) {
                    cardNumberMsgOk = getString(R.string.perfect)

                    binding.cardNumberLayout.endIconDrawable =
                        getDrawable(requireContext(), R.drawable.ic_card_check_on)
                    inputCard = binding.cardNumberInput.text


                    paymentMethodWithout =
                        PaymentMethod(
                            type = "scheme",
                            encryptedCardNumber = inputCard.toString(),
                            encryptedExpiryMonth = inputDateCard.toString(),
                            encryptedExpiryYear = inputDateCard.toString(),
                            encryptedSecurityCode = inputSecure.toString(),
                            encryptedUserName = inputOwnerN.toString()
                        )



                    println("\n payment::::1 ${paymentMethodWithout?.encryptedExpiryMonth}")
                    println("\n payment::::2 ${paymentMethodWithout?.encryptedCardNumber}") //payment::::2 4047 0000 2109 1662
                    println("\n payment::::3 ${paymentMethodWithout?.encryptedExpiryYear}")
                    println("\n payment::::4 ${paymentMethodWithout?.encryptedSecurityCode}")

                    println("\n inputCard Card $inputCard") //inputCard Card 4047 0000 2100 1562

                }
                binding.cardNumberLayout.error = cardNumberMsgOk
                binding.cardNumberLayout.isErrorEnabled = false
                binding.cardNumberInput.text


            }

        }

        /**val s: StringBuilder =
            java.lang.StringBuilder(binding.cardNumberInput.text.toString())

        var i = 4
        while (i < s.length) {
            s.insert(i, " ")
            i += 5
        }
        println("sssss $s")
        binding.cardNumberInput.setText(s.toString())**/


        //showKeyboard(binding.expirationCardInput)

        binding.expirationCardInput.doOnTextChanged { inputExpirationNumber, _, _, _ ->

            if (inputExpirationNumber != null && inputExpirationNumber.length < 5) {
                var dateNumberError = ""

                if (TextUtils.isEmpty(binding.expirationCardInput.text) || inputExpirationNumber.length < 5) {
                    dateNumberError = getString(R.string.expiration_message)
                }
                binding.expirationCardInput.error = dateNumberError
                binding.expirationCardLayout.isErrorEnabled = true

            } else if (inputExpirationNumber != null && inputExpirationNumber.length == 5) {
                binding.expirationCardLayout.endIconDrawable =
                    getDrawable(requireContext(), R.drawable.ic_card_check_on)
                binding.expirationCardLayout.isErrorEnabled = false
                binding.expirationCardInput.text

                inputDateCard = binding.expirationCardInput.text

                paymentMethodWithout =
                    PaymentMethod(
                        type = "scheme",
                        encryptedCardNumber = inputCard.toString(),
                        encryptedExpiryMonth = inputDateCard.toString(),
                        encryptedExpiryYear = inputDateCard.toString(),
                        encryptedSecurityCode = inputSecure.toString(),
                        encryptedUserName = inputOwnerN.toString()
                    )
                //payment.invoke(uiModel.copy(payment = paymentMethodWithout))

                println("\n payment::::5 ${paymentMethodWithout?.encryptedExpiryMonth}") //03/26
                println("\n payment::::6 ${paymentMethodWithout?.encryptedCardNumber}") //4047 0000 2100 1562
                println("\n payment::::7 ${paymentMethodWithout?.encryptedExpiryYear}") //03/26
                println("\n payment::::8 ${paymentMethodWithout?.encryptedSecurityCode}")

                println("\n inputDateCard $inputDateCard") //inputDateCard 03/26
            }
        }

        binding.cardUserInput.doOnTextChanged { inputOwner, _, _, _ ->

            if (inputOwner != null && inputOwner.length < 7) {
                var ownerNameError = ""

                if (TextUtils.isEmpty(binding.cardUserInput.text) || inputOwner.length < 7) {
                    ownerNameError = getString(R.string.username_card_required)
                }
                binding.cardUserInput.error = ownerNameError
                binding.userNameCardLayout.isErrorEnabled = true

            } else if (inputOwner != null && inputOwner.length > 6) {

                binding.userNameCardLayout.endIconDrawable =
                    getDrawable(requireContext(), R.drawable.ic_card_check_on)
                binding.userNameCardLayout.isErrorEnabled = false

                inputOwnerN = binding.cardUserInput.text

                println(" inputOwnerN test $inputOwnerN")

                paymentMethodWithout =
                    PaymentMethod(
                        type = "scheme",
                        encryptedCardNumber = inputCard.toString(),
                        encryptedExpiryMonth = inputDateCard.toString(),
                        encryptedExpiryYear = inputDateCard.toString(),
                        encryptedSecurityCode = inputSecure.toString(),
                        encryptedUserName = inputOwnerN.toString()
                    )

                println("\n inputOwnerN $inputOwnerN") //inputOwnerN fernanda Gonçalves

            }
        }

        binding.securityCardInput.doOnTextChanged { inputSecurityCard, _, _, _ ->

            if (inputSecurityCard != null && inputSecurityCard.length < 3) {
                var securityNumberError = ""

                if (TextUtils.isEmpty(binding.securityCardInput.text) || inputSecurityCard.length < 3) {
                    securityNumberError = getString(R.string.cvv_required_message)
                }
                binding.securityCardInput.error = securityNumberError
                binding.securityCardLayout.isErrorEnabled = true

            } else if (inputSecurityCard != null && inputSecurityCard.length == 3) {
                binding.securityCardLayout.endIconDrawable =
                    getDrawable(requireContext(), R.drawable.ic_card_check_on)
                binding.securityCardLayout.isErrorEnabled = false
                binding.securityCardInput.text

                inputSecure = binding.securityCardInput.text

                paymentMethodWithout =
                    PaymentMethod(
                        type = "scheme",
                        encryptedCardNumber = inputCard.toString(),
                        encryptedExpiryMonth = inputDateCard.toString(),
                        encryptedExpiryYear = inputDateCard.toString(),
                        encryptedSecurityCode = inputSecure.toString(),
                        encryptedUserName = inputOwnerN.toString()
                    )

                println("\n paymentMethodWithout|||| ${paymentMethodWithout!!.encryptedSecurityCode}") //987
                println("\n payment::::9 ${paymentMethodWithout?.encryptedExpiryMonth}") //03/26
                println("\n payment::::10 ${paymentMethodWithout?.encryptedCardNumber}") //4047 0000 2100 1562
                println("\n payment::::11 ${paymentMethodWithout?.encryptedExpiryYear}") //03/26
                println("\n payment::::12 ${paymentMethodWithout?.encryptedSecurityCode}") //987
                println("\n payment::::9-1 ${paymentMethodWithout?.encryptedUserName}") //987


                println("\n inputSecure $inputSecure") //inputSecure 987

                println("  test1 paymentMethodWithout!!.encryptedUserName ${paymentMethodWithout!!.encryptedUserName}")

                //paymentMethodWithout?.encryptedUserName?.length!! > 7

                if (paymentMethodWithout?.encryptedSecurityCode?.length == 3 &&
                    paymentMethodWithout?.encryptedExpiryMonth?.length == 5 &&
                    paymentMethodWithout?.encryptedCardNumber?.trim()?.length == maxLengthCard &&
                    paymentMethodWithout?.encryptedExpiryYear?.length == 5 &&
                    paymentMethodWithout?.encryptedUserName?.length!! > 7

                ) {
                    println(" after if paymentMethodWithout!!.encryptedUserName ${paymentMethodWithout!!.encryptedUserName}")
                    val bundlePayment = Bundle()
                    bundlePayment.putString(
                        getString(R.string.card_security_encrypt),
                        paymentMethodWithout!!.encryptedSecurityCode
                    )
                    bundlePayment.putString(
                        getString(R.string.card_expire_encrypt),
                        paymentMethodWithout!!.encryptedExpiryMonth
                    )
                    bundlePayment.putString(
                        getString(R.string.card_number_encrypt),
                        paymentMethodWithout!!.encryptedCardNumber
                    )
                    bundlePayment.putString(
                        getString(R.string.card_expire_year_encrypt),
                        paymentMethodWithout!!.encryptedExpiryYear
                    )
                    println(" before test paymentMethodWithout!!.encryptedUserName ${paymentMethodWithout!!.encryptedUserName}")
                    bundlePayment.putString(
                        getString(R.string.card_user_encrypt),
                        paymentMethodWithout!!.encryptedUserName
                    )

                    println("  test paymentMethodWithout!!.encryptedUserName ${paymentMethodWithout!!.encryptedUserName}")

                    paymentMethod = PaymentMethod(
                        type = "scheme",
                        encryptedCardNumber = paymentMethodWithout!!.encryptedSecurityCode,
                        encryptedExpiryMonth = paymentMethodWithout!!.encryptedExpiryMonth,
                        encryptedExpiryYear = paymentMethodWithout!!.encryptedExpiryYear,
                        encryptedSecurityCode = paymentMethodWithout!!.encryptedSecurityCode,
                        encryptedUserName = paymentMethodWithout!!.encryptedUserName
                    )

                }

            }
        }



        println("\n payment::::13 ${paymentMethodWithout?.encryptedExpiryMonth}") //null
        println("\n payment::::14 ${paymentMethodWithout?.encryptedCardNumber}") //null
        println("\n payment::::15 ${paymentMethodWithout?.encryptedExpiryYear}") //null
        println("\n payment::::16 ${paymentMethodWithout?.encryptedSecurityCode}") //null

        if (paymentMethodWithout?.encryptedSecurityCode != null) {
            /** paymentMethod = encrypt(
            paymentMethodWithout?.encryptedCardNumber!!,
            paymentMethodWithout?.encryptedExpiryMonth!!.substring(0, 2),
            paymentMethodWithout?.encryptedExpiryYear!!.substring(3, 5),
            paymentMethodWithout?.encryptedSecurityCode!!,
            inputOwnerN.toString()
            )**/
        }



        binding.cardCameraButton.setOnClickListener {
            Toast.makeText(requireContext(), "Hola", Toast.LENGTH_LONG).show()

            val scanIntent = startActivity(Intent(requireContext(), CardIOActivity::class.java))

            // customize these values to suit your needs.

            // customize these values to suit your needs.
            /**scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true) // default: false

            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false) // default: false

            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false) // default: false


            // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.

            // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
            startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE)**/
        }

        binding.addCardButtonTwo.setOnClickListener {

            if (paymentMethodWithout != null) {

                if (paymentMethodWithout?.encryptedSecurityCode?.length == 3 &&
                    paymentMethodWithout?.encryptedExpiryMonth?.length == 5 &&
                    paymentMethodWithout?.encryptedCardNumber?.length == maxLengthCard &&
                    paymentMethodWithout?.encryptedExpiryYear?.length == 5 &&
                    paymentMethodWithout?.encryptedUserName?.length!! > 6
                ) {

                    val bundlePayment = Bundle()
                    bundlePayment.putString(
                        getString(R.string.card_security_encrypt),
                        paymentMethodWithout!!.encryptedSecurityCode
                    )
                    bundlePayment.putString(
                        getString(R.string.card_expire_encrypt),
                        paymentMethodWithout!!.encryptedExpiryMonth
                    )
                    bundlePayment.putString(
                        getString(R.string.card_number_encrypt),
                        paymentMethodWithout!!.encryptedCardNumber
                    )
                    bundlePayment.putString(
                        getString(R.string.card_expire_year_encrypt),
                        paymentMethodWithout!!.encryptedExpiryYear
                    )
                    bundlePayment.putString(
                        getString(R.string.card_user_encrypt),
                        paymentMethodWithout!!.encryptedUserName
                    )


                    val fragment = PaymentMethodFragment()
                    fragment.arguments = bundlePayment
                    replaceFragment(fragment)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.card_same_data_required), Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(requireContext(), getString(R.string.card_required_message), Toast.LENGTH_LONG).show()

            }

        }

            viewModel = ViewModelProvider(this).get(AddCardViewModel::class.java)
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