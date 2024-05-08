package com.rm.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rm.api.ApiInterface
import com.rm.preferences.SharedPrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideGsonFactory(gson: Gson): GsonConverterFactory = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun providesOkHttp(
        @Named("requestInterceptor") requestInterceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(requestInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("requestInterceptor")
    fun providesHeaderRequestInterceptor(): Interceptor {
        return Interceptor.invoke {
            val request = it.request()
            val builder = request.newBuilder()
            val headers = getRequestHeaders()

            headers.forEach { (key, value) ->
                builder.addHeader(key, value)
            }
            return@invoke it.proceed(builder.build())
        }
    }

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun getRequestHeaders(): HashMap<String, String> {
        val map = HashMap<String, String>()
        val token = SharedPrefsManager.token
        if (token.isNotEmpty()) {
            map["Authorization"] = "Bearer $token"
        }
        map["accept"] = "application/json"
        return map
    }

    @Provides
    @Singleton
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://us-central1-res-management-torinit.cloudfunctions.net/")
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()

    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): ApiInterface = retrofit
        .create(ApiInterface::class.java)
}