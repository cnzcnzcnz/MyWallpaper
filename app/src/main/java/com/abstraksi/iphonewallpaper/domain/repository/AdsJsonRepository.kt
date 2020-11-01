package com.abstraksi.iphonewallpaper.domain.repository

import com.abstraksi.iphonewallpaper.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}