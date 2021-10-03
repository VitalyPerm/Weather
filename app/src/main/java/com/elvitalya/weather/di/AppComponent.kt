package com.elvitalya.weather.di

import com.elvitalya.weather.model.WeatherService
import com.elvitalya.weather.viewmodel.MainActivityViewModel
import dagger.Component


@Component(modules = [AppModule::class])
interface AppComponent {

    fun injectWeatherService(service: WeatherService)
    fun injectViewModel(viewModel: MainActivityViewModel)

}