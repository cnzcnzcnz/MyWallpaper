package com.abstraksi.gambarpemainbola.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.abstraksi.gambarpemainbola.data.db.AppDatabase
import com.abstraksi.gambarpemainbola.data.remote.createWebService
import com.abstraksi.gambarpemainbola.data.remote.provideOkHttpClient
import com.abstraksi.gambarpemainbola.data.pref.PreferencesHelper
import com.abstraksi.gambarpemainbola.data.remote.service.ApiService
import com.abstraksi.gambarpemainbola.data.repository.PhotoRepositoryImpl
import com.abstraksi.gambarpemainbola.domain.repository.PhotoRepository
import com.abstraksi.gambarpemainbola.presentation.viewmodel.PhotoViewModel
import com.abstraksi.gambarpemainbola.data.repository.AdsJsonRepositoryImpl
import com.abstraksi.gambarpemainbola.domain.repository.AdsJsonRepository
import com.abstraksi.gambarpemainbola.presentation.viewmodel.AdsJsonViewModel
import com.abstraksi.gambarpemainbola.config.AdsHelper
import com.abstraksi.gambarpemainbola.utils.rx.AppSchedulerProvider
import com.abstraksi.gambarpemainbola.utils.rx.SchedulerProvider

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