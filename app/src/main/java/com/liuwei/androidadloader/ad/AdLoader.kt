package com.liuwei.androidadloader.ad

import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdSize
import com.liuwei.androidadloader.AdDbHelper
import org.jetbrains.anko.AnkoLogger

/**
 * Created by liuwei on 2017/7/26.
 */
abstract class AdLoader(
        val context: Context,
        val ad: Ad
) : AnkoLogger {

    lateinit var listener: IAdListener

    protected abstract fun load()

    fun loadAd(listener: IAdListener) {
        this.listener = listener
        load()
    }


    fun onStart(adView: View) {
        listener.onStart(adView)
    }

    fun onLoaded() {
        ad.successCount = ad.successCount + 1
        save(ad)
        listener.onLoaded()
    }

    fun onOpened() {
        listener.onOpened()
    }

    fun onError(errorCode: Int, errorMsg: String = "") {
        if (ad.successCount > 0) {
            ad.failureCount = ad.failureCount + 1
            save(ad)
        }
        listener.onError(errorCode, errorMsg)
    }


    interface IAdListener {
        fun onStart(adView: View)
        fun onLoaded()
        fun onOpened()
        fun onError(errorCode: Int, errorMsg: String = "")
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