package com.liuwei.androidadloader.ad

import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdSize
import com.liuwei.androidadloader.AdDbHelper
import com.liuwei.androidadloader.prefs
import org.jetbrains.anko.AnkoLogger
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by liuwei on 2017/7/26.
 */
abstract class AdLoader(
        val context: Context,
        val ad: Ad
) : AnkoLogger {

    lateinit var listener: IAdListener
    var isTestAd = false

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


    // region ---- assist method ----

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

    fun getTestDeviceId() = if (isTestAd)
        context.prefs().getString("TEST_DEVICE_${ad.type.name}", null) else null

    fun saveTestDevice() {
        val commandLine = arrayOf("logcat", "-d", "|", "grep", "\"Ads\"")
        val clearCommand = arrayOf("logcat", "-c")

        val process = Runtime.getRuntime().exec(commandLine)
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream), 1024)

        var line: String
        while (true) {
            line = bufferedReader.readLine() ?: break
            val deviceId = parseTestDeviceId(line, ad.type)
            deviceId?.let {
                context.prefs().edit().putString("TEST_DEVICE_${ad.type.name}", deviceId).apply()
            }
        }

        Runtime.getRuntime().exec(clearCommand)
    }

    private fun parseTestDeviceId(log: String, type: Ad.Type): String? {
        when (type) {
            Ad.Type.FB -> {

                // The log will be "Test mode device hash: 15214d5415214d5415214d54"
                var index = log.indexOf("Test mode device hash: ")
                if (index != -1) {
                    index += 23
                    return log.substring(index, index + 32)
                }

                // The log also maybe "AdSettings.addTestDevice("d155feasef90ab5404c415c44befdbba");"
                index = log.indexOf("AdSettings.addTestDevice")
                if (index != -1) {
                    index += 26
                    return log.substring(index, index + 32)
                }

            }

            Ad.Type.DFP_BANNER, /*Ad.Type.DFP_MRAID,*/ Ad.Type.DFP_NATIVE -> {
                // The log will be : .... addTestDevice("123456789ABCDEF0123456789ABCDEF0") ...
                var index = log.indexOf("addTestDevice")
                if (index != -1) {
                    index += 15
                    return log.substring(index, index + 32)
                }
            }
        }

        return null
    }

    // endregion
}