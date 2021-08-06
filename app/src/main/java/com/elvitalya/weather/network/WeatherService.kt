package com.elvitalya.weather.network

import com.elvitalya.weather.models.byCity.WeatherResponseCity
import com.elvitalya.weather.models.byLocation.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("2.5/weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String?,
        @Query("lang") lang:String,
        @Query("appid") appid:String?
    ) : retrofit2.Call<WeatherResponse>

}


interface WeatherServiceByCity{
    @GET("2.5/weather")
    fun getWeatherByCity(
        @Query("q") city_name:String,
        @Query("lang") lang:String,
        @Query("appid") appid:String?,
        @Query("units") units: String?,

    ) : retrofit2.Call<WeatherResponseCity>
}