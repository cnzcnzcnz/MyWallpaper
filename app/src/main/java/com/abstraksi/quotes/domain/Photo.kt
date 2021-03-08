package com.abstraksi.quotes.domain

import android.os.Parcelable
import com.abstraksi.quotes.config.ALBUM
import com.abstraksi.quotes.data.remote.response.Pin
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
        val photoId: String = "",
        val album: String = "",
        val created: String = "",
        val path: String = "",
        val thumbnail: String = ""
) : Parcelable {
    companion object {
        fun mapToDomain(pin: Pin) = Photo(
                pin.id,
                ALBUM[0],
                pin.description,
                pin.images.x564.url.replace("564x", "1200x"),
                pin.images.x564.url.replace("564x", "474x")
        )
    }
}