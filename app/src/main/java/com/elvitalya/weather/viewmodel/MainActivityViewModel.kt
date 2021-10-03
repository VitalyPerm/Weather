package com.elvitalya.weather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.elvitalya.weather.di.DaggerAppComponent
import com.elvitalya.weather.model.Weather
import com.elvitalya.weather.model.WeatherService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    @Inject
    lateinit var weatherService: WeatherService

    init {
        DaggerAppComponent.create().injectViewModel(this)
    }

    val weather = MutableLiveData<Weather>()

    fun getWeather() {
        disposable.add(
            weatherService.getWeather()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Weather>(){
                    override fun onSuccess(t: Weather) {
                    weather.value = t
                    }

                    override fun onError(e: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
        )
    }

}