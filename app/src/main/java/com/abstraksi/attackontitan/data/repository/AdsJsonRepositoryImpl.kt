package com.abstraksi.attackontitan.data.repository

import com.abstraksi.attackontitan.data.remote.response.AdsJsonResponse
import com.abstraksi.attackontitan.data.remote.service.ApiService
import com.abstraksi.attackontitan.domain.repository.AdsJsonRepository
import io.reactivex.Single

class AdsJsonRepositoryImpl(private val apiService: ApiService): AdsJsonRepository {

    override fun getAdsJson(url: String): Single<AdsJsonResponse> {
        return apiService.getAdsJson(url)
    }

}