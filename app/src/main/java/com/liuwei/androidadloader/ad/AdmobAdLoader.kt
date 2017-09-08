package com.liuwei.androidadloader.ad

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import org.jetbrains.anko.info

/**
 * Created by liuwei on 2017/8/18.
 */
class AdmobAdLoader(context: Context, ad: Ad) : AdLoader(context, ad) {
    override fun load() {
        val adView = AdView(context)
        adView.adSize = newAdSize(ad.size)
        adView.adUnitId = ad.body
        adView.adListener = object : AdListener() {
            override fun onAdLeftApplication() {
                super.onAdLeftApplication()
                info("AdmobAdLoader - onAdLeftApplication() - ${ad.body}")
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                info("AdmobAdLoader - onAdFailedToLoad() - ${ad.body}")
                onError(p0)
            }

            override fun onAdClosed() {
                super.onAdClosed()
                info("AdmobAdLoader - onAdClosed() - ${ad.body}")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                info("AdmobAdLoader - onAdOpened() - ${ad.body}")
                onOpened()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                info("AdmobAdLoader - onAdLoaded() - ${ad.body}")
                onLoaded()
            }
        }

        val adBuilder = AdRequest.Builder()
        val testDeviceId = getTestDeviceId()
        testDeviceId?.let {
            adBuilder.addTestDevice(testDeviceId)
        }
        adView.loadAd(adBuilder.build())

        onStart(adView)
    }
}