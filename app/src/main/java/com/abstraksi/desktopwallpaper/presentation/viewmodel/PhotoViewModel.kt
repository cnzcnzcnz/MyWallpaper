package com.abstraksi.desktopwallpaper.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e
import com.abstraksi.desktopwallpaper.config.INIT_JUMLAH_PHOTO_PER_ALBUM
import com.abstraksi.desktopwallpaper.config.JUMLAH_PHOTO_PER_ALBUM
import com.abstraksi.desktopwallpaper.config.USERNAME
import com.abstraksi.desktopwallpaper.domain.Photo
import com.abstraksi.desktopwallpaper.domain.repository.PhotoRepository
import com.abstraksi.desktopwallpaper.presentation.base.BaseViewModel
import com.abstraksi.desktopwallpaper.presentation.state.UiState
import com.abstraksi.desktopwallpaper.utils.rx.SchedulerProvider
import com.abstraksi.desktopwallpaper.utils.rx.with
import kotlin.math.ceil

class PhotoViewModel(
        private val photoRepository: PhotoRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val photos = MutableLiveData<UiState<List<Photo>>>()
    val favPhotos = MutableLiveData<UiState<List<Photo>>>()

    private fun getPhoto(album: String) {
//        photos.value = UiState.Loading()
        compositeDisposable.add(photoRepository.getPhoto(USERNAME, album.toLowerCase())
                .with(schedulerProvider)
                .subscribe({
//                    photos.value = UiState.Success(it)
                }, this::onError)
        )
    }

    fun getPhotoLokal(album: String) {
        photos.value = UiState.Loading()
        compositeDisposable.add(photoRepository.getPhotoFromLocal(album.toLowerCase())
                .with(schedulerProvider)
                .subscribe({
                    photos.value = UiState.Success(it)
                    if (it.isEmpty()) {
                        getPhoto(album + (it.size + 1).toString())
                    } else {
                        if (it.size < INIT_JUMLAH_PHOTO_PER_ALBUM){
                            val albumCount = ceil((it.size / JUMLAH_PHOTO_PER_ALBUM).toDouble()).toInt() + 1
                            getPhoto(album + albumCount).toString()
                        }
                    }
                }, this::onError)
        )
    }

    fun findFavPhotos() {
        compositeDisposable.add(photoRepository.findFavPhotos()
                .with(schedulerProvider)
                .subscribe({
                    favPhotos.value = UiState.Success(it)
                }, {
                    favPhotos.value = UiState.Error(it)
                }))
    }

    fun findFavById(photoId: String) {
        compositeDisposable.add(photoRepository.findFavById(photoId)
                .with(schedulerProvider)
                .subscribe({
                    favPhotos.value = UiState.Success(it)
                }, {
                    favPhotos.value = UiState.Error(it)
                }))
    }

    fun insertFav(photo: Photo, callbackSuccess: () -> Unit){
        compositeDisposable.add(photoRepository.insertToFav(photo)
                .with(schedulerProvider)
                .subscribe({
                    d { "insert to fav success" }
                    callbackSuccess()
                },{
                    e(it)
                }))
    }

    fun deleteFav(photo: Photo, callbackSuccess: () -> Unit){
        compositeDisposable.add(photoRepository.deleteFav(photo)
                .with(schedulerProvider)
                .subscribe({
                    d { "delete fav success" }
                    callbackSuccess()
                },{
                    e(it)
                }))
    }


    override fun onError(error: Throwable) {
        e { error.localizedMessage }
    }

}