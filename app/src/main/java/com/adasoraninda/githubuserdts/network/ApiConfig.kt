package com.adasoraninda.githubuserdts.network

import com.adasoraninda.githubuserdts.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {

    private val interceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val newRequest = original.newBuilder()
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Authorization", "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")
                .build()

            chain.proceed(newRequest)
        }
        .addInterceptor(interceptor)
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(GitHubUserService.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: GitHubUserService = retrofit.create(GitHubUserService::class.java)

}