package com.abstraksi.btswallpaperhd2020.domain.repository

import com.abstraksi.btswallpaperhd2020.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}