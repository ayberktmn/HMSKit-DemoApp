package com.example.accountkithms


import MyPushService
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.accountkithms.databinding.ActivityMainBinding
import com.huawei.hmf.tasks.Task
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.HmsMessaging
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

        getToken()
       // MyPushService()


        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams()
        val service : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authParams)

        val authorizationParams : AccountAuthParams =  AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams()
        val serviceAuth : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authorizationParams)

        binding.huaweiIdAuthorizationButton.setOnClickListener {
            startActivityForResult(service.signInIntent, 8888)
        }
        binding.accountSilentSignin.setOnClickListener {
            silentsignin()
        }
        binding.accountSignout.setOnClickListener {
            signOut()
        }
        binding.accountSignInCode.setOnClickListener {
            startActivityForResult(serviceAuth.signInIntent, 8888)
        }
        binding.cancelAuthorization.setOnClickListener {
            showCancelAuthorizationConfirmationDialog()
        }
    }

    private fun getToken() {
        // Create a thread.
        object : Thread() {
            override fun run() {
                try {
                    // Obtain the app ID from the agconnect-services.json file.
                    val appId = "109193679"

                    // Set tokenScope to HCM.
                    val tokenScope = "HCM"
                    val token = HmsInstanceId.getInstance(this@MainActivity).getToken(appId, tokenScope)
                    Log.i(TAG, "get token:$token")
//                    Toast.makeText(this@MainActivity,"Token:",Toast.LENGTH_SHORT).show()

                    // Check whether the token is null.
                    if (!TextUtils.isEmpty(token)) {
                        sendRegTokenToServer(token)
                    }
                } catch (e: ApiException) {
                    Log.e(TAG, "get token failed, $e")
                }
            }
        }.start()
    }
    private fun sendRegTokenToServer(token: String) {
        Log.i(TAG, "sending token to server. token:$token")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Process the authorization result and obtain an ID to**AuthAccount**thAccount.
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 8888) {
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful) {

                val authAccount = authAccountTask.result
           //     Toast.makeText(this,"Id Token:"+ authAccount.idToken, Toast.LENGTH_SHORT).show() // id ile giris yapilinca gelecek id Token
            //    Toast.makeText(this,"serverAuthCode:" + authAccount.authorizationCode, Toast.LENGTH_SHORT).show() // authorizationCode ile giris yapilinca gelecek serverAuthCode

                val intent = Intent(this, HomeActivity::class.java)

                // Intent'e idToken'i ekleyin
                intent.putExtra("idToken", authAccount.idToken)

                intent.putExtra("authorizationCode", authAccount.authorizationCode)

                // HomeActivity'yi başlat
                startActivity(intent)
            } else {

                Toast.makeText(this,"Yanlis id:", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun silentsignin(){
        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authParams)
        val task : Task<AuthAccount> = service.silentSignIn()

        task.addOnSuccessListener { authAccount ->
         //   Toast.makeText(this,"SuccesSilentSignin:"+ authAccount.displayName, Toast.LENGTH_SHORT).show()  // giris yapildiktan sonra eger cikis yapilmadiysa kullanici adiyle girilince gelen mesaj

            val intent = Intent(this, HomeActivity::class.java)

            // Intent'e idToken'i ekleyin
            intent.putExtra("SilentSignin",authAccount.displayName)

            // HomeActivity'yi başlat
            startActivity(intent)
        }
        task.addOnFailureListener { e ->

            if (e is ApiException) {
                Toast.makeText(this,"LOGIN FAILED", Toast.LENGTH_SHORT).show() //eger daha once id veya code ile giris yapilmadiysa giris islemi olmayacagini ileten mesaj

            }
        }
    }

    fun signOut(){
        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authParams)
        val signOutTask = service.signOut()

        signOutTask.addOnCompleteListener { it ->
            Toast.makeText(this,"SignOut Complete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelAuthorization() {
        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service : AccountAuthService = AccountAuthManager.getService(this@MainActivity, authParams)
        val task = service.cancelAuthorization()
        task.addOnSuccessListener {
           Toast.makeText(this,"CancelAuthorization Success", Toast.LENGTH_SHORT).show()

        }
        task.addOnFailureListener { e ->
            Toast.makeText(this,"CancelAuthorization Failed", Toast.LENGTH_SHORT).show()

        }
    }

    private fun showCancelAuthorizationConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Cikis")
        alertDialogBuilder.setMessage("Cikis yapmak istediğinize emin misiniz?")
        alertDialogBuilder.setIcon(R.drawable.huawei)

        val NegativeButtonText = "Hayır"
        val NegativeButtonTextSpannable = SpannableString(NegativeButtonText)
        NegativeButtonTextSpannable.setSpan(
            ForegroundColorSpan(Color.RED), // Kırmızı renk
            0,
            NegativeButtonText.length,
            0
        )

        alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
            // İptal işlemi onaylandı
            cancelAuthorization()
        }

        alertDialogBuilder.setNegativeButton(NegativeButtonTextSpannable) { dialog, _ ->
            // İptal işlemi iptal edildi, dialog'u kapat
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}