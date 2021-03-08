package com.abstraksi.desktopwallpaper.domain.repository

import com.abstraksi.desktopwallpaper.data.remote.response.AdsJsonResponse
import io.reactivex.Single

interface AdsJsonRepository {

    fun getAdsJson(url: String): Single<AdsJsonResponse>
}