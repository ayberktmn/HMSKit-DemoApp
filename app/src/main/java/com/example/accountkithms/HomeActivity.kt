package com.example.accountkithms


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.accountkithms.databinding.ActivityHomeBinding
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        HwAds.init(this)


        // Set the ad unit ID and ad dimensions. "testw6vs28auh3" is a dedicated test ad unit ID.
        binding.hwBannerView.adId = "testw6vs28auh3"
        binding.hwBannerView.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
        // Set the refresh interval to 60 seconds.
        binding.hwBannerView.setBannerRefresh(60)
        // Create an ad request to load an ad.
        val adParam = AdParam.Builder().build()
        binding.hwBannerView.loadAd(adParam)



        // Set the ad unit ID and ad dimensions. "testw6vs28auh3" is a dedicated test ad unit ID.
        binding.hwBannerView2.adId = "testw6vs28auh3"
        binding.hwBannerView2.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
        // Set the refresh interval to 60 seconds.
        binding.hwBannerView2.setBannerRefresh(60)
        // Create an ad request to load an ad.
        val adParam1 = AdParam.Builder().build()
        binding.hwBannerView2.loadAd(adParam1)


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

        binding.btnLocation.setOnClickListener {
            locationIntent()
        }
    }

    fun locationIntent(){
        val intent = Intent(this, LocationActivity::class.java)
        startActivity(intent)
    }
}