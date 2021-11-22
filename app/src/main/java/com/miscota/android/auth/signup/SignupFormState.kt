package com.miscota.android.auth.signup

/**
 * Data validation state of the login form.
 */
data class SignupFormState(
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val termsAcceptedError: Int? = null,
    val isDataValid: Boolean = false
)
