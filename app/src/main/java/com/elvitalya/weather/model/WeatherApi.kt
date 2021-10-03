package com.elvitalya.weather.model


import io.reactivex.Single
import retrofit2.http.GET

interface WeatherApi {
    @GET("2.5/weather")
    fun getWeather() : Single<Weather>
}
