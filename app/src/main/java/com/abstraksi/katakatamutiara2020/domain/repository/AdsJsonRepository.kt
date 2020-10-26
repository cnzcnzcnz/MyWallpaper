package com.abstraksi.katakatamutiara2020.domain.repository

import com.abstraksi.katakatamutiara2020.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}