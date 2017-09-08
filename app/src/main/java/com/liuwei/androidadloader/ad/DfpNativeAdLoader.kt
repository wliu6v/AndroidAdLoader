package com.liuwei.androidadloader.ad

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.NativeContentAdView
import com.liuwei.androidadloader.R
import org.jetbrains.anko.info

/**
 * Created by liuwei on 2017/8/18.
 */
class DfpNativeAdLoader(context: Context, ad: Ad) : AdLoader(context, ad) {

    lateinit var adViewHolder: DfpNativeAdLargeViewHolder

    override fun load() {

        adViewHolder = DfpNativeAdLargeViewHolder(context)

        val adLoader = com.google.android.gms.ads.AdLoader.Builder(context, ad.body).forContentAd {
            info("DfpNativeAdLoader - onContentAdLoaded()")
            onLoaded()

            val adView = adViewHolder.itemView
            adView.setNativeAd(it)

            adView.headlineView = adViewHolder.titleTv
            adViewHolder.titleTv.text = it.headline

            adView.bodyView = adViewHolder.contentTv
            adViewHolder.contentTv.text = it.body

            adView.callToActionView = adViewHolder.installBtn
            adViewHolder.installBtn.text = it.callToAction

            adView.imageView = adViewHolder.imgView
            Glide.with(context).load(it.images[0].uri.toString()).into(adViewHolder.imgView)

        }.withAdListener(object : AdListener() {
            override fun onAdImpression() {
                info("DfpNativeAdLoader - onAdImpression()")
            }

            override fun onAdLeftApplication() {
                info("DfpNativeAdLoader - onAdLeftApplication()")
            }

            override fun onAdClicked() {
                info("DfpNativeAdLoader - onAdClicked()")
                onOpened()
            }

            override fun onAdFailedToLoad(p0: Int) {
                info("DfpNativeAdLoader - onAdFailedToLoad() - $p0")
                onError(p0)
            }

            override fun onAdClosed() {
                info("DfpNativeAdLoader - onAdClosed()")
            }

            override fun onAdOpened() {
                info("DfpNativeAdLoader - onAdOpened()")
            }

            override fun onAdLoaded() {
                info("DfpNativeAdLoader - onAdLoaded()")
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().setReturnUrlsForImageAssets(true).build()).build()

        val adBuilder = PublisherAdRequest.Builder()
        val testDeviceId = getTestDeviceId()
        testDeviceId?.let {
            adBuilder.addTestDevice(testDeviceId)
        }
        onStart(adViewHolder.itemView)
        adLoader.loadAd(adBuilder.build())
        saveTestDevice()
    }

    inner class DfpNativeAdLargeViewHolder(context: Context) {
        lateinit var itemView: NativeContentAdView
        lateinit var imgView: ImageView
        lateinit var installBtn: TextView
        lateinit var titleTv: TextView
        lateinit var contentTv: TextView
        lateinit var videoContainer: ViewGroup

        init {
            itemView = LayoutInflater.from(context).inflate(R.layout.dfp_large_ad_view, null) as NativeContentAdView
            imgView = itemView.findViewById(R.id.ad_img)
            installBtn = itemView.findViewById(R.id.ad_action_btn)
            titleTv = itemView.findViewById(R.id.ad_title)
            contentTv = itemView.findViewById(R.id.ad_description)
            videoContainer = itemView.findViewById(R.id.ad_video_container)
        }
    }
}