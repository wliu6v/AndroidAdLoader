package com.liuwei.androidadloader.ad

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.ads.*
import com.liuwei.androidadloader.R
import org.jetbrains.anko.info

/**
 * Created by liuwei on 2017/8/18.
 */
class FacebookAdLoader(context: Context, ad: Ad) : AdLoader(context, ad) {
    lateinit var adViewHolder: FbAdViewHolder
    override fun load() {
        adViewHolder = FbAdViewHolder(context)

        val fbNativeAd = NativeAd(context, ad.body)
        fbNativeAd.setAdListener(object : AdListener {
            override fun onAdClicked(p0: com.facebook.ads.Ad?) {
                info("FacebookAdLoader - onAdClicked()")
                onOpened()
            }

            override fun onError(p0: com.facebook.ads.Ad?, p1: AdError?) {
                info("FacebookAdLoader - onError() - ${p1?.errorCode} , ${p1?.errorMessage}")
                onError(p1?.errorCode ?: 0, p1?.errorMessage ?: "")
                saveTestDevice()
            }

            override fun onAdLoaded(p0: com.facebook.ads.Ad?) {
                info("FacebookAdLoader - onAdLoaded()")
                if (p0 == null || p0 != fbNativeAd) {
                    info("FacebookAdLoader - onAdLoaded() - Should not reach here")
                    return
                }

                fbNativeAd.registerViewForInteraction(adViewHolder.itemView)
                adViewHolder.mediaView.setNativeAd(fbNativeAd)
                adViewHolder.adChoiceView.visibility = View.VISIBLE
                adViewHolder.adChoiceView.addView(AdChoicesView(context, fbNativeAd, true))

                onLoaded()
            }

            override fun onLoggingImpression(p0: com.facebook.ads.Ad?) {
                info("FacebookAdLoader - onLoggingImpression()")
            }
        })

        val testDeviceId = getTestDeviceId()
        testDeviceId?.let {
            AdSettings.addTestDevice(testDeviceId)
        }
        onStart(adViewHolder.itemView)
        fbNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL)
        saveTestDevice()
    }

    inner class FbAdViewHolder(context: Context) {
        lateinit var itemView: View
        lateinit var mediaView: MediaView
        lateinit var adChoiceView: ViewGroup

        init {
            itemView = LayoutInflater.from(context).inflate(R.layout.fb_native_ad_view, null)
            mediaView = itemView.findViewById(R.id.ad_media)
            adChoiceView = itemView.findViewById(R.id.ad_choice_container)
        }
    }
}
