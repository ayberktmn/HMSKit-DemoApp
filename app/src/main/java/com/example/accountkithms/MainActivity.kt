package com.example.accountkithms

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.accountkithms.databinding.ActivityMainBinding
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams()
        val service : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authParams)


        binding.huaweiIdAuthorizationButton.setOnClickListener {
            startActivityForResult(service.signInIntent, 8888)
        }

        binding.accountSilentSignin.setOnClickListener {
            silentsignin()
        }
        binding.accountSignout.setOnClickListener {
            signOut()
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

    fun silentsignin(){
        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authParams)
        val task : Task<AuthAccount> = service.silentSignIn()

        task.addOnSuccessListener { authAccount ->
            // Obtain the user's ID information.
            Toast.makeText(this,"SuccesSilentSignin:"+ authAccount.displayName, Toast.LENGTH_LONG).show()
            Log.i(TAG, "displayName:" + authAccount.displayName)
            // Obtain the **0**D type (0: HU**1**WEI ID; 1: AppTouch ID).
            Log.i(TAG, "accountFlag:" + authAccount.accountFlag);
        }
        task.addOnFailureListener { e ->
            // The sign-in failed. Your app can **getSignInIntent()**nIntent() method to explicitly display the authorization screen.
            if (e is ApiException) {
                Log.i(TAG, "sign failed status:" + e.statusCode)
            }
        }
    }

    fun signOut(){
        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authParams)
        val signOutTask = service.signOut()

        signOutTask.addOnCompleteListener { it ->

            Toast.makeText(this,"signOut complete", Toast.LENGTH_LONG).show()
            Log.i(TAG, "signOut complete")
        }
    }
}