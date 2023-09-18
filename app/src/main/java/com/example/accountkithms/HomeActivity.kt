package com.example.accountkithms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.accountkithms.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idToken = intent.getStringExtra("idToken")
        val SilentSignin = intent.getStringExtra("SilentSignin")
        val authorizationCode = intent.getStringExtra("authorizationCode")

        if (idToken != null || authorizationCode != null) {
            binding.txtIdToken.text = idToken.toString()
            binding.txtSilentSign.text = "Silent Singin ile giris yapiniz!"

            if (authorizationCode == null){
                binding.txtAuthorization.text = "AuthorizationCode ile giris yapiniz!"
            } else{
                binding.txtAuthorization.text = authorizationCode.toString()
            }

            if (idToken == null){
                binding.txtIdToken.text = "IdToken ile giris yapiniz!"
            } else{
                binding.txtIdToken.text = idToken.toString()
            }

        } else {
            // idToken null ise işleme devam edin veya hata işleme yapın
            Toast.makeText(this,"id token:" + idToken,Toast.LENGTH_SHORT).show()
        }
        if (SilentSignin != null) {
            binding.txtSilentSign.text = SilentSignin.toString()
            binding.txtAuthorization.text = "AuthorizationCode ile giris yapiniz!"
            binding.txtIdToken.text = "IdToken ile giris yapiniz!"
        } else {
            // idToken null ise işleme devam edin veya hata işleme yapın
            Toast.makeText(this,"Silent Signin:" + SilentSignin,Toast.LENGTH_SHORT).show()
        }
    }
}