package com.example.accountkithms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val idToken = intent.getStringExtra("idToken")
        val txt = findViewById<TextView>(R.id.txtIdToken)

        if (idToken != null) {

            txt.text = idToken.toString()
        } else {
            // idToken null ise işleme devam edin veya hata işleme yapın
            Toast.makeText(this,"id token:" + idToken,Toast.LENGTH_SHORT).show()
        }
    }
}