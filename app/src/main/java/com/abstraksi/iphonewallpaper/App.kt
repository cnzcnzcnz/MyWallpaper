package com.abstraksi.iphonewallpaper

import android.app.Application
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.github.ajalt.timberkt.Timber
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.abstraksi.iphonewallpaper.di.myAppModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AudienceNetworkAds.initialize(this)
        AdSettings.setTestMode(BuildConfig.DEBUG)
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@App)
            modules(myAppModule)
        }
    }
}