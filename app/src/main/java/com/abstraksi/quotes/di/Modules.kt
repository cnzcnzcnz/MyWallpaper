package com.abstraksi.quotes.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.abstraksi.quotes.data.db.AppDatabase
import com.abstraksi.quotes.data.remote.createWebService
import com.abstraksi.quotes.data.remote.provideOkHttpClient
import com.abstraksi.quotes.data.pref.PreferencesHelper
import com.abstraksi.quotes.data.remote.service.ApiService
import com.abstraksi.quotes.data.repository.PhotoRepositoryImpl
import com.abstraksi.quotes.domain.repository.PhotoRepository
import com.abstraksi.quotes.presentation.viewmodel.PhotoViewModel
import com.abstraksi.quotes.data.repository.AdsJsonRepositoryImpl
import com.abstraksi.quotes.domain.repository.AdsJsonRepository
import com.abstraksi.quotes.presentation.viewmodel.AdsJsonViewModel
import com.abstraksi.quotes.config.AdsHelper
import com.abstraksi.quotes.utils.rx.AppSchedulerProvider
import com.abstraksi.quotes.utils.rx.SchedulerProvider

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