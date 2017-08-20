package com.liuwei.androidadloader.ad

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import org.jetbrains.anko.info
import org.jetbrains.anko.verbose

/**
 * Created by liuwei on 2017/8/18.
 */
class DfpBannerAdLoader(context: Context, ad: Ad) : AdLoader(context, ad) {

    override fun load() {
        val adView = PublisherAdView(context)
        adView.setAdSizes(newAdSize(ad.size))
        adView.adUnitId = ad.body

        val adBuilder = PublisherAdRequest.Builder()
        adView.adListener = object : AdListener() {
            override fun onAdLeftApplication() {
                super.onAdLeftApplication()
                info("DfpBannerAdLoader - onAdLeftApplication()")
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                info("DfpBannerAdLoader - onAdFailedToLoad() - $p0")
                onError(p0)
            }

            override fun onAdClosed() {
                super.onAdClosed()
                info("DfpBannerAdLoader - onAdClosed()")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                info("DfpBannerAdLoader - onAdOpened()")
                onOpened()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                info("DfpBannerAdLoader - onAdLoaded()")
                onLoaded()
            }
        }

        val testDeviceId = getTestDeviceId()
        testDeviceId?.let {
            adBuilder.addTestDevice(testDeviceId)
        }
        onStart(adView)
        adView.loadAd(adBuilder.build())
        saveTestDevice()
    }
}