package com.miscota.android.auth.signup

import com.miscota.android.auth.login.ui.LoggedInUserView

/**
 * Authentication result : success (user details) or error message.
 */
data class SignUpResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)
