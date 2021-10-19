package com.miscota.android.auth.login.ui

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.miscota.android.R
import com.miscota.android.afterTextChanged
import com.miscota.android.auth.AuthViewModel
import com.miscota.android.auth.forgotpassword.ui.ForgotPasswordActivity
import com.miscota.android.databinding.FragmentLoginBinding
import com.miscota.android.util.autoClean
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

enum class ProviderType{
    FACEBOOK,
    GOOGLE
}
class LoginFragment : Fragment() {

    private var binding by autoClean<FragmentLoginBinding>()

    private val loginViewModel by viewModel<LoginViewModel>()

    private val authViewModel by sharedViewModel<AuthViewModel>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var auth: FirebaseAuth

    // fb login attributes
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager
    private lateinit var callbackManagerFB: CallbackManager
    private lateinit var loginManagerFB: LoginManager

    // google login attributes
    private val googleSignInRegistration =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            handleGoogleSignInResult(task)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()
        callbackManagerFB = CallbackManager.Factory.create()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebase
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        recordScreenViewLogin()

        with(binding) {
            loginViewModel.loginFormState.observe(viewLifecycleOwner) {
                val loginState = it ?: return@observe

                // disable login button unless both username / password is valid
                loginButton.isEnabled = loginState.isDataValid

                if (loginState.usernameError != null) {
                    usernameLayout.error = getString(loginState.usernameError)
                } else {
                    usernameLayout.error = null
                }
                if (loginState.passwordError != null) {
                    passwordLayout.error = getString(loginState.passwordError)
                } else {
                    passwordLayout.error = null
                }
            }

            loginViewModel.loginResult.observe(viewLifecycleOwner) {
                val loginResult = it ?: return@observe

                defaultState()
                if (loginResult.error != null) {
                    showLoginFailed(loginResult.error)
                }
                if (loginResult.success != null) {
                   updateUiWithUser(loginResult.success)
                }
            }

            username.afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            password.apply {
                afterTextChanged {
                    loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                    )
                }

                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                            )
                    }
                    false
                }

                loginButton.setOnClickListener {
                    if ( username.text?.isNotEmpty() == true && password.text?.isNotEmpty() == true ) {
                        loadingState()
                        loginViewModel.login(username.text.toString(), password.text.toString())
                    }
                    else{
                        Toast.makeText(requireContext(),getString(R.string.invalid_pass_user),Toast.LENGTH_LONG).show()
                    }
                }

                forgotButton.setOnClickListener {
                    goToForgotPasswordScreen()
                }
            }
        }

        binding.signUpButton.text = boldColorMyText(getString(R.string.create_account_new_app),19,binding.signUpButton.text.length,getColor(requireContext(), R.color.app_blue))
        binding.signUpButton.setOnClickListener {
            authViewModel.setScreen(AuthViewModel.Screen.SignUpScreen)
        }

        initializeFacebookLogin()

        initializeGoogleSignIn()

        initializeFacebookLoginFB()

        initializeGoogleSignInFB()
    }

    private fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id_fb))
            .requestEmail()
            .requestProfile()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.googleLoginButton.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            googleSignInRegistration.launch(signInIntent)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {

            val account: GoogleSignInAccount =
                requireNotNull(completedTask.getResult(ApiException::class.java))

            loginViewModel.loginWithGoogle(
                email = requireNotNull(account.email),
                name = requireNotNull(account.displayName),
                accessToken = requireNotNull(account.idToken),
            )


            println("account.serverAuthCode "+account.serverAuthCode) //null
            println("account.idToken "+account.idToken) //eyJhbGciOiJSUzI1NiIsImtpZCI6I....
            println("account.id "+account.id) //107767334923251488660
            println("account.grantedScopes "+account.grantedScopes) //[https://www.googleapis.com/auth/userinfo.profile, https://www.googleapis.com/auth/userinfo.email, openid, profile, email]
            println("account.photoUrl "+account.photoUrl) ////lh3.googleusercontent.com/a/AATXAJwhjs96nULBh0LRbuNZgAgmi3TlBsO_GA-xD257=s96-c
            println("account.familyName "+account.familyName) //Test
            println("account.givenName "+account.givenName) //Fernanda
            println("account.email "+account.email) //fernanda.adeveloper@gmail.com
            println("account.isExpired "+account.isExpired) //false

        } catch (e: ApiException) {
            println("Error GoogleSign $e")
            println("Error GoogleSign StackTrace"+e.printStackTrace())
            println("Error GoogleSign Status"+e.status)
            println("Error GoogleSign StatusCode"+e.statusCode)
            println("Error GoogleSign StatusCode"+e.cause)
            println("Error GoogleSign StatusCode"+e.fillInStackTrace())

            defaultState()
        }
    }

    private fun initializeGoogleSignInFB() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id_fb))
            .requestEmail()
            .requestProfile()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.googleLoginButtonFB.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            googleSignInRegistration.launch(signInIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        callbackManagerFB.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun goToForgotPasswordScreen() {
        startActivity(Intent(requireContext(), ForgotPasswordActivity::class.java))
    }

    private fun updateUiWithUser(model: LoggedInUserView) {

        //start event login
        val methodName = "LoginResultSucess"
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, methodName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "LoginFragment")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        //end event login

        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
        requireActivity().setResult(Activity.RESULT_OK)

        requireActivity().finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
    }

    private fun initializeFacebookLogin() {
        binding.fbLoginButton.setOnClickListener {
            loginManager = LoginManager.getInstance()
            loginManager.logInWithReadPermissions(
                this,
                listOf(FB_EMAIL_READ_PERMISSION, FB_PROFILE_READ_PERMISSION)
            )
            loginManager.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        val accessToken: AccessToken = loginResult.accessToken
                        // Facebook Email address
                        val request = GraphRequest.newMeRequest(
                            loginResult.accessToken
                        ) { jsonObject, response ->
                            Timber.v(String.format(getString(R.string.login_response)+" $response"))
                            try {
                                val name = jsonObject.getString(getString(R.string.name_fb))
                                val fbEmail = jsonObject.getString(getString(R.string.email_fb))

                                loginViewModel.loginWithFacebook(
                                    name = name,
                                    email = fbEmail,
                                    accessToken = accessToken.token,
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        val parameters = Bundle()
                        parameters.putString(getString(R.string.fields), getString(R.string.id_name_email_gender_birthday))
                        request.parameters = parameters
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        defaultState()
                    }

                    override fun onError(error: FacebookException?) {
                        Timber.d(error)
                        defaultState()
                    }
                }
            )
        }
    }


    private fun initializeFacebookLoginFB() {
        binding.fbLoginButtonFB.setOnClickListener {
            loginManagerFB = LoginManager.getInstance()
            loginManagerFB.logInWithReadPermissions(
                this,
                listOf(FB_EMAIL_READ_PERMISSION, FB_PROFILE_READ_PERMISSION)
            )
            // Initialize Facebook Login button
            LoginManager.getInstance().registerCallback(callbackManagerFB,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Timber.d(String.format(getString(R.string.on_success)+"$loginResult"))

                        loginResult.let {
                            val token = it.accessToken
                            Timber.d(String.format(getString(R.string.access_token)+"${it.accessToken}"))
                            handleFacebookAccessToken(token)

                        }
                    }
                    override fun onCancel() {
                        Timber.d(getString(R.string.on_cancel))
                    }

                    override fun onError(error: FacebookException) {
                        Timber.d(String.format(getString(R.string.on_error), error))

                        if (error is FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut()
                                Timber.d(String.format(getString(R.string.get_instance), error))

                            }
                        }
                    }
                })

        }
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth = Firebase.auth
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val userTask = task.result.user
                    val userEmail = task.result.credential
                    println(" user $user login firebase") //user com.google.firebase.auth.internal.zzx@b08f5 login firebase
                    if (user != null) {
                        println(" user.email ${user.email} email firebase") //user.email fernanda.goncalves@iesjoandaustria.org email firebase  --debe enviar dato
                        println(" user.displayName ${user.displayName} displayName firebase") //user.displayName Haztucoctel Haztucoctel displayName firebase  --debe mostrar y enviar dato
                        println(" user.photoUrl ${user.photoUrl} photoUrl firebase") // user.photoUrl https://graph.facebook.com/1404543886596239/picture photoUrl firebase ?¿
                        println(" user.phoneNumber ${user.phoneNumber} phoneNumber firebase") //user.phoneNumber null phoneNumber firebase
                    }
                    if (userTask != null) {
                        println(" userTask.phoneNumber ${userTask.phoneNumber} phoneNumber firebase") //userTask.phoneNumber null phoneNumber firebase
                        println(" userTask.email ${userTask.email} email firebase") //userTask.email fernanda.goncalves@iesjoandaustria.org email firebase
                        println(" userTask.photoUrl ${userTask.photoUrl} photoUrl firebase") //userTask.photoUrl https://graph.facebook.com/1404543886596239/picture photoUrl firebase
                        println(" userTask.displayName ${userTask.displayName} displayName firebase") //userTask.displayName Haztucoctel Haztucoctel displayName firebase
                    }
                    if (userEmail != null) {
                        println(" userEmail $userEmail login firebase") //userEmail com.google.firebase.auth.zze@3eba68a login firebase
                        println(" userEmail.provider ${userEmail.provider} provider firebase") //userEmail.provider facebook.com provider firebase
                        println(" task.result.additionalUserInfo ${task.result.additionalUserInfo} additionalUserInfo firebase") //task.result.additionalUserInfo com.google.firebase.auth.internal.zzp@d024fb additionalUserInfo firebase
                        println(" task.result.user?.displayName ${task.result.user?.displayName} displayName firebase") //task.result.user?.displayName Haztucoctel Haztucoctel displayName firebase
                        println(" token.token ${token.token} token firebase") //token.token EAACSZAfnSRfcBAMB882MNA7oWvVZBqMPEsiLCmUVFZBdeLhpSBQn4Os9r0QyiGLjDUvXeisHwGJ5e8gaGRraOXZAwiLYEndD6ZAdVd0K5kpUlgczXZAnnvxsopQcRdd08bX0j41vQOuZC2sumZBHXEMyHsmyFBUJ9buZAd30SlHPmTghzNgZBsS0KCZCOFeLKJKeJlbOHPUP3ZCelxE1R49IRqaM0tzZBZCwj9YEDH6Suwr1aj5wZDZD token firebase
                    }
                    //Login in Miscota
                    val request = GraphRequest.newMeRequest(
                        token
                    ) { jsonObject, response ->
                        try {
                                val name = jsonObject.getString("name")
                                val fbEmail = jsonObject.getString("email")
                                    loginViewModel.loginWithFacebook(
                                        name = name,
                                        email = fbEmail,
                                        accessToken = token.token,
                                    )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender, birthday")
                    request.parameters = parameters
                    request.executeAsync()

                    //updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Autenticación fallida.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }



     private fun loadingState() {
        binding.usernameLayout.error = null
        binding.username.isEnabled = false
        binding.passwordLayout.error = null
        binding.password.isEnabled = false
        binding.loginButton.text = getString(R.string.signing)
        binding.loginButton.isEnabled = false
        binding.forgotButton.isEnabled = false
        binding.signUpButton.isEnabled = false
        binding.fbLoginButton.isEnabled = false
        binding.googleLoginButton.isEnabled = false
    }

    private fun defaultState() {
        binding.usernameLayout.error = null
        binding.username.isEnabled = true
        binding.passwordLayout.error = null
        binding.password.isEnabled = true
        binding.loginButton.text = getString(R.string.sign_in_bottom)
        binding.loginButton.isEnabled = true
        binding.forgotButton.isEnabled = true
        binding.signUpButton.isEnabled = true
        binding.fbLoginButton.isEnabled = true
        binding.googleLoginButton.isEnabled = true
    }

    companion object {
        const val FB_EMAIL_READ_PERMISSION = "email"
        const val FB_PROFILE_READ_PERMISSION = "public_profile"
    }

    private fun recordScreenViewLogin() {
        // This string must be <= 36 characters long.
        val screenName = "screen_login"

        firebaseAnalytics.logEvent(screenName) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "LoginFragment")
            param(FirebaseAnalytics.Param.METHOD, "recordScreenViewLogin")
        }
    }

    private fun recordLoginWithEmailClick() {
        val screenName = "screen_login_loginwithemail_click"

        firebaseAnalytics.logEvent(screenName) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "LoginFragment")
            param(FirebaseAnalytics.Param.METHOD, "loginButton")
        }
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

}
