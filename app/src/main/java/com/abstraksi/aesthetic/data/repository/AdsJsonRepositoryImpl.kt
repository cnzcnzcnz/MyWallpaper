package com.abstraksi.aesthetic.data.repository

import com.abstraksi.aesthetic.data.remote.response.AdsJsonResponse
import com.abstraksi.aesthetic.data.remote.service.ApiService
import com.abstraksi.aesthetic.domain.repository.AdsJsonRepository
import io.reactivex.Single

class AdsJsonRepositoryImpl(private val apiService: ApiService): AdsJsonRepository {

    override fun getAdsJson(url: String): Single<AdsJsonResponse> {
        return apiService.getAdsJson(url)
    }

}