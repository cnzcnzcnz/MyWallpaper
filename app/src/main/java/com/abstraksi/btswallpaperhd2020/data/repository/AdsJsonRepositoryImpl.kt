package com.abstraksi.btswallpaperhd2020.data.repository

import com.abstraksi.btswallpaperhd2020.data.remote.response.AdsJsonResponse
import com.abstraksi.btswallpaperhd2020.data.remote.service.ApiService
import com.abstraksi.btswallpaperhd2020.domain.repository.AdsJsonRepository
import io.reactivex.Single

class AdsJsonRepositoryImpl(private val apiService: ApiService): AdsJsonRepository {

    override fun getAdsJson(url: String): Single<AdsJsonResponse> {
        return apiService.getAdsJson(url)
    }

}