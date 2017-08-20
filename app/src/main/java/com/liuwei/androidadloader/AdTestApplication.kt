package com.liuwei.androidadloader

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.flurry.android.FlurryAgent

/**
 * Created by liuwei on 2017/8/18.
 */
class AdTestApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Constants.FLURRY_DEV_KEY?.let {
            FlurryAgent.Builder().build(this, Constants.FLURRY_DEV_KEY)
        }
    }
}