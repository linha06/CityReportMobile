package com.linha.myreportcity.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL_LARAVELAPI = "http://192.168.1.5:8000/" // pakai ip perangkat, bukan 127

    val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val reportsClient: ReportsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_LARAVELAPI)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ReportsApiService::class.java)
    }

    val usersClient: UsersApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_LARAVELAPI)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(UsersApiService::class.java)
    }

    val cloudinaryClient: CloudinaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_LARAVELAPI)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(CloudinaryApiService::class.java)
    }
}