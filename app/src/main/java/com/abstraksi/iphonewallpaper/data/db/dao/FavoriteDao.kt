package com.abstraksi.iphonewallpaper.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.abstraksi.iphonewallpaper.data.db.entity.FavoriteEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FavoriteDao : BaseDao<FavoriteEntity> {

    @Query("SELECT * FROM favorite ")
    fun findAll() : Flowable<List<FavoriteEntity>>

    @Query("SELECT * FROM favorite WHERE photo_id = :photoId")
    fun findById(photoId: String) : Single<List<FavoriteEntity>>

    @Query("DELETE FROM favorite WHERE photo_id = :photoId")
    fun deleteFav(photoId: String)

}