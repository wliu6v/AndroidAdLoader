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
    override fun load(listener: IAdListener) {
        this.listener = listener

        adViewHolder = FbAdViewHolder(context)

        val fbNativeAd = NativeAd(context, ad.body)
        fbNativeAd.setAdListener(object : AdListener {
            override fun onAdClicked(p0: com.facebook.ads.Ad?) {
                info { "onAdClicked" }
            }

            override fun onError(p0: com.facebook.ads.Ad?, p1: AdError?) {
                info { "onError : $p1" }
            }

            override fun onAdLoaded(ad: com.facebook.ads.Ad?) {
                info { "onAdLoaded" }
                if (ad == null || ad != fbNativeAd) {
                    return
                }

                fbNativeAd.registerViewForInteraction(adViewHolder.itemView)
                adViewHolder.mediaView.setNativeAd(fbNativeAd)
                adViewHolder.adChoiceView.visibility = View.VISIBLE
                adViewHolder.adChoiceView.addView(AdChoicesView(context, fbNativeAd, true))
            }

            override fun onLoggingImpression(p0: com.facebook.ads.Ad?) {
                info { "onImpression" }
            }
        })
        fbNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL)
        this.listener.onStart(adViewHolder.itemView)
    }

    inner class FbAdViewHolder(context: Context) {
        lateinit var itemView: View
        lateinit var mediaView: MediaView
        lateinit var adChoiceView: ViewGroup

        init {
            itemView = LayoutInflater.from(context).inflate(R.layout.fb_native_ad_view, null)
            mediaView = itemView.findViewById(R.id.ad_media) as MediaView
            adChoiceView = itemView.findViewById(R.id.ad_choice_container) as ViewGroup
        }
    }
}
