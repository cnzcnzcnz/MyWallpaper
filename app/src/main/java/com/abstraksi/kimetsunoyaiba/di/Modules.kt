package com.abstraksi.kimetsunoyaiba.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.abstraksi.kimetsunoyaiba.data.db.AppDatabase
import com.abstraksi.kimetsunoyaiba.data.remote.createWebService
import com.abstraksi.kimetsunoyaiba.data.remote.provideOkHttpClient
import com.abstraksi.kimetsunoyaiba.data.pref.PreferencesHelper
import com.abstraksi.kimetsunoyaiba.data.remote.service.ApiService
import com.abstraksi.kimetsunoyaiba.data.repository.PhotoRepositoryImpl
import com.abstraksi.kimetsunoyaiba.domain.repository.PhotoRepository
import com.abstraksi.kimetsunoyaiba.presentation.viewmodel.PhotoViewModel
import com.abstraksi.kimetsunoyaiba.data.repository.AdsJsonRepositoryImpl
import com.abstraksi.kimetsunoyaiba.domain.repository.AdsJsonRepository
import com.abstraksi.kimetsunoyaiba.presentation.viewmodel.AdsJsonViewModel
import com.abstraksi.kimetsunoyaiba.config.AdsHelper
import com.abstraksi.kimetsunoyaiba.utils.rx.AppSchedulerProvider
import com.abstraksi.kimetsunoyaiba.utils.rx.SchedulerProvider

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