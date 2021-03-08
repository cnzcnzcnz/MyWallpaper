package com.abstraksi.quotes.data.repository

import com.abstraksi.quotes.data.remote.response.AdsJsonResponse
import com.abstraksi.quotes.data.remote.service.ApiService
import com.abstraksi.quotes.domain.repository.AdsJsonRepository
import io.reactivex.Single

class AdsJsonRepositoryImpl(private val apiService: ApiService): AdsJsonRepository {

    override fun getAdsJson(url: String): Single<AdsJsonResponse> {
        return apiService.getAdsJson(url)
    }

}