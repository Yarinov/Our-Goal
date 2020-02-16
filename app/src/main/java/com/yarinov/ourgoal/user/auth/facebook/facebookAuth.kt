package com.yarinov.ourgoal.user.auth.facebook

import android.content.Intent
import com.facebook.CallbackManager


class facebookAuth {

    var callbackManager: CallbackManager? = null

    private val TAG = "FacebookAuth"

    init {
        callbackManager = CallbackManager.Factory.create()


    }

    protected fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
}