package com.example.accountkithms

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        alertDialogBuilder.setTitle("Onaylama")
        alertDialogBuilder.setMessage("İptal etmek istediğinize emin misiniz?")
        alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
            // İptal işlemi onaylandı, iptal kodunu çağırabilirsiniz
            cancelAuthorization()
        }
        alertDialogBuilder.setNegativeButton("Hayır") { dialog, _ ->
            // İptal işlemi iptal edildi, dialog'u kapatın
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}