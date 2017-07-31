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
class Ad(val context: Context,
         val body: String,
         val size: Size,
         val type: Type
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
                save(body)
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

    fun save(adBody: String) {
        val storedAd = getStoredAd(adBody)
        if (storedAd != null) {
            AdDbHelper.getInstance(context).writableDatabase.replace(
                    AdDbHelper.TABLE_NAME,
                    AdDbHelper.COLUMN_BODY to body,
                    AdDbHelper.COLUMN_TYPE to type.toString(),
                    AdDbHelper.COLUMN_SIZE to size.toString(),
                    AdDbHelper.COLUMN_LAST_USE to System.currentTimeMillis(),
                    AdDbHelper.COLUMN_SUCCESS_COUNT to 1 + storedAd[AdDbHelper.COLUMN_SUCCESS_COUNT] as Int,
                    AdDbHelper.COLUMN_FAILURE_COUNT to storedAd[AdDbHelper.COLUMN_FAILURE_COUNT] as Int)
        } else {
            AdDbHelper.getInstance(context).writableDatabase.insert(
                    AdDbHelper.TABLE_NAME,
                    AdDbHelper.COLUMN_BODY to body,
                    AdDbHelper.COLUMN_TYPE to type.toString(),
                    AdDbHelper.COLUMN_SIZE to size.toString(),
                    AdDbHelper.COLUMN_LAST_USE to System.currentTimeMillis(),
                    AdDbHelper.COLUMN_SUCCESS_COUNT to 1,
                    AdDbHelper.COLUMN_FAILURE_COUNT to 0
            )
        }
    }

    fun getStoredAd(adBody: String): Map<String, Any>? {
        return AdDbHelper.getInstance(context).readableDatabase.select(AdDbHelper.TABLE_NAME).
                whereArgs("({${AdDbHelper.COLUMN_BODY}} = {body})", "body" to adBody).parseOpt(
                object : RowParser<Map<String, Any>> {
                    override fun parseRow(columns: Array<Any?>): Map<String, Any> {
                        return HashMap<String, Any>().apply {
                            put(AdDbHelper.COLUMN_BODY, columns[1] as String)
                            put(AdDbHelper.COLUMN_TYPE, Type.valueOf(columns[2] as String))
                            put(AdDbHelper.COLUMN_SIZE, Size.valueOf(columns[3] as String))
                            put(AdDbHelper.COLUMN_LAST_USE, columns[4] as Long)
                            put(AdDbHelper.COLUMN_SUCCESS_COUNT, columns[5] as Long)
                            put(AdDbHelper.COLUMN_FAILURE_COUNT, columns[6] as Long)
                        }
                    }
                })
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