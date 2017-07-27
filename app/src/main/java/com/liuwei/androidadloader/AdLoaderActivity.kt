package com.liuwei.androidadloader

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.liuwei.androidadloader.ad.Ad
import org.jetbrains.anko.*

/**
 * Created by liuwei on 2017/7/26.
 */

class AdLoaderActivity : AppCompatActivity() {
    lateinit var view: UI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = UI()
        view.setContentView(this@AdLoaderActivity)
        initView()
    }

    fun initView() {
        val body = intent.extras.getString("body")
        val size = intent.extras.getSerializable("size") as Ad.Size
        val type = intent.extras.getSerializable("type") as Ad.Type

        addMsg("$body")
        addMsg("type = $type , size = $size")

        val ad = Ad(this, body, size, type)
        ad.load(object : Ad.IAdListener {
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
