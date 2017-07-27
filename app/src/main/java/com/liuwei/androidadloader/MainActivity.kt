package com.liuwei.androidadloader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.liuwei.androidadloader.ad.Ad
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by liuwei on 2017/7/26.
 */
class MainActivity : AppCompatActivity() {

    lateinit var view: UI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = UI()
        view.setContentView(this@MainActivity)
        initView()
    }

    fun initView() {

        // init ad size
        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Ad.Size.values())
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.adSizeList.adapter = sizeAdapter

        // init ad type
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Ad.Type.values())
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.adTypeList.adapter = typeAdapter

        view.adLoadBtn.onClick {
            val body = view.adInput.text.toString().trim()
            val type = Ad.Type.values()[view.adTypeList.selectedItemPosition]
            val size = Ad.Size.values()[view.adSizeList.selectedItemPosition]
            toast("$body , $type , $size")
            startActivity(intentFor<AdLoaderActivity>("body" to body, "type" to type, "size" to size))
        }
    }
    // region ---- UI ----

    inner class UI : AnkoComponent<MainActivity> {
        lateinit var adSelectList: Spinner
        lateinit var adInput: EditText
        lateinit var adTypeList: Spinner
        lateinit var adSizeList: Spinner
        lateinit var adLoadBtn: Button
        lateinit var adLoadStatusTv: TextView
        lateinit var adContainer: ViewGroup

        override fun createView(ui: AnkoContext<MainActivity>): View {
            return with(ui) {
                verticalLayout {
                    textView("Ad load history") {
                        textSize = dip(6).toFloat()
                    }.lparams {
                        margin = dip(10)
                    }

                    adSelectList = spinner().lparams {
                        width = matchParent
                        margin = dip(10)
                    }

                    adInput = editText {
                        hint = "Input your ad id"
                    }.lparams {
                        width = matchParent
                        margin = dip(10)
                    }

                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        textView("Ad type") {
                            textSize = dip(6).toFloat()
                        }.lparams {
                            margin = dip(10)
                        }

                        adTypeList = spinner().lparams {
                            width = matchParent
                            margin = dip(10)
                        }
                    }

                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL

                        textView("Ad size") {
                            textSize = dip(6).toFloat()
                        }.lparams {
                            margin = dip(10)
                        }

                        adSizeList = spinner().lparams {
                            width = matchParent
                            margin = dip(10)
                        }
                    }

                    adLoadBtn = button("Load") {
                        textSize = dip(6).toFloat()
                    }.lparams {
                        width = matchParent
                        verticalMargin = dip(12)
                        horizontalMargin = dip(36)
                    }
                }
            }
        }
    }

    // endregion
}