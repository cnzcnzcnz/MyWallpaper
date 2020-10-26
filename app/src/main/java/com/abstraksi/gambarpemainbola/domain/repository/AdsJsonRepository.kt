package com.abstraksi.gambarpemainbola.domain.repository

import com.abstraksi.gambarpemainbola.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}