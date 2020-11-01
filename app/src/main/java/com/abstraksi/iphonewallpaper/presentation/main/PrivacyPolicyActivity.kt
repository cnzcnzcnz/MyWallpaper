package com.abstraksi.iphonewallpaper.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.abstraksi.iphonewallpaper.R
import com.abstraksi.iphonewallpaper.config.PRIVACY_POLICY_URL
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Privacy Policy"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView.loadUrl(PRIVACY_POLICY_URL)
        webView.webViewClient = MyWebViewClient()
        webView.settings.apply {
            javaScriptEnabled = true
            loadsImagesAutomatically = true
            domStorageEnabled = true
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100) progressBar?.visibility = View.VISIBLE

                if (newProgress == 100) {
                    progressBar?.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            url?.let {
                view?.loadUrl(it)
            }
            return false
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}