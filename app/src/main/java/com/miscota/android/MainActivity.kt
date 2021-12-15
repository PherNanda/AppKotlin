package com.miscota.android

import android.R.attr.*
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.*
import com.google.firebase.perf.ktx.performance
import com.miscota.android.address.AddressActivity
import com.miscota.android.auth.AuthActivity
import com.miscota.android.connection.ConnectionManager
import com.miscota.android.databinding.ActivityMainBinding
import com.miscota.android.ui.cart.CartActivity
import com.miscota.android.ui.connection.ConnectionStateFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val viewModel by viewModel<MainActivityViewModel>()

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private val broadcastReceiver by lazy {
        ConnectionManager.create({
            binding.connectionOff.visibility = View.GONE
            binding.locationLinearLayoutmain.visibility = View.VISIBLE
            binding.navView.visibility = View.VISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }, {
            viewDisconnected()
        })
    }


    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.navigation_orders) {


                if (!viewModel.loguedIn.value!!) {

                    viewModel.setShowAuth(null)
                }
                if (viewModel.loguedIn.value!!) {
                    viewModel.setShowAuth("1")
                }
            }
            if (destination.id == R.id.navigation_profile) {

                if (!viewModel.loguedIn.value!!) {
                    viewModel.setShowAuth(null)
                }
                if (viewModel.loguedIn.value!!) {
                    viewModel.setShowAuth("1")
                }
            }

            if (destination.id == R.id.mainCategoriesFragment) {
                println(" destination 1 $destination")

            }
            if (destination.id == R.id.navigation_home) {
                print("destination home $destination")
                //viewModel.setShowAuth("1")
                //controller.navigate(R.id.navigation_home)
                println(" destination 2 $destination")

            }
            if (destination.id == R.id.navigation_product) {

                println(" destination 3 $destination")

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.authStore.getStatus()
        println("viewModel.authStore.getStatus() ${viewModel.authStore.getStatus()}")
        println("viewModel.statusConnect ${viewModel.statusConnect.value}")


        viewModel.statusConnect.observe(this){
            println("viewModel.statusConnect observe ${viewModel.statusConnect.value}")
            if (viewModel.statusConnect.value!!){
                viewErrorApi()
            }
            return@observe
        }

        viewModel.loadAddress()

        viewModel.loadSelectedLocation()

        binding.fragmentHome.visibility = View.VISIBLE

       /** binding.samedayInfoMain.setOnClickListener {

           binding.samedayInfoMain.setBackgroundDrawable(applicationContext.getDrawable(R.drawable.background_same_day_on))
           binding.textSamedayMain.text = getString(R.string.title_sameday)
           binding.textSamedayMain.text = colorMyText(getString(R.string.title_sameday),0,11, ContextCompat.getColor(this, R.color.app_pink))
           binding.imageSamedayCheck.visibility = View.VISIBLE

        }**/

        val sameDayExplainOne = findViewById<TextView>(R.id.same_day_explain_one)
        sameDayExplainOne.text = boldColorMyText(getString(R.string.second_text_info_sameday_one),startIndex,lastIndex, ContextCompat.getColor(this, R.color.app_pink))

        val sameDayExplainTwo = findViewById<TextView>(R.id.same_day_explain_two)
        sameDayExplainTwo.text = boldColorMyText(getString(R.string.second_text_info_sameday_two),startIndex,thirthFive, ContextCompat.getColor(this, R.color.app_pink))

        val sameDayExplainThree = findViewById<TextView>(R.id.same_day_explain_three)
        sameDayExplainThree.text = boldColorMyText(getString(R.string.second_text_info_sameday_three),startIndex,fourth, ContextCompat.getColor(this, R.color.app_pink))

        val sameDayExplainFour = findViewById<TextView>(R.id.same_day_explain_four)
        sameDayExplainFour.text = boldColorMyText(getString(R.string.second_text_info_sameday_four),startIndex,endIndex, ContextCompat.getColor(this, R.color.app_pink))

        val navigationHome = findViewById<BottomNavigationItemView>(R.id.navigation_home)

        navigationHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            //navController.navigate(R.id.navigation_home)
        }

        val llBottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        sheetBehavior = BottomSheetBehavior.from(llBottomSheet)

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.fragmentHome.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->
                        binding.fragmentHome.visibility = View.INVISIBLE
                    BottomSheetBehavior.STATE_COLLAPSED ->
                        binding.fragmentHome.visibility = View.INVISIBLE
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        binding.fragmentHome.visibility = View.INVISIBLE
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        binding.fragmentHome.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.samedayInfo.setOnClickListener {
            expandCloseSheet()
        }

        binding.locationLinearLayoutmain.setOnClickListener {
            startActivity(Intent(this, AddressActivity::class.java))
        }


        val myTrace = Firebase.performance.newTrace("MainActivity")
        myTrace.start()
        myTrace.stop()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        viewModel.getTotalItens()

        viewModel.requestID.observe(this) {
            if (viewModel.requestID.value != null) {

                if (viewModel.requestID.value!!.retail_shop_id == idEcommerce) {

                    binding.textSamedayMain.text = getString(R.string.same_day_off)
                    binding.imageSamedayCheck.visibility = View.GONE
                    //binding.imageSamedayMain.visibility = View.GONE
                    binding.textSamedayMain.text = colorMyText(
                        getString(R.string.same_day_off),
                        11,
                        37,
                        ContextCompat.getColor(this, R.color.app_pink)
                    )

                    binding.textSamedayMainInfo.text =
                        boldMyText(getString(R.string.text_sameday_off_info), 13, lastIndex)

                }
                if (viewModel.requestID.value!!.retail_shop_id != idEcommerce) {

                    binding.samedayInfoMain.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.background_same_day_on
                        )
                    )
                    binding.textSamedayMain.text = getString(R.string.title_sameday)
                    binding.textSamedayMain.text = colorMyText(
                        getString(R.string.title_sameday),
                        startIndex,
                        11,
                        ContextCompat.getColor(this, R.color.app_pink)
                    )
                    binding.imageSamedayCheck.visibility = View.VISIBLE
                    binding.imageSamedayMain.visibility = View.VISIBLE

                    //println("line238 MainActivity inn::: line 249 \n ${viewModel.authStore.getRetailID()} ")
                }
            }
        }


        viewModel.selectedLocation.observe(this) {
            if (it != null ) {
                changeSizeMyText(18F,binding.locationTextMain)
                binding.locationTextMain.text=
                    String.format(it.postalCode+ ", " + it.city)

                it.postalCode.let { postalCode ->
                     println("viewModel.statusConnect.value main line 286 ${viewModel.statusConnect.value}  viewModel.authStore.getStatus() ${viewModel.authStore.getStatus()}")
                     viewModel.checkPostalCode(postalCode)
                }
            }
            if (it == null){
                binding.textSamedayMain.text = getString(R.string.text_default_main)
                binding.imageSamedayCheck.visibility = View.GONE
                binding.imageSamedayMain.visibility = View.GONE
                binding.samedayInfoMain.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.background_same_day_off
                    )
                )

                binding.samedayInfo.visibility = View.GONE

                //binding.samedayInfoBottom.gravity = Gravity.END
                changeSizeMyText(14F,binding.textSamedayMain)
                val gravity = Gravity.CENTER

                val paramss: ViewGroup.LayoutParams =  binding.samedayInfoBottom.layoutParams
                paramss.height = ViewGroup.LayoutParams.WRAP_CONTENT
                paramss.width = ViewGroup.LayoutParams.MATCH_PARENT

                binding.samedayInfoBottom.gravity = gravity
                binding.samedayInfoBottom.layoutParams = paramss



            }

        }

        if(viewModel.getTotalItens() == 0)
        {
            binding.cartItemsText.visibility = View.INVISIBLE
        }
        else {
            binding.cartItemsText.text = viewModel.getTotalItens().toString()
            binding.cartItemsText.visibility = View.VISIBLE
        }

        binding.cartItemsText.setOnClickListener {
            viewModel.getTotalItens()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.storeImage.setOnClickListener {
                goToCartScreen()
                viewModel.getTotalItens()
        }
        navController.addOnDestinationChangedListener(listener)

        binding.navView.setupWithNavController(navController)

        binding.imageBack.setOnClickListener {
            navController.navigateUp()
            //startActivity(Intent(this, MainActivity::class.java))
        }

        binding.logoImage.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        val navigationProfile = findViewById<BottomNavigationItemView>(R.id.navigation_profile)
        viewModel.openLoginActivityEvent.observe(this) {
            it.consume()?.let {
                if(!viewModel.getShowAuth()) {
                    goToLogin()
                }
            }
        }

        viewModel.loguedIn.observe(this){
            return@observe
        }

        viewModel.costEcommerce.observe(this){
            viewModel.costEcommerce.value
            return@observe
        }
        viewModel.costSd.observe(this){
            viewModel.costSd.value
            return@observe
        }

    }

    private fun expandCloseSheet() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.samedayBottomInfo.visibility = View.VISIBLE
            binding.fragmentHome.visibility = View.INVISIBLE
        } else if(sheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN)  {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.fragmentHome.visibility = View.VISIBLE
        }
        else {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.samedayBottomInfo.visibility = View.GONE
            binding.fragmentHome.visibility = View.INVISIBLE
        }
    }

    private fun goToCartScreen() {

        viewModel.getTotalItens()

        startActivity(Intent(this, CartActivity::class.java))



        val currentDate = Date()
        val dateExpire = viewModel.getDateLoginExpire()
        viewModel.comproveLoginExpireDate(currentDate, dateExpire)

    }

    private fun goToLogin() {
         startActivity(Intent(this, AuthActivity::class.java))
         viewModel.setShowAuth("1")
    }


    override fun onStart() {
        super.onStart()

        println("onStartMain \n")
        println("viewModel.authStore.getStatus() onStartMain ${viewModel.authStore.getStatus()}")
        println("viewModel.statusConnect onStartMain ${viewModel.statusConnect.value}")

        viewModel.getTotalItens()
        viewModel.costEcommerce
        viewModel.costSd

        val currentDate = Date()
        val dateExpire = viewModel.getDateLoginExpire()
        viewModel.comproveLoginExpireDate(currentDate, dateExpire)

        viewModel.loadSelectedLocation()

        viewModel.requestID.observe(this) {
            return@observe
        }


        if (viewModel.authStore.getRetailID() != null) {

            if (viewModel.authStore.getRetailID() == idEcommerce ){

                binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.background_home_new_search))
                binding.textSamedayMain.text = getString(R.string.same_day_off)
                binding.imageSamedayCheck.visibility = View.GONE
                binding.textSamedayMain.text = colorMyText(getString(R.string.same_day_off),11,37, ContextCompat.getColor(this, R.color.app_pink))

            }

            if (viewModel.authStore.getRetailID()  != idEcommerce ){

                binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.background_same_day_on))
                binding.textSamedayMain.text = getString(R.string.title_sameday)
                binding.textSamedayMain.text = colorMyText(getString(R.string.title_sameday),0,11, ContextCompat.getColor(this, R.color.app_pink))
                binding.imageSamedayCheck.visibility = View.VISIBLE


                //println("onStartMain viewModel.authStore.getRetailID() MainActivity inn::: line 449 \n ${viewModel.authStore.getRetailID()} ")

            }
        }


        viewModel.selectedLocation.observe(this) {
            if (it == null){
                binding.textSamedayMain.text = getString(R.string.text_default_main)
                binding.imageSamedayCheck.visibility = View.GONE

            }
            if (it != null){

                changeSizeMyText(18F,binding.locationTextMain)
                binding.locationTextMain.text=
                    String.format(it.postalCode+ ", " + it.city)


                it.postalCode.let { postalCode ->
                    viewModel.checkPostalCode(postalCode)

                    }
                }

            }

        }


    override fun onResume() {
        super.onResume()


        //Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))

        println("onResumeMain")
        println("viewModel.authStore.getStatus() onResumeMain ${viewModel.authStore.getStatus()}")
        println("viewModel.statusConnect onResumeMain ${viewModel.statusConnect.value}")

        viewModel.getTotalItens()
        binding.cartItemsText.text = viewModel.getTotalItens().toString()

        if(viewModel.statusConnect.value!!){
            viewErrorApi()
        }

        ConnectionManager.register(this, broadcastReceiver)


    }

    override fun onPause() {
        super.onPause()

        println("onPauseMain")
        println("viewModel.authStore.getStatus() onPauseMain ${viewModel.authStore.getStatus()}")
        println("viewModel.statusConnect onPauseMain ${viewModel.statusConnect.value}")

        if(viewModel.statusConnect.value!!){
            viewErrorApi()
        }

        ConnectionManager.unregister(this, broadcastReceiver)
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestartMain")

        viewModel.loadSelectedLocation()

        viewModel.selectedLocation.observe(this) {
            if (it != null ) {
                changeSizeMyText(16F,binding.locationTextMain)
                binding.locationTextMain.text=
                    String.format(it.postalCode+ ", " + it.city)


                viewModel.checkPostalCode(it.postalCode)

                if (viewModel.authStore.getRetailID() != null) {

                if (viewModel.authStore.getRetailID() == idEcommerce ){

                binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.background_home_new_search))
                binding.textSamedayMain.text = getString(R.string.same_day_off)
                binding.imageSamedayCheck.visibility = View.GONE
                binding.textSamedayMain.text = colorMyText(getString(R.string.same_day_off),11,37, ContextCompat.getColor(this, R.color.app_pink))
                binding.locationTextMain.text = colorMyText(it.postalCode+ ", " + it.city,0,binding.locationTextMain.text.length,  ContextCompat.getColor(this, R.color.new_app_grey))

                }
                if (viewModel.authStore.getRetailID() != idEcommerce ){

                binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.background_same_day_on))
                binding.textSamedayMain.text = getString(R.string.title_sameday)
                binding.textSamedayMain.text = colorMyText(getString(R.string.title_sameday),0,11, ContextCompat.getColor(this, R.color.app_pink))
                binding.imageSamedayCheck.visibility = View.VISIBLE

                    }
                }

            }
            if (it == null){
                binding.samedayInfoMain.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
                binding.textSamedayMain.text = getString(R.string.text_default_main)
                binding.imageSamedayCheck.visibility = View.GONE
                binding.samedayInfo.visibility = View.GONE
                binding.imageSamedayMain.visibility = View.GONE

                binding.samedayInfoBottom.gravity = Gravity.END

                changeSizeMyText(14F,binding.textSamedayMain)
                val gravity = Gravity.CENTER

                val paramss: ViewGroup.LayoutParams =  binding.samedayInfoBottom.layoutParams
                paramss.height = ViewGroup.LayoutParams.WRAP_CONTENT
                paramss.width = ViewGroup.LayoutParams.MATCH_PARENT

                binding.samedayInfoBottom.gravity = gravity
                binding.samedayInfoBottom.layoutParams = paramss


            }
        }


        if (viewModel.requestID.value?.retail_shop_id != null) {

            if (viewModel.authStore.getRetailID() == idEcommerce ){

                binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.background_home_new_search))
                binding.textSamedayMain.text = getString(R.string.same_day_off)
                binding.imageSamedayCheck.visibility = View.GONE
                binding.textSamedayMain.text = colorMyText(getString(R.string.same_day_off),11,37, ContextCompat.getColor(this, R.color.app_pink))

            }
            if (viewModel.authStore.getRetailID() != idEcommerce ){

                binding.samedayInfoMain.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.background_same_day_on))
                binding.textSamedayMain.text = getString(R.string.title_sameday)
                binding.textSamedayMain.text = colorMyText(getString(R.string.title_sameday),0,11, ContextCompat.getColor(this, R.color.app_pink))
                binding.imageSamedayCheck.visibility = View.VISIBLE

            }

        }
        println("onRestartMain")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroyMain")
    }

    private fun colorMyText(inputText:String,startIndex:Int,endIndex:Int,textColor:Int): Spannable {
        val outPutColoredText: Spannable = SpannableString(inputText)
        outPutColoredText.setSpan(
            ForegroundColorSpan(textColor), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return outPutColoredText
    }

    private fun boldMyText(inputText:String,startIndex:Int,endIndex:Int): Spannable {
        val outPutBoldText: Spannable = SpannableString(inputText)
        outPutBoldText.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldText
    }

    private fun changeSizeMyText( size:Float, textView: TextView): TextView {
        textView.textSize = size
        return textView
    }

    private fun boldColorMyText(inputText:String,startIndex:Int,endIndex:Int,textColor:Int): Spannable {
        val outPutBoldColorText: Spannable = SpannableString(inputText)
        outPutBoldColorText.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        outPutBoldColorText.setSpan(
        ForegroundColorSpan(textColor), startIndex, endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return outPutBoldColorText
    }


    override fun onStop() {
        super.onStop()

        viewModel.loadSelectedLocation()
        viewModel.selectedLocation.observe(this) {
            return@observe
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        //navController.navigateUp()
    }

    private fun viewDisconnected(){
        binding.connectionOff.visibility = View.VISIBLE
        binding.locationLinearLayoutmain.visibility = View.GONE
        binding.navView.visibility = View.GONE

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.add(R.id.connectionOff, ConnectionStateFragment())
        ft.commit()

    }

    private fun viewErrorApi(){

        viewDisconnected()
        
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val startIndex = 0
        private const val endIndex = 23
        private const val idEcommerce = "0"
        private const val lastIndex = 30
        private const val fourth = 40
        private const val thirthFive = 35
    }


}
