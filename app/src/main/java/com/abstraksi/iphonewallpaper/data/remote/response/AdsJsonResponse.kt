package com.abstraksi.iphonewallpaper.data.remote.response
import com.google.gson.annotations.SerializedName


data class AdsJsonResponse(
    @SerializedName("username")
    val username: String = "",
    @SerializedName("album")
    val album: List<String> = listOf(),
    @SerializedName("init_jumlah_photo")
    val initJumlahPhoto: Int? = 0,
    @SerializedName("pub_id")
    val pubId: String = "",
    @SerializedName("app_id")
    val appId: String = "",
    @SerializedName("admob_banner_id")
    val admobBannerId: String = "",
    @SerializedName("admob_interstitial_id")
    val admobInterstitialId: String = "",
    @SerializedName("admob_native_id")
    val admobNativeId: String = "",
    @SerializedName("admob_hpk_1")
    val admobHpk1: String = "",
    @SerializedName("admob_hpk_2")
    val admobHpk2: String = "",
    @SerializedName("admob_hpk_3")
    val admobHpk3: String = "",
    @SerializedName("admob_hpk_4")
    val admobHpk4: String = "",
    @SerializedName("admob_hpk_5")
    val admobHpk5: String = "",
    @SerializedName("fan_banner_id")
    val fanBannerId: String = "",
    @SerializedName("fan_interstitial_id")
    val fanInterstitialId: String = "",
    @SerializedName("fan_native_id")
    val fanNativeId: String = "",
    @SerializedName("interstitial_inverval")
    val interstitialInverval: Int = 0,
    @SerializedName("primary_ads")
    val primaryAds: String = ""
)