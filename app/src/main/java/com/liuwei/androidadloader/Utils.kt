package com.liuwei.androidadloader

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by liuwei on 2017/8/20.
 */
fun Context.prefs() = PreferenceManager.getDefaultSharedPreferences(this)