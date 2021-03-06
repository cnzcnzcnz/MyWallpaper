package com.abstraksi.aesthetic.domain.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import com.abstraksi.aesthetic.data.db.entity.PhotoEntity
import com.abstraksi.aesthetic.domain.Photo
import io.reactivex.Single

interface PhotoRepository {

    fun getPhoto(username: String, album: String): Observable<List<Photo>>

    fun getPhotoFromLocal(album: String): Flowable<List<Photo>>

    fun insertToLokal(data: List<PhotoEntity>) : Completable

    fun getRowCount(album: String) : Single<Int>

    fun insertToFav(photo: Photo) : Completable

    fun deleteFav(photo: Photo) : Completable

    fun findFavById(photoId: String): Single<List<Photo>>

    fun findFavPhotos(): Flowable<List<Photo>>

}