package com.abstraksi.desktopwallpaper.data.repository

import com.abstraksi.desktopwallpaper.data.remote.response.AdsJsonResponse
import com.abstraksi.desktopwallpaper.data.remote.service.ApiService
import com.abstraksi.desktopwallpaper.domain.repository.AdsJsonRepository
import io.reactivex.Single

class AdsJsonRepositoryImpl(private val apiService: ApiService): AdsJsonRepository {

    override fun getAdsJson(url: String): Single<AdsJsonResponse> {
        return apiService.getAdsJson(url)
    }

}