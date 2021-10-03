package com.elvitalya.weather.model

import com.elvitalya.weather.utils.Constants.APP_ID
import com.elvitalya.weather.utils.Constants.METRIC_UNIT
import com.elvitalya.weather.di.DaggerAppComponent
import io.reactivex.Single
import javax.inject.Inject

class WeatherService {
    @Inject
    lateinit var api: WeatherApi

    init {
        DaggerAppComponent.create().injectWeatherService(this)
    }

    fun getWeather(city: String): Single<WeatherData> {
        return api.getWeather(city, "ru", APP_ID, METRIC_UNIT)
    }
}