package com.abstraksi.btswallpaperhd2020.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Flowable
import com.abstraksi.btswallpaperhd2020.data.db.entity.PhotoEntity
import io.reactivex.Single

@Dao
interface PhotoDao : BaseDao<PhotoEntity> {

    @Query("SELECT * FROM photo ")
    fun findAll() : Flowable<List<PhotoEntity>>

    @Query("SELECT * FROM photo WHERE album = :album")
    fun findByAlbum(album: String) : Flowable<List<PhotoEntity>>

    @Query("SELECT COUNT(*) FROM photo WHERE album = :album")
    fun getRowCount(album: String): Single<Int>

}