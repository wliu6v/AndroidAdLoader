package com.liuwei.androidadloader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.liuwei.androidadloader.ad.Ad
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onItemSelectedListener

/**
 * Created by liuwei on 2017/7/26.
 */
class MainActivity : AppCompatActivity(), AnkoLogger {

    lateinit var view: UI
    var dbHistoryAdapter: ArrayAdapter<String>? = null
    var adList: List<Ad>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = UI()
        view.setContentView(this@MainActivity)
        initView()
    }

    private fun initView() {

        // init ad size
        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Ad.Size.values())
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.adSizeList.adapter = sizeAdapter

        // init ad type
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Ad.Type.values())
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.adTypeList.adapter = typeAdapter

        // init ad history
        updateAdHistory()

        view.adLoadBtn.onClick {
            val ad: Ad
            if (view.adSelectList.selectedItemPosition > 0) {
                ad = adList!![view.adSelectList.selectedItemPosition - 1]
            } else {
                val body = view.adInput.text.toString().trim()
                val storedAd = AdDbHelper.getInstance(this@MainActivity).getAd(body)
                if (storedAd == null) {
                    val type = Ad.Type.values()[view.adTypeList.selectedItemPosition]
                    val size = Ad.Size.values()[view.adSizeList.selectedItemPosition]
                    ad = Ad(body, type, size)
                } else {
                    ad = storedAd
                    view.adSizeList.setSelection(ad.size.ordinal)
                    view.adTypeList.setSelection(ad.type.ordinal)
                    toast("This one has been tested and successed ${ad.successCount} times")
                }
            }

            startActivity(intentFor<AdLoaderActivity>("ad" to ad))
        }

        view.adSelectList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                if (view.adSelectList.selectedItemPosition > 0) {
                    val selectedAd = adList!![view.adSelectList.selectedItemPosition - 1]
                    view.adInput.setText(selectedAd.body)
                    view.adSizeList.setSelection(selectedAd.size.ordinal)
                    view.adTypeList.setSelection(selectedAd.type.ordinal)
                }
            }
        }
    }

    private fun updateAdHistory() {
        adList = AdDbHelper.getInstance(this).getAll()
        if (adList!!.isEmpty()) {
            view.adSelectTip.visibility = View.GONE
            view.adSelectList.visibility = View.GONE
        } else {
            view.adSelectTip.visibility = View.VISIBLE
            view.adSelectList.visibility = View.VISIBLE

            val storeAdsList = ArrayList<String>(adList!!.map { it.body })
            storeAdsList.add(0, "Select Ad")
            if (dbHistoryAdapter == null) {
                dbHistoryAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, storeAdsList)
                dbHistoryAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.adSelectList.adapter = dbHistoryAdapter
            } else {
                dbHistoryAdapter?.clear()
                dbHistoryAdapter?.addAll(storeAdsList)
            }
        }
    }

    // region ---- UI ----

    inner class UI : AnkoComponent<MainActivity> {
        lateinit var adSelectTip: View
        lateinit var adSelectList: Spinner
        lateinit var adInput: EditText
        lateinit var adTypeList: Spinner
        lateinit var adSizeList: Spinner
        lateinit var adLoadBtn: Button

        override fun createView(ui: AnkoContext<MainActivity>): View {
            return with(ui) {
                verticalLayout {
                    adSelectTip = textView("Ad load history") {
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
