package com.example.mapapp

import android.os.Parcel
import android.os.Parcelable

data class Marker(val latit: String?, val lontit: String?, val adress: String?) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(latit)
        writeString(lontit)
        writeString(adress)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Marker> = object : Parcelable.Creator<Marker> {
            override fun createFromParcel(source: Parcel): Marker = Marker(source)
            override fun newArray(size: Int): Array<Marker?> = arrayOfNulls(size)
        }
    }
}