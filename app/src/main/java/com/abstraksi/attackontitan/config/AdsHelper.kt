package com.abstraksi.attackontitan.config

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isEmpty
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.ump.*
import com.google.android.ump.ConsentInformation.ConsentType.NON_PERSONALIZED
import com.abstraksi.attackontitan.R
import com.abstraksi.attackontitan.config.AdsUtils.ADMOB
import com.abstraksi.attackontitan.config.AdsUtils.FAN
import com.abstraksi.attackontitan.config.AdsUtils.NATIVE_TEST_AD
import com.abstraksi.attackontitan.data.pref.PreferencesHelper
import com.abstraksi.attackontitan.utils.listener.OnAppItemClickListener
import com.abstraksi.attackontitan.utils.visible

class AdsHelper(private val pref: PreferencesHelper) {

    private var interstitialAdmob: InterstitialAd? = null

    private var consentInformation: ConsentInformation? = null
    private var mConsentForm: ConsentForm? = null
    private var callbackCheckConset: (() -> Unit)? = null

    private var adView: com.facebook.ads.AdView? = null
    private var interstitialAdFb: com.facebook.ads.InterstitialAd? = null

    private var appItemclickedListener: OnAppItemClickListener? = null

    companion object {
        var isIntersShow = false

        val mNativeAds = mutableListOf<UnifiedNativeAd>()
        var nativeAd: NativeAd? = null

        fun loadNative(context: Context) {
            loadNativeAdmob(context)
            loadNativeFan(context)
        }

        private fun loadNativeAdmob(context: Context) {
            val adLoader = AdLoader.Builder(context,
                    if (!BuildConfig.DEBUG) AdsUtils.ADMOB_NATIVE_ID else NATIVE_TEST_AD
            )
                    .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                        // Show the ad.
                        mNativeAds.add(ad)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            // Handle the failure by logging, altering the UI, and so on.

                        }
                    })
                    .withNativeAdOptions(
                            NativeAdOptions.Builder()
                                    // Methods in the NativeAdOptions.Builder class can be
                                    // used here to specify individual options settings.
                                    .build())
                    .build()

            adLoader.loadAds(AdRequest.Builder().build(), 3)
        }

        private fun loadNativeFan(context: Context) {
            nativeAd = NativeAd(context, AdsUtils.FAN_NATIVE_ID)
            val nativeAdListener = object : NativeAdListener {
                override fun onAdClicked(p0: Ad?) {
                    d { "fan native clicked " }
                }

                override fun onMediaDownloaded(p0: Ad?) {
                    d { "fan native media downloaded " }
                }

                override fun onError(p0: Ad?, p1: AdError?) {
                    d { "fan native error : ${p1?.errorMessage} " }
                }

                override fun onAdLoaded(p0: Ad?) {
                    if (nativeAd != p0) {
                        return;
                    }
                    // Inflate Native Ad into Container
                    d { "fan native loaded " }

                }

                override fun onLoggingImpression(p0: Ad?) {
                    d { "fan native loggin impression " }
                }

            }
            nativeAd?.loadAd(nativeAd?.buildLoadAdConfig()?.withAdListener(nativeAdListener)?.build())
        }
    }

    fun createBannerAds(adLayout: LinearLayoutCompat) {
        if (AdsUtils.ADS_PRIMARY_SELECTED == AdsUtils.ADMOB) {
            createAdmobBanner(adLayout)
        } else {
            createFanBanner(adLayout)
        }
    }

    private fun createAdmobBanner(adLayout: LinearLayoutCompat) {
        val adView = AdView(adLayout.context)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = if (BuildConfig.DEBUG) AdsUtils.BANNER_TEST_AD else AdsUtils.ADMOB_BANNER_ID

        fun getNonPersonalizedAdsBundle(): Bundle? {
            val extras = Bundle()
            extras.putString("npa", "1")
            return extras
        }

        var adRequest = AdRequest.Builder()
                .addKeyword(AdsUtils.ADMOB_HPK_1)
                .addKeyword(AdsUtils.ADMOB_HPK_2)
                .addKeyword(AdsUtils.ADMOB_HPK_3)
                .addKeyword(AdsUtils.ADMOB_HPK_4)
                .addKeyword(AdsUtils.ADMOB_HPK_5)
                .build()

        val consentType = consentInformation?.consentType
        consentType?.let {
            if (it == NON_PERSONALIZED) {
                adRequest = AdRequest.Builder()
                        .addKeyword(AdsUtils.ADMOB_HPK_1)
                        .addKeyword(AdsUtils.ADMOB_HPK_2)
                        .addKeyword(AdsUtils.ADMOB_HPK_3)
                        .addKeyword(AdsUtils.ADMOB_HPK_4)
                        .addKeyword(AdsUtils.ADMOB_HPK_5)
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, getNonPersonalizedAdsBundle())
                        .build()
            }
        }

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adLayout.visible()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.e { adError.message.toString() }
                Timber.e { adError.code.toString() }
                if (AdsUtils.ADS_PRIMARY_SELECTED == ADMOB) createFanBanner(adLayout)
            }

            override fun onAdOpened() {
                Timber.d { "ads opened" }
            }

            override fun onAdClicked() {
                Timber.d { "ads clicked" }
            }

            override fun onAdLeftApplication() {
                Timber.d { "user left app" }
            }

            override fun onAdClosed() {
                Timber.d { "ads closed" }
            }

        }

        adView.loadAd(adRequest)

        // Place the ad view.
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        adLayout.addView(adView, params)
    }

    fun generateInterstitialAds(context: Context) {
        generateInterstitialAdmobAd(context)
        generateInterstitialFbAds(context)
    }

    private fun generateInterstitialAdmobAd(context: Context) {
        interstitialAdmob = InterstitialAd(context)
        interstitialAdmob?.adUnitId = if (BuildConfig.DEBUG) AdsUtils.INTERSTITIAL_TEST_AD else AdsUtils.ADMOB_INTERSTITIAL_ID
        val adRequestIn = AdRequest.Builder()
                .addKeyword(AdsUtils.ADMOB_HPK_1)
                .addKeyword(AdsUtils.ADMOB_HPK_2)
                .addKeyword(AdsUtils.ADMOB_HPK_3)
                .addKeyword(AdsUtils.ADMOB_HPK_4)
                .addKeyword(AdsUtils.ADMOB_HPK_5)
                .build()

        interstitialAdmob?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Timber.d { "admob inters ad loaded" }
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Timber.e { "admob failed to load inters ad : $errorCode" }
            }

            override fun onAdClosed() {
                Timber.d { "inters ad closed" }
                isIntersShow = false
                appItemclickedListener?.onItemClicked()
                interstitialAdmob?.loadAd(adRequestIn)
            }
        }

        interstitialAdmob?.loadAd(adRequestIn)
    }

    private fun isDisplayInterAds(): Boolean {
        return pref.getInt(PreferencesHelper.PAGE_CLICKED_COUNT) % AdsUtils.INTERSTITIAL_INTERVAL == 0
    }

    fun showInterstitial(listener: OnAppItemClickListener) {
        appItemclickedListener = listener
        if (isDisplayInterAds()) {
            pref.saveInt(
                    PreferencesHelper.PAGE_CLICKED_COUNT, pref.getInt(
                    PreferencesHelper.PAGE_CLICKED_COUNT) + 1)
            if (AdsUtils.ADS_PRIMARY_SELECTED == AdsUtils.ADMOB) {
                showInterAdmob(listener)
            } else {
                showInterFan(listener)
            }

        } else {
            listener.onItemClicked()
            pref.saveInt(
                    PreferencesHelper.PAGE_CLICKED_COUNT,
                    pref.getInt(PreferencesHelper.PAGE_CLICKED_COUNT) + 1
            )

        }
    }

    private fun showInterAdmob(listener: OnAppItemClickListener) {
        interstitialAdmob?.let {
            if (it.isLoaded) {
                isIntersShow = true
                it.show()
            } else {
                showInterFan(listener)
            }
        }
    }

    private fun showInterFan(listener: OnAppItemClickListener) {
        // Check if interstitialAd has been loaded successfully
        interstitialAdFb?.let {
            if (!it.isAdLoaded) {
                Timber.d { "fb inter is not loaded" }
                interstitialAdFb?.loadAd()
                interstitialAdmob?.let { admobInter ->
                    if (admobInter.isLoaded) {
                        isIntersShow = true
                        admobInter.show()
                    } else {
                        listener.onItemClicked()
                    }
                }
                return
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if (it.isAdInvalidated) {
                Timber.d { "fb inter is not invalid" }
                interstitialAdmob?.let { admobInter ->
                    if (admobInter.isLoaded) {
                        isIntersShow = true
                        admobInter.show()
                    } else {
                        listener.onItemClicked()
                    }
                }
                return
            }
            // Show the ad
            interstitialAdFb?.show()
        } ?: kotlin.run {
            listener.onItemClicked()
        }
    }


    fun checkGDPR(activity: Activity, callback: () -> Unit) {
        callbackCheckConset = callback
        val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA)
                .addTestDeviceHashedId("379B54503710418AEB6F6EE432AD522A")
                .build()
        val params = ConsentRequestParameters.Builder()
                .setConsentDebugSettings(debugSettings)
                .build()
        consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation?.requestConsentInfoUpdate(
                activity,
                params,
                { // The consent information state was updated.
                    // You are now ready to check if a form is available.
                    if (consentInformation?.isConsentFormAvailable!!) {
                        loadForm(activity)
                    } else {
                        callbackCheckConset
                    }
                },
                { callbackCheckConset })
    }

    private fun loadForm(activity: Activity?) {
        UserMessagingPlatform.loadConsentForm(
                activity,
                { consentForm ->
                    mConsentForm = consentForm
                    if (consentInformation!!.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(
                                activity
                        ) { // Handle dismissal by reloading form.
                            loadForm(activity)
                            if (callbackCheckConset != null) {
                                callbackCheckConset
                            }
                        }
                    }
                }
        ) { // Handle the error
            if (callbackCheckConset != null) {
                callbackCheckConset
            }
        }
    }

    private fun createFanBanner(adLayout: LinearLayoutCompat) {
        adView = com.facebook.ads.AdView(
                adLayout.context,
                AdsUtils.FAN_BANNER_ID,
                com.facebook.ads.AdSize.BANNER_HEIGHT_50
        )

        val adListener = object : com.facebook.ads.AdListener {
            override fun onAdClicked(p0: Ad?) {
                Timber.d { "ad clicked" }
            }

            override fun onError(p0: Ad?, p1: AdError?) {
                Timber.e { "ad error : ${p1?.errorMessage}" }
                if (AdsUtils.ADS_PRIMARY_SELECTED == FAN) createAdmobBanner(adLayout)
            }

            override fun onAdLoaded(p0: Ad?) {
                adLayout.visible()
                Timber.d { "ad loaded" }
            }

            override fun onLoggingImpression(p0: Ad?) {
                Timber.d { "ad logging impression" }
            }

        }

        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        if (adLayout.isEmpty()) adLayout.addView(adView, params)
        adView?.loadAd(adView?.buildLoadAdConfig()?.withAdListener(adListener)?.build())

    }

    private fun generateInterstitialFbAds(context: Context) {
        interstitialAdFb = com.facebook.ads.InterstitialAd(context,
                AdsUtils.FAN_INTERSTITIAL_ID
        )

        val interstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(p0: Ad?) {
                Timber.d { "inters ad displayed" }
                isIntersShow = true
                interstitialAdFb?.loadAd()
            }

            override fun onAdClicked(p0: Ad?) {
                Timber.d { "inters ad clicked" }
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                Timber.d { "inters ad dismissed" }
                isIntersShow = false
                appItemclickedListener?.onItemClicked()
            }

            override fun onError(p0: Ad?, p1: AdError?) {
                Timber.d { "inters ad error : ${p1?.errorMessage}" }
            }

            override fun onAdLoaded(p0: Ad?) {
                Timber.d { "fb inters ad loaded" }
            }

            override fun onLoggingImpression(p0: Ad?) {
                Timber.d { "inters ad loggin impression" }
            }
        }

        interstitialAdFb?.loadAd(interstitialAdFb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)
                ?.build())

    }

    fun populateUnifiedNativeAdViewBig(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        //        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        //        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }

//        if (nativeAd.getPrice() == null) {
//            adView.getPriceView().setVisibility(View.INVISIBLE);
//        } else {
////            adView.getPriceView().setVisibility(View.VISIBLE);
////            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//        }

//        if (nativeAd.getStore() == null) {
//            adView.getStoreView().setVisibility(View.INVISIBLE);
//        } else {
////            adView.getStoreView().setVisibility(View.VISIBLE);
////            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.GONE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
//            videoStatus.setText(String.format(Locale.getDefault(),
//                    "Video status: Ad contains a %.2f:1 video asset.",
//                    vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
//                    refresh.setEnabled(true);
//                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd()
                }
            })
        }
