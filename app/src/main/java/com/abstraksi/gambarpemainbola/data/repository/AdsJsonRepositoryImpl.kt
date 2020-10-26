package com.abstraksi.gambarpemainbola.data.repository

import com.abstraksi.gambarpemainbola.data.remote.response.AdsJsonResponse
import com.abstraksi.gambarpemainbola.data.remote.service.ApiService
import com.abstraksi.gambarpemainbola.domain.repository.AdsJsonRepository
import io.reactivex.Single

class AdsJsonRepositoryImpl(private val apiService: ApiService): AdsJsonRepository {

    override fun getAdsJson(url: String): Single<AdsJsonResponse> {
        return apiService.getAdsJson(url)
    }

}