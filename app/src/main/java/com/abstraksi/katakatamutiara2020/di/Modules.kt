package com.abstraksi.katakatamutiara2020.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.abstraksi.katakatamutiara2020.data.db.AppDatabase
import com.abstraksi.katakatamutiara2020.data.remote.createWebService
import com.abstraksi.katakatamutiara2020.data.remote.provideOkHttpClient
import com.abstraksi.katakatamutiara2020.data.pref.PreferencesHelper
import com.abstraksi.katakatamutiara2020.data.remote.service.ApiService
import com.abstraksi.katakatamutiara2020.data.repository.PhotoRepositoryImpl
import com.abstraksi.katakatamutiara2020.domain.repository.PhotoRepository
import com.abstraksi.katakatamutiara2020.presentation.viewmodel.PhotoViewModel
import com.abstraksi.katakatamutiara2020.data.repository.AdsJsonRepositoryImpl
import com.abstraksi.katakatamutiara2020.domain.repository.AdsJsonRepository
import com.abstraksi.katakatamutiara2020.presentation.viewmodel.AdsJsonViewModel
import com.abstraksi.katakatamutiara2020.config.AdsHelper
import com.abstraksi.katakatamutiara2020.utils.rx.AppSchedulerProvider
import com.abstraksi.katakatamutiara2020.utils.rx.SchedulerProvider

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