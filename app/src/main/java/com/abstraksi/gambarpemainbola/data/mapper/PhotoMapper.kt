package com.abstraksi.gambarpemainbola.data.mapper

import com.abstraksi.gambarpemainbola.data.db.entity.FavoriteEntity
import com.abstraksi.gambarpemainbola.data.db.entity.PhotoEntity
import com.abstraksi.gambarpemainbola.data.remote.response.Pin
import com.abstraksi.gambarpemainbola.domain.Photo

object PhotoMapper {

    fun mapToDomain(date: String, pin: Pin, album: String) = Photo(
        photoId = pin.id,
        album = album,
        created = date,
        path = if (pin.embed == null) pin.images.x564.url.replace("564x", "1200x") else pin.embed.src,
        thumbnail = if (pin.embed == null) pin.images.x564.url.replace("564x", "474x") else pin.embed.src
    )

    fun mapToDomain(photo: PhotoEntity) = Photo(
        photoId = photo.photoId,
        album = photo.album,
        created = photo.created,
        path = photo.imageUrl,
        thumbnail = photo.imageThumbUrl
    )

    fun mapToDomain(photo: FavoriteEntity) = Photo(
            photoId = photo.photoId,
            album = photo.album,
            created = photo.created,
            path = photo.imageUrl,
            thumbnail = photo.imageThumbUrl
    )

    fun mapToData(photo: Photo) = PhotoEntity(
        photoId = photo.photoId,
        album = photo.album.replace("\\d".toRegex(), "").replace(" ".toRegex(), "-"),
        created = photo.created,
        imageUrl = photo.path,
        imageThumbUrl = photo.thumbnail
    )

    fun mapToFav(photo: Photo) = FavoriteEntity(
            photoId = photo.photoId,
            album = photo.album.replace("\\d".toRegex(), "").replace(" ".toRegex(), "-"),
            created = photo.created,
            imageUrl = photo.path,
            imageThumbUrl = photo.thumbnail
    )

}