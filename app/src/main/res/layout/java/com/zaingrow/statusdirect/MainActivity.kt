package com.zaingrow.statusdirect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {

    private var interstitialAd: InterstitialAd? = null

    private val bannerAdUnitId = "ca-app-pub-4034797174706820/1825553608"
    private val interstitialAdUnitId = "ca-app-pub-4034797174706820/8564467756"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}

        val bannerContainer = findViewById<FrameLayout>(R.id.bannerContainer)
        val adView = AdView(this).apply {
            adUnitId = bannerAdUnitId
            setAdSize(AdSize.BANNER)
        }
        bannerContainer.addView(adView)
        adView.loadAd(AdRequest.Builder().build())

        loadInterstitialAd()

        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnDirectChat = findViewById<Button>(R.id.btnDirectChat)

        btnDirectChat.setOnClickListener {
            val phone = etPhone.text.toString().replace(Regex("[^0-9]"), "")
            val message = etMessage.text.toString().trim()

            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encodedMsg = Uri.encode(message)
            val url = "https://wa.me/$phone?text=$encodedMsg"

            if (interstitialAd != null) {
                interstitialAd?.show(this)
                loadInterstitialAd()
            }

            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Unable to open WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            interstitialAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }
}
