package com.abstraksi.attackontitan.domain.repository

import com.abstraksi.attackontitan.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}