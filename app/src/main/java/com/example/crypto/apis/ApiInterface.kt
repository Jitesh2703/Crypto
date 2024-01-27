package com.example.crypto.apis

import com.example.crypto.models.MarketModel
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("data-api/v3/cryptocurrency/listing") // Base URL likely added in Retrofit instance
    suspend fun getMarketData(): Response<MarketModel>
}
