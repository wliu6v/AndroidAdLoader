package com.liuwei.androidadloader.ad

import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.verbose

/**
 * Created by liuwei on 2017/7/26.
 */
class Ad(val context: Context, val body: String, val size: Size, val type: Type) : AnkoLogger {

    interface IAdListener {
        fun onStart(adView: View)
        fun onLoaded()
        fun onOpened()
        fun onError(errorCode: Int, errorMsg: String = "")
    }

    lateinit var listener: IAdListener

    fun load(listener: IAdListener) {
        this.listener = listener

        when (type) {
            Type.DFP_BANNER -> loadDfpBannerAd()
        }
    }

    fun loadDfpBannerAd() {
        val adView = PublisherAdView(context)
        adView.setAdSizes(newAdSize(size))
        adView.adUnitId = body

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
            }
        }
        adView.loadAd(adBuilder.build())
        listener.onStart(adView)
    }

    fun newAdSize(size: Size): AdSize {
        when (size) {
            Size.SIZE_320_50 -> return AdSize(320, 50)
            Size.SIZE_320_100 -> return AdSize(320, 100)
            Size.SIZE_300_250 -> return AdSize(300, 250)
            else -> return AdSize(320, 50)
        }
    }

    enum class Size(val size: String) {
        SIZE_320_50("320x50"),
        SIZE_300_250("300x250"),
        SIZE_320_100("320x100"),
        SIZE_LARGE("large"),
        SIZE_SMALL("small")
    }

    enum class Type(val type: String) {
        DFP_BANNER("DFP Banner"),
        ADMOB_BANNER("Admob Banner"),
        FLURRY_NATIVE("Flurry Native"),
        DFP_NATIVE("DFP Native"),
        DFP_MRAID("DFP Mraid"),
        FB("Facebook Native")
    }
}