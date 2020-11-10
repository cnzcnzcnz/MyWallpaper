package com.abstraksi.attackontitan.presentation.base

import android.app.ProgressDialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.abstraksi.attackontitan.R
import com.abstraksi.attackontitan.config.AdsHelper
import com.abstraksi.attackontitan.utils.listener.OnAppItemClickListener
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {

    private val adsHelper: AdsHelper by inject()

    private lateinit var progressLoadingDialog: ProgressDialog
    private var adLayout: LinearLayoutCompat? = null

    @LayoutRes
    abstract fun getLayoutResId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        progressLoadingDialog = ProgressDialog(this)

        adLayout = findViewById<LinearLayoutCompat>(R.id.adLayout)

        adLayout?.let {
            adsHelper.createBannerAds(it)
            adsHelper.generateInterstitialAds(this)
        }
    }

    fun showLoading(msg: String = getString(R.string.please_wait)) {
        if (!progressLoadingDialog.isShowing) {
            progressLoadingDialog.setMessage(msg)
            progressLoadingDialog.show()
        }
    }

    fun dismissLoading() {
        if (progressLoadingDialog.isShowing) {
            progressLoadingDialog.dismiss()
        }
    }

    fun showInterstitialAd(listener: OnAppItemClickListener) {
        adsHelper.showInterstitial(listener)
    }

    override fun onDestroy() {
        adsHelper.destroyFanAds()
        super.onDestroy()
    }
}