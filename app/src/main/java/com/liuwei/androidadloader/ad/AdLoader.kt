package com.liuwei.androidadloader.ad

import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import com.liuwei.androidadloader.AdDbHelper
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.db.*
import org.jetbrains.anko.verbose

/**
 * Created by liuwei on 2017/7/26.
 */
class AdLoader(
        val context: Context,
        val ad: Ad
) : AnkoLogger {

    interface IAdListener {
        fun onStart(adView: View)
        fun onLoaded()
        fun onOpened()
        fun onError(errorCode: Int, errorMsg: String = "")
    }

    lateinit var listener: IAdListener

    fun load(listener: IAdListener) {
        this.listener = listener

        when (ad.type) {
            Ad.Type.DFP_BANNER -> loadDfpBannerAd()
        }
    }

    fun loadDfpBannerAd() {
        val adView = PublisherAdView(context)
        adView.setAdSizes(newAdSize(ad.size))
        adView.adUnitId = ad.body

        var adBuilder = PublisherAdRequest.Builder()
        adView.adListener = object : AdListener() {
            override fun onAdLeftApplication() {
                super.onAdLeftApplication()
                verbose("onAdLeftApplication(Ad:45)")
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                verbose("onAdFailedToLoad(Ad:50) - ")
                listener.onError(p0)
                if (ad.successCount > 0) {
                    ad.failureCount = ad.failureCount + 1
                    save(ad)
                }
            }

            override fun onAdClosed() {
                super.onAdClosed()
                verbose("onAdClosed(Ad:56) - ")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                verbose("onAdOpened(Ad:61) - ")
                listener.onOpened()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                verbose("onAdLoaded(Ad:67) - ")
                listener.onLoaded()
                ad.successCount = ad.successCount + 1
                save(ad)
            }
        }
        adView.loadAd(adBuilder.build())
        listener.onStart(adView)
    }

    fun newAdSize(size: Ad.Size): AdSize {
        when (size) {
            Ad.Size.SIZE_320_50 -> return AdSize(320, 50)
            Ad.Size.SIZE_320_100 -> return AdSize(320, 100)
            Ad.Size.SIZE_300_250 -> return AdSize(300, 250)
            else -> return AdSize(320, 50)
        }
    }

    fun save(ad: Ad) {
        ad.lastUse = System.currentTimeMillis()
        AdDbHelper.getInstance(context).save(ad)
    }
}