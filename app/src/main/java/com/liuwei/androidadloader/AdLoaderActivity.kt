package com.liuwei.androidadloader

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.liuwei.androidadloader.ad.*
import org.jetbrains.anko.*

/**
 * Created by liuwei on 2017/7/26.
 */
class AdLoaderActivity : AppCompatActivity(), AnkoLogger {
    lateinit var view: UI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = UI()
        view.setContentView(this@AdLoaderActivity)

        initView()

        setTitle("Ad Detail")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun initView() {
        val ad = intent.extras.getParcelable<Ad>("ad") ?: return
        val isTest = intent.extras.getBoolean("isTest")
        addMsg(ad.body)
        addMsg("type = ${ad.type} , size = ${ad.size}")

        val adLoader = when (ad.type) {
            Ad.Type.DFP_BANNER -> DfpBannerAdLoader(this, ad)
            Ad.Type.DFP_NATIVE -> DfpNativeAdLoader(this, ad)
            Ad.Type.ADMOB_BANNER -> AdmobAdLoader(this, ad)
            Ad.Type.FB -> FacebookAdLoader(this, ad)
            Ad.Type.FLURRY_NATIVE -> FlurryAdLoader(this, ad)
            else -> DfpBannerAdLoader(this, ad)
        }
        adLoader.isTestAd = true
        adLoader.loadAd(object : AdLoader.IAdListener {
            override fun onStart(adView: View) {
                view.adContainer.addView(adView)
            }

            override fun onLoaded() {
                addMsg("Ad loaded success")
            }

            override fun onOpened() {
                addMsg("Ad opened")
            }

            override fun onError(errorCode: Int, errorMsg: String) {
                addMsg("Ad loaded error. errorCode=$errorCode. $errorMsg")

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun addMsg(msg: String) {
        view.adLoadStatusTv.text = "${view.adLoadStatusTv.text}\n$msg"
    }

    // region ---- UI ----

    inner class UI : AnkoComponent<AdLoaderActivity> {
        lateinit var adLoadStatusTv: TextView
        lateinit var adContainer: ViewGroup

        override fun createView(ui: AnkoContext<AdLoaderActivity>): View {
            return with(ui) {
                verticalLayout {
                    adLoadStatusTv = textView {
                        textSize = dip(5).toFloat()
                    }.lparams {
                        margin = dip(10)
                    }

                    view {
                        backgroundColor = ContextCompat.getColor(context, android.R.color.holo_blue_light)
                    }.lparams {
                        height = dip(0.5f)
                        width = matchParent
                        horizontalMargin = dip(8)
                    }

                    adContainer = frameLayout().lparams {
                        margin = dip(10)
                    }
                }
            }
        }
    }

    // endregion
}
