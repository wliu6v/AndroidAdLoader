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

    override fun load(listener: IAdListener) {
        this.listener = listener

        val adView = PublisherAdView(context)
        adView.setAdSizes(newAdSize(ad.size))
        adView.adUnitId = ad.body

        val adBuilder = PublisherAdRequest.Builder()
        adView.adListener = object : AdListener() {
            override fun onAdLeftApplication() {
                super.onAdLeftApplication()
                info("onAdLeftApplication(Ad:45)")
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                info("onAdFailedToLoad(Ad:50) - ")
                listener.onError(p0)
                if (ad.successCount > 0) {
                    ad.failureCount = ad.failureCount + 1
                    save(ad)
                }
            }

            override fun onAdClosed() {
                super.onAdClosed()
                info("onAdClosed(Ad:56) - ")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                info("onAdOpened(Ad:61) - ")
                listener.onOpened()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                info("onAdLoaded(Ad:67) - ")
                listener.onLoaded()
                ad.successCount = ad.successCount + 1
                save(ad)
            }
        }
        adView.loadAd(adBuilder.build())
        listener.onStart(adView)
    }
}