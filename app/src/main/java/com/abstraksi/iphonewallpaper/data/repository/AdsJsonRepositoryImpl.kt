package com.abstraksi.iphonewallpaper.data.repository

import com.abstraksi.iphonewallpaper.data.remote.response.AdsJsonResponse
import com.abstraksi.iphonewallpaper.data.remote.service.ApiService
import com.abstraksi.iphonewallpaper.domain.repository.AdsJsonRepository
import io.reactivex.Single

class AdsJsonRepositoryImpl(private val apiService: ApiService): AdsJsonRepository {

    override fun getAdsJson(url: String): Single<AdsJsonResponse> {
        return apiService.getAdsJson(url)
    }

}