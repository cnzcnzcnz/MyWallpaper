package com.abstraksi.attackontitan.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abstraksi.attackontitan.data.db.dao.FavoriteDao
import com.abstraksi.attackontitan.data.db.dao.PhotoDao
import com.abstraksi.attackontitan.data.db.entity.FavoriteEntity
import com.abstraksi.attackontitan.data.db.entity.PhotoEntity

@Database(
    entities = [PhotoEntity::class, FavoriteEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun photoDao() : PhotoDao
    abstract fun favoriteDao() : FavoriteDao

}