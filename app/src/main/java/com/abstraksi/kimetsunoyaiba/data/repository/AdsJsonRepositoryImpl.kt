package com.abstraksi.kimetsunoyaiba.data.repository

import com.abstraksi.kimetsunoyaiba.data.remote.response.AdsJsonResponse
import com.abstraksi.kimetsunoyaiba.data.remote.service.ApiService
import com.abstraksi.kimetsunoyaiba.domain.repository.AdsJsonRepository
import io.reactivex.Single

class AdsJsonRepositoryImpl(private val apiService: ApiService): AdsJsonRepository {

    override fun getAdsJson(url: String): Single<AdsJsonResponse> {
        return apiService.getAdsJson(url)
    }

}