package com.abstraksi.kimetsunoyaiba.domain.repository

import com.abstraksi.kimetsunoyaiba.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}