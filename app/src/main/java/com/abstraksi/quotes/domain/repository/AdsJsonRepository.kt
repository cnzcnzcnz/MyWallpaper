package com.abstraksi.quotes.domain.repository

import com.abstraksi.quotes.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}