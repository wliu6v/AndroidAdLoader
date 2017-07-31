package com.liuwei.androidadloader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.liuwei.androidadloader.ad.Ad
import org.jetbrains.anko.*
import org.jetbrains.anko.db.RowParser
import org.jetbrains.anko.db.select
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

        // init ad history
        val storedAds = AdDbHelper.getInstance(this).readableDatabase.select(AdDbHelper.TABLE_NAME).parseList(object : RowParser<Map<String, Any>> {
            override fun parseRow(columns: Array<Any?>): Map<String, Any> {
                return HashMap<String, Any>().apply {
                    put(AdDbHelper.COLUMN_BODY, columns[1] as String)
                    put(AdDbHelper.COLUMN_TYPE, Ad.Type.valueOf(columns[2] as String))
                    put(AdDbHelper.COLUMN_SIZE, Ad.Size.valueOf(columns[3] as String))
                    put(AdDbHelper.COLUMN_LAST_USE, columns[4] as Long)
                    put(AdDbHelper.COLUMN_SUCCESS_COUNT, columns[5] as Long)
                    put(AdDbHelper.COLUMN_FAILURE_COUNT, columns[6] as Long)
                }
            }
        })
        val historyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, storedAds)
        historyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.adSelectList.adapter = historyAdapter

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