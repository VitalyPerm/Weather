package com.elvitalya.weather.model


import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("2.5/weather")
    fun getWeather(
        @Query("q") city_name:String,
        @Query("lang") lang:String,
        @Query("appid") appid:String?,
        @Query("units") units: String?,
    ) : Single<WeatherData>
}
