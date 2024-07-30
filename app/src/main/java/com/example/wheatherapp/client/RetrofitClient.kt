package com.example.wheatherapp.client

import com.example.wheatherapp.interfaces.ApiInterface
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient {
    init {
        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    companion object {
        private var retrofitClient: RetrofitClient? = null
        private lateinit var retrofit: Retrofit
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        @Synchronized
        fun getClientInstance(): RetrofitClient {
            if (retrofitClient == null) {
                retrofitClient = RetrofitClient()
            }
            return retrofitClient!!
        }
    }

    fun getApi(): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}
