package com.example.project4


import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    companion object {
        const val API_KEY = "f576fef22c875b8b920f46104e52d005"
    }

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") appid: String = API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("data/2.5/onecall")
    suspend fun getOneCall(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely,hourly,alerts",
        @Query("appid") appid: String = API_KEY,
        @Query("units") units: String = "metric"
    ): OneCallResponse
}
