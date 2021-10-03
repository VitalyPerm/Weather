package com.elvitalya.weather.model

import com.elvitalya.weather.di.DaggerAppComponent
import io.reactivex.Single
import javax.inject.Inject

class WeatherService {
    @Inject
    lateinit var api: WeatherApi

    init {
        DaggerAppComponent.create().injectWeatherService(this)
    }

    fun getWeather(): Single<Weather> {
        return api.getWeather()
    }
}