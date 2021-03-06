package com.abstraksi.aesthetic.data.remote.service

import com.abstraksi.aesthetic.data.remote.response.AdsJsonResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import com.abstraksi.aesthetic.data.remote.response.ListPhotoPinterestResponse
import io.reactivex.Single
import retrofit2.http.Url

interface ApiService {

    @GET("pidgets/boards/{username}/{album}/pins/")
    fun getPhoto(
        @Path("username") username: String,
        @Path("album") album: String
    ): Observable<ListPhotoPinterestResponse>

    @GET()
    fun getAdsJson(@Url url: String): Single<AdsJsonResponse>

}