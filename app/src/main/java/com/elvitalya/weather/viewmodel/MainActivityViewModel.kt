package com.elvitalya.weather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.elvitalya.weather.di.DaggerAppComponent
import com.elvitalya.weather.model.WeatherData
import com.elvitalya.weather.model.WeatherService
import com.elvitalya.weather.utils.Functions
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

    val weather = MutableLiveData<WeatherData>()
    val loading = MutableLiveData<Boolean>()
    val errorLoading = MutableLiveData<Boolean>()

    fun getWeather(city: String) {
        loading.value = true
        disposable.add(
            weatherService.getWeather(city)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherData>() {
                    override fun onSuccess(t: WeatherData) {
                        loading.value = false
                        errorLoading.value = false
                        weather.value = t
                    }

                    override fun onError(e: Throwable) {
                        Functions.showToast(e.message.toString(), getApplication())
                        loading.value = false
                        errorLoading.value = true
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}