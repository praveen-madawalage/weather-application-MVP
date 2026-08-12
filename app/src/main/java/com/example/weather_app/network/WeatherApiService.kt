package com.example.weather_app.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.weather_app.model.WeatherResponse

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>
}