//        else {
//            videoStatus.setText("Video status: Ad does not contain a video asset.");
//            refresh.setEnabled(true);
//        }
    }

    fun populateNativeFan(nativeAd: NativeAd, nativeAdLayout: NativeAdLayout) {
        val adView = LayoutInflater.from(nativeAdLayout.context)
                .inflate(R.layout.layout_fan_native_exit, nativeAdLayout, false) as LinearLayout
        nativeAdLayout.addView(adView)

        // Add the AdOptionsView
        val adChoicesContainer: LinearLayout = adView.findViewById(R.id.ad_choices_container)
        val adOptionsView = AdOptionsView(nativeAdLayout.context, nativeAd, nativeAdLayout)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.

        // Create native UI using the ad metadata.
//        val nativeAdIcon: MediaView = adView.findViewById(R.id.native_ad_icon)
        val nativeAdTitle: TextView = adView.findViewById(R.id.native_ad_title)
        val nativeAdMedia: com.facebook.ads.MediaView = adView.findViewById(R.id.native_ad_media)
        val nativeAdSocialContext: TextView = adView.findViewById(R.id.native_ad_social_context)
        val nativeAdBody: TextView = adView.findViewById(R.id.native_ad_body)
        val sponsoredLabel: TextView = adView.findViewById(R.id.native_ad_sponsored_label)
        val nativeAdCallToAction: Button = adView.findViewById(R.id.native_ad_call_to_action)

        // Set the Text.

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
        nativeAdSocialContext.text = nativeAd.adSocialContext
        nativeAdCallToAction.setVisibility(if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE)
        nativeAdCallToAction.setText(nativeAd.adCallToAction)
        sponsoredLabel.text = nativeAd.sponsoredTranslation

        // Create a list of clickable views

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
//                nativeAdIcon,
                clickableViews
        )
    }

    fun destroyFanAds() {
        adView?.destroy()
        interstitialAdFb?.destroy()
    }

}