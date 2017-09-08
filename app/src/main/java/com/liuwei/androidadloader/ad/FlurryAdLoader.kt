package com.liuwei.androidadloader.ad

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.flurry.android.ads.FlurryAdErrorType
import com.flurry.android.ads.FlurryAdNative
import com.flurry.android.ads.FlurryAdNativeListener
import com.flurry.android.ads.FlurryAdTargeting
import com.liuwei.androidadloader.Constants
import com.liuwei.androidadloader.R
import org.jetbrains.anko.info

/**
 * Created by liuwei on 2017/8/18.
 */
class FlurryAdLoader(context: Context, ad: Ad) : AdLoader(context, ad) {
    lateinit var adViewHolder: FlurryAdLargeViewHolder

    override fun load() {
        Constants.FLURRY_DEV_KEY ?: return

        adViewHolder = FlurryAdLargeViewHolder(context)

        val adNative = FlurryAdNative(context, ad.body)
        adNative.setListener(object : FlurryAdNativeListener {
            override fun onImpressionLogged(p0: FlurryAdNative?) {
                info("FlurryAdLoader - onImpressionLogged()")
            }

            override fun onShowFullscreen(p0: FlurryAdNative?) {
                info("FlurryAdLoader - onShowFullscreen()")
            }

            override fun onAppExit(p0: FlurryAdNative?) {
                info("FlurryAdLoader - onAppExit()")
            }

            override fun onClicked(p0: FlurryAdNative?) {
                info("FlurryAdLoader - onClicked()")
                onOpened()
            }

            override fun onFetched(flurryAdNative: FlurryAdNative?) {
                info("FlurryAdLoader - onFetched()")

                if (flurryAdNative == null) {
                    error("FlurryAdLoader - onFetched() - Should reach here")
                    return
                }

                flurryAdNative.setTrackingView(adViewHolder.itemView)

                adViewHolder.titleTv.text = flurryAdNative.getAsset("headline")?.value
                adViewHolder.contentTv.text = flurryAdNative.getAsset("summary")?.value
                adViewHolder.installBtn.text = flurryAdNative.getAsset("callToAction")?.value

                adViewHolder.videoContainer.visibility = View.INVISIBLE
                if (flurryAdNative.isVideoAd) {
                    adViewHolder.imgView.visibility = View.INVISIBLE
                    flurryAdNative.getAsset("videoUrl")?.let {
                        adViewHolder.videoContainer.visibility = View.VISIBLE
                        it.loadAssetIntoView(adViewHolder.videoContainer)
                    }
                } else {
                    adViewHolder.imgView.visibility = View.VISIBLE
                    Glide.with(context)
                            .load(flurryAdNative.getAsset("secHqImage")?.value)
                            .into(adViewHolder.imgView)
                }

                onLoaded()
            }

            override fun onExpanded(p0: FlurryAdNative?) {
                info("FlurryAdLoader - onExpanded()")
            }

            override fun onError(p0: FlurryAdNative?, p1: FlurryAdErrorType?, p2: Int) {
                info("FlurryAdLoader - onError() - $p2")
                onError(p2)
            }

            override fun onCloseFullscreen(p0: FlurryAdNative?) {
                info("FlurryAdLoader - onCloseFullscreen()")
            }

            override fun onCollapsed(p0: FlurryAdNative?) {
                info("FlurryAdLoader - onCollapsed()")
            }
        })

        if (isTestAd) {
            val flurryAdTargeting = FlurryAdTargeting()
            flurryAdTargeting.enableTestAds = true
            adNative.setTargeting(flurryAdTargeting)
        }

        adNative.fetchAd()
        onStart(adViewHolder.itemView)
    }

    inner class FlurryAdLargeViewHolder(context: Context) {
        lateinit var itemView: View
        lateinit var imgView: ImageView
        lateinit var installBtn: TextView
        lateinit var titleTv: TextView
        lateinit var contentTv: TextView
        lateinit var videoContainer: ViewGroup

        init {
            itemView = LayoutInflater.from(context).inflate(R.layout.flurry_large_ad_view, null)
            imgView = itemView.findViewById(R.id.ad_img)
            installBtn = itemView.findViewById(R.id.ad_action_btn)
            titleTv = itemView.findViewById(R.id.ad_title)
            contentTv = itemView.findViewById(R.id.ad_description)
            videoContainer = itemView.findViewById(R.id.ad_video_container)
        }

    }
}