package com.abstraksi.btswallpaperhd2020.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "photo_id") val photoId: String,
        @ColumnInfo(name = "album") val album: String,
        @ColumnInfo(name = "created") val created: String,
        @ColumnInfo(name = "path") val imageUrl: String,
        @ColumnInfo(name = "thumb") val imageThumbUrl: String
)