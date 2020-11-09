package com.abstraksi.kimetsunoyaiba.data.repository

import com.abstraksi.kimetsunoyaiba.data.db.dao.FavoriteDao
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import com.abstraksi.kimetsunoyaiba.data.db.dao.PhotoDao
import com.abstraksi.kimetsunoyaiba.data.db.entity.PhotoEntity
import com.abstraksi.kimetsunoyaiba.data.mapper.PhotoMapper
import com.abstraksi.kimetsunoyaiba.data.remote.service.ApiService
import com.abstraksi.kimetsunoyaiba.domain.Photo
import com.abstraksi.kimetsunoyaiba.domain.repository.PhotoRepository
import io.reactivex.Single

class PhotoRepositoryImpl(
        private val apiService: ApiService,
        private val photoDao: PhotoDao,
        private val favoriteDao: FavoriteDao
) : PhotoRepository {

    override fun getPhoto(username: String, album: String): Observable<List<Photo>> {
        return apiService.getPhoto(username, album.replace(" ".toRegex(), "-")).map {
            it.data.pins.map { pin ->
               PhotoMapper.mapToDomain(it.data.board.description, pin, album)
            }
        }.doOnNext {
            if (it.isNotEmpty()) photoDao.insert(it.map { PhotoMapper.mapToData(it) })
        }
    }

    override fun getPhotoFromLocal(album: String): Flowable<List<Photo>> {
        return photoDao.findByAlbum(album.replace(" ".toRegex(), "-")).map { list ->
            list.map { PhotoMapper.mapToDomain(it)   }
        }
    }

    override fun insertToLokal(data: List<PhotoEntity>): Completable {
        return Completable.fromAction {  }
    }

    override fun getRowCount(album: String): Single<Int> {
        return photoDao.getRowCount(album)
    }

    override fun insertToFav(photo: Photo): Completable {
        return Completable.fromAction { favoriteDao.insert(PhotoMapper.mapToFav(photo))  }
    }

    override fun deleteFav(photo: Photo): Completable {
        return Completable.fromAction { favoriteDao.deleteFav(photoId = photo.photoId)  }
    }

    override fun findFavById(photoId: String): Single<List<Photo>> {
        return favoriteDao.findById(photoId).map { list ->
            list.map { PhotoMapper.mapToDomain(it)   }
        }
    }

    override fun findFavPhotos(): Flowable<List<Photo>> {
        return favoriteDao.findAll().map {
            it.map { fav -> PhotoMapper.mapToDomain(fav) }
        }
    }

}