package com.abstraksi.aesthetic.domain.repository

import com.abstraksi.aesthetic.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}