package com.abstraksi.iphonewallpaper.config

object AdsUtils {

    // Jangan diubah
    const val ADMOB = "ADMOB"
    const val FAN = "FAN"

    // ADMOB PUBLISHER ID
//    var ADMOB_PUB_ID = "pub-2645249444311791"
//    pub-3559020312196470
    var ADMOB_PUB_ID = "pub-2687920864105031"

    // ADMOB APP ID (ubah di res -> values -> strings juga)
    var ADMOB_APP_ID = "ca-app-pub-2687920864105031~2855349198"

    // ADMOB IKLAN ID
    var ADMOB_BANNER_ID = "ca-app-pub-2687920864105031/1153408807"
    var ADMOB_INTERSTITIAL_ID = "ca-app-pub-2687920864105031/8449961812"
    var ADMOB_NATIVE_ID = "ca-app-pub-2687920864105031/8258390129"

    // ADMOB FOR TEST
//    const val BANNER_TEST_AD = "ca-app-pub-3940256099942544/6300978111"
//    const val INTERSTITIAL_TEST_AD = "ca-app-pub-3940256099942544/1033173712"
//    const val NATIVE_TEST_AD = "ca-app-pub-3940256099942544/2247696110"

    const val BANNER_TEST_AD = "ca-app-pub-2687920864105031/1153408807"
    const val INTERSTITIAL_TEST_AD = "ca-app-pub-2687920864105031/8449961812"
    const val NATIVE_TEST_AD = "ca-app-pub-2687920864105031/8258390129"

    // interval interstitial tiap klik wallpaper, logo, save, fullscreen dll
    var INTERSTITIAL_INTERVAL = 6

    //HIGH PAYMENT KEYWORD
    var ADMOB_HPK_1 = "crypto"
    var ADMOB_HPK_2 = "fashion"
    var ADMOB_HPK_3 = "cars"
    var ADMOB_HPK_4 = "mobile"
    var ADMOB_HPK_5 = "insurance"

    // FAN IKLAN ID
    var FAN_BANNER_ID = "YOUR_PLACEMENT_ID"
    var FAN_INTERSTITIAL_ID = "YOUR_PLACEMENT_ID"
    var FAN_NATIVE_ID = "YOUR_PLACEMENT_ID"

    // Pilih iklan utama, ADMOB atau FAN
    // iklan akan auto backup
    var ADS_PRIMARY_SELECTED = ADMOB

    // url ads json online
    // jika mau offline, kosongkan ""
    // jika offline, jangan lupa set username & albums di file Constants
    // dan untuk di file ini harap diisi semua dengan benar, sehingga ketika gagal load ads json online, masih bisa menggunakan config ads offline
//    var ADS_JSON_URL = "https://sites.google.com/site/aibangstudiowallpaper/json/ads_wallpaper.json"
    var ADS_JSON_URL = ""

}