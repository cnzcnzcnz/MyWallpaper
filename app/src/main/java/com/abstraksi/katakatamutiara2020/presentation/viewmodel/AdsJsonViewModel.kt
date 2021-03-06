package com.abstraksi.katakatamutiara2020.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.abstraksi.katakatamutiara2020.config.ALBUM
import com.abstraksi.katakatamutiara2020.config.AdsUtils
import com.abstraksi.katakatamutiara2020.config.INIT_JUMLAH_PHOTO_PER_ALBUM
import com.abstraksi.katakatamutiara2020.config.USERNAME
import com.abstraksi.katakatamutiara2020.data.remote.response.AdsJsonResponse
import com.abstraksi.katakatamutiara2020.domain.repository.AdsJsonRepository
import com.abstraksi.katakatamutiara2020.presentation.base.BaseViewModel
import com.abstraksi.katakatamutiara2020.presentation.state.UiState
import com.abstraksi.katakatamutiara2020.utils.rx.SchedulerProvider
import com.abstraksi.katakatamutiara2020.utils.rx.with

class AdsJsonViewModel(
        private val adsJsonRepository: AdsJsonRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val adsJson by lazy {
        MutableLiveData<UiState<AdsJsonResponse>>()
    }

    fun getAdsJson() {
        compositeDisposable.add(adsJsonRepository.getAdsJson(AdsUtils.ADS_JSON_URL)
                .with(schedulerProvider)
                .subscribe({
                    adsJson.value = UiState.Success(it)
                    setConfig(it)
                }, {
                    onError(it)
                }))
    }

    override fun onError(error: Throwable) {
        adsJson.value = UiState.Error(error)
    }

    private fun setConfig(config: AdsJsonResponse) {
        config.apply {
            USERNAME = username
            ALBUM = album
            AdsUtils.ADMOB_PUB_ID = pubId
            AdsUtils.ADMOB_APP_ID = appId
            AdsUtils.ADMOB_BANNER_ID = admobBannerId
            AdsUtils.ADMOB_INTERSTITIAL_ID = admobInterstitialId
            AdsUtils.ADMOB_NATIVE_ID = admobNativeId
            AdsUtils.ADMOB_HPK_1 = admobHpk1
            AdsUtils.ADMOB_HPK_2 = admobHpk2
            AdsUtils.ADMOB_HPK_3 = admobHpk3
            AdsUtils.ADMOB_HPK_4 = admobHpk4
            AdsUtils.ADMOB_HPK_5 = admobHpk5
            AdsUtils.FAN_BANNER_ID = fanBannerId
            AdsUtils.FAN_INTERSTITIAL_ID = fanInterstitialId
            AdsUtils.FAN_NATIVE_ID = fanNativeId
            AdsUtils.INTERSTITIAL_INTERVAL = interstitialInverval
            AdsUtils.ADS_PRIMARY_SELECTED = primaryAds

            if (initJumlahPhoto != 0) {
                initJumlahPhoto?.let { INIT_JUMLAH_PHOTO_PER_ALBUM = it }

            }
        }
    }

}