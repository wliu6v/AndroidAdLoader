package com.liuwei.androidadloader.ad

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by liuwei on 2017/8/14.
 */
data class Ad(
        val body: String,
        val type: Type,
        val size: Size,
        var lastUse: Long = 0,
        var successCount: Int = 0,
        var failureCount: Int = 0,
        var id: Int = 0
) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            Type.values()[source.readInt()],
            Size.values()[source.readInt()],
            source.readLong(),
            source.readInt(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(body)
        writeInt(type.ordinal)
        writeInt(size.ordinal)
        writeLong(lastUse)
        writeInt(successCount)
        writeInt(failureCount)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Ad> = object : Parcelable.Creator<Ad> {
            override fun createFromParcel(source: Parcel): Ad = Ad(source)
            override fun newArray(size: Int): Array<Ad?> = arrayOfNulls(size)
        }
    }

    enum class Type(val type: String) {
        DFP_BANNER("DFP Banner"),
        ADMOB_BANNER("Admob Banner"),
        FLURRY_NATIVE("Flurry Native"),
        DFP_NATIVE("DFP Native"),
        DFP_MRAID("DFP Mraid"),
        FB("Facebook Native")
    }

    enum class Size(val size: String) {
        SIZE_320_50("320x50"),
        SIZE_300_250("300x250"),
        SIZE_320_100("320x100"),
        SIZE_LARGE("large"),
        SIZE_SMALL("small")
    }
}

