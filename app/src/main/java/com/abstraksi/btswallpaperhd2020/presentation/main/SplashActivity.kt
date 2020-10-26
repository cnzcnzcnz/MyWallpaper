package com.abstraksi.btswallpaperhd2020.presentation.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import com.facebook.ads.AdSettings
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.abstraksi.btswallpaperhd2020.R
import com.abstraksi.btswallpaperhd2020.config.AdsHelper
import com.abstraksi.btswallpaperhd2020.config.AdsUtils
import com.abstraksi.btswallpaperhd2020.presentation.state.UiState
import com.abstraksi.btswallpaperhd2020.presentation.viewmodel.AdsJsonViewModel
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private val adsJsonViewModel: AdsJsonViewModel by viewModel()

    private val MY_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        MobileAds.initialize(this) { }

        val testDeviceIds = listOf("379B54503710418AEB6F6EE432AD522A")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        AdSettings.addTestDevice("2ce6caf9-d170-4eea-b1d3-4ed5ce7b00a1")

        AdsHelper.loadNative(this)

        adsJsonViewModel.adsJson.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    startActivity<HomeActivity>()
                    finish()
                }
                is UiState.Error -> {
                    startActivity<HomeActivity>()
                    finish()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        checkUpdate()
    }

    private fun checkUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val appUpdateManager = AppUpdateManagerFactory.create(this)
            appUpdateManager
                    .appUpdateInfo
                    .addOnSuccessListener { appUpdateInfo ->
                        if (appUpdateInfo.updateAvailability()
                                == UpdateAvailability.UPDATE_AVAILABLE
                        ) {
                            // If an in-app update is already running, resume the update.
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.IMMEDIATE,
                                    this,
                                    MY_REQUEST_CODE
                            )
                        } else {
                            initProcess()
                        }
                    }.addOnFailureListener {
                        initProcess()
                    }

        } else {
            initProcess()
        }
    }

    private fun initProcess() {
        if (AdsUtils.ADS_JSON_URL.isNotBlank()) {
            adsJsonViewModel.getAdsJson()
        } else {
            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    startActivity<HomeActivity>()
                    finish()
                }, 1500)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                toast("Failed to update application")
            }
            initProcess()
        }
    }
}