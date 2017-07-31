package com.liuwei.androidadloader

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.liuwei.androidadloader.ad.Ad
import org.jetbrains.anko.db.*

/**
 * Created by liuwei on 2017/7/27.
 */
class AdDbHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DB_NAME, null, VERSION) {

    companion object {

        const val DB_NAME = "AdDb"
        const val TABLE_NAME = "ad_history"
        const val VERSION = 1

        const val COLUMN_ID = "id"
        const val COLUMN_BODY = "body"
        const val COLUMN_TYPE = "type"
        const val COLUMN_SIZE = "size"
        const val COLUMN_LAST_USE = "last_use"
        const val COLUMN_SUCCESS_COUNT = "success_count"
        const val COLUMN_FAILURE_COUNT = "failure_count"


        private var instance: AdDbHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): AdDbHelper {
            if (instance == null) {
                instance = AdDbHelper(ctx.applicationContext)
            }

            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(TABLE_NAME, true,
//                COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                COLUMN_ID to SqlType.create("INTEGER PRIMARY KEY AUTOINCREMENT"),
                COLUMN_BODY to TEXT,
                COLUMN_TYPE to TEXT,
                COLUMN_SIZE to TEXT,
                COLUMN_LAST_USE to INTEGER,
                COLUMN_SUCCESS_COUNT to INTEGER,
                COLUMN_FAILURE_COUNT to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(TABLE_NAME)
    }

    val Context.database: AdDbHelper
        get() = AdDbHelper.getInstance(applicationContext)
}


