package com.example.accountkithms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.accountkithms.databinding.ActivityHomeBinding
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        HwAds.init(this)


        var bannerView: BannerView? = findViewById(R.id.hw_banner_view)
        // Set the ad unit ID and ad dimensions. "testw6vs28auh3" is a dedicated test ad unit ID.
        bannerView!!.adId = "testw6vs28auh3"
        bannerView!!.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
        // Set the refresh interval to 60 seconds.
        bannerView!!.setBannerRefresh(60)
        // Create an ad request to load an ad.
        val adParam = AdParam.Builder().build()
        bannerView!!.loadAd(adParam)

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