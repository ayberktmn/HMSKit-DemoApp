package com.example.accountkithms

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.service.AccountAuthService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams()
        val service : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authParams)

        val btn = findViewById<View>(R.id.huaweiIdAuthButton)
        btn.setOnClickListener {
            startActivityForResult(service.signInIntent, 8888)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Process the authorization result and obtain an ID to**AuthAccount**thAccount.
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 8888) {
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful) {
                // The sign-in is successful, and the user's ID information and ID token are obtained.
                val authAccount = authAccountTask.result
                Log.i(ContentValues.TAG, "idToken:" + authAccount.idToken)
                Toast.makeText(this,"tokenid:"+ authAccount.idToken, Toast.LENGTH_LONG).show()
                println("calisiyor")
            } else {
                // The sign-in failed. No processing is required. Logs are recorded for fault locating.
                Log.e(ContentValues.TAG, "sign in failed : " + (authAccountTask.exception as ApiException).statusCode)
                println("calismadi")
                Toast.makeText(this,"calismayan id:", Toast.LENGTH_LONG).show()
            }
        }
    }
}