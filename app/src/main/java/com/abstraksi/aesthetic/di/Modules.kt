package com.abstraksi.aesthetic.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.abstraksi.aesthetic.data.db.AppDatabase
import com.abstraksi.aesthetic.data.remote.createWebService
import com.abstraksi.aesthetic.data.remote.provideOkHttpClient
import com.abstraksi.aesthetic.data.pref.PreferencesHelper
import com.abstraksi.aesthetic.data.remote.service.ApiService
import com.abstraksi.aesthetic.data.repository.PhotoRepositoryImpl
import com.abstraksi.aesthetic.domain.repository.PhotoRepository
import com.abstraksi.aesthetic.presentation.viewmodel.PhotoViewModel
import com.abstraksi.aesthetic.data.repository.AdsJsonRepositoryImpl
import com.abstraksi.aesthetic.domain.repository.AdsJsonRepository
import com.abstraksi.aesthetic.presentation.viewmodel.AdsJsonViewModel
import com.abstraksi.aesthetic.config.AdsHelper
import com.abstraksi.aesthetic.utils.rx.AppSchedulerProvider
import com.abstraksi.aesthetic.utils.rx.SchedulerProvider

val appModule = module {
    single { provideOkHttpClient() }
    single { createWebService<ApiService>(get()) }

    single { PreferencesHelper(androidContext()) }

    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "app-database")
                .fallbackToDestructiveMigration()
                .build()
    }

    single { AppSchedulerProvider() as SchedulerProvider }

    factory { AdsHelper(get()) }

}

val dataModule = module {
    single { get<AppDatabase>().photoDao() }
    single { get<AppDatabase>().favoriteDao() }
    single { PhotoRepositoryImpl(get(), get(), get()) as PhotoRepository }
    single { AdsJsonRepositoryImpl(get()) as AdsJsonRepository }

    viewModel {
        PhotoViewModel(
            get(),
            get()
        )
    }

    viewModel { AdsJsonViewModel(get(), get()) }
}

val myAppModule = listOf(appModule, dataModule)