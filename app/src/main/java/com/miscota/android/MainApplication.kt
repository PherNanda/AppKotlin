package com.miscota.android


import android.app.Application
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.google.firebase.analytics.*
//import io.emma.android.EMMA
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainApplication :Application() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        applicationContext.setTheme(R.style.Theme_MaterialComponents_Light)
        super.onCreate()
        /**if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        }**/


        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@MainApplication)
            modules(koinModules)
        }

        var keyEmma = getString(R.string.key_emma)
        //Emma  antiquy .setSessionKey("7EA059029AFCAa89cb2f6910aacc55376")
        /**val configuration = EMMA.Configuration.Builder(this)
            .setSessionKey(keyEmma)
            .setDebugActive(BuildConfig.DEBUG)
            .build()

        EMMA.getInstance().startSession(configuration)**/
        //End Emma


        // Add code to print out the key hash
        // Add code to print out the key hash
        try {
            val info = packageManager.getPackageInfo(
                "com.miscota.android",
                PackageManager.GET_SIGNATURES
            )

            val infoTwo = packageManager.getPackageInfo(
                "com.miscota.android",
                PackageManager.GET_CONFIGURATIONS
            )

            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }

            //Huella digital del certificado SHA‑1
            val input = "EF:89:C8:B5:51:C9:86:71:7A:85:0D:24:EF:22:53:EB:79:B5:26:18"
            val inputWithout = input.replace(":","")
            val bytes = inputWithout.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            val encodeBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            println(" KeyHash Release: $encodeBase64 ")

        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
    }





}