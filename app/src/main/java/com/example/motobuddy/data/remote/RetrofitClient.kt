package com.example.motobuddy.data.remote

import android.content.Context
import com.example.motobuddy.data.local.AuthManager
import com.example.motobuddy.data.remote.api.MotoBuddyApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var apiService: MotoBuddyApiService? = null

    fun getApiService(context: Context): MotoBuddyApiService {
        if (apiService == null) {
            val authManager = AuthManager(context)
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(authManager))
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(MotoBuddyApiService::class.java)
        }
        return apiService!!
    }
}
