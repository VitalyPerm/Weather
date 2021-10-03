package com.elvitalya.weather.di

import com.elvitalya.weather.utils.Constants.BASE_URL
import com.elvitalya.weather.model.WeatherApi
import com.elvitalya.weather.model.WeatherService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule {

    @Provides
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    fun provideWeatherService(): WeatherService {
        return WeatherService()
    }
}