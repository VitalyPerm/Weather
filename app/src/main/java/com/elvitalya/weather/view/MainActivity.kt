package com.elvitalya.weather.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.elvitalya.weather.R
import com.elvitalya.weather.databinding.ActivityMainBinding
import com.elvitalya.weather.utils.Constants
import com.elvitalya.weather.utils.Functions.getUnit
import com.elvitalya.weather.utils.Functions.setIcon
import com.elvitalya.weather.utils.Functions.setVisibility
import com.elvitalya.weather.utils.Functions.unixTime
import com.elvitalya.weather.viewmodel.MainActivityViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    var city: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        binding.btnChangeCity.setOnClickListener {
            city = binding.etChangeCity.text.toString()
            city?.let {
                viewModel.getWeather(it)
                saveData()
            }

        }
        binding.btnRefresh.setOnClickListener {
            city?.let { city -> viewModel.getWeather(city) }
        }
        loadData()
        observeWeather()
    }


    @SuppressLint("SetTextI18n")
    private fun observeWeather() {
        viewModel.weather.observe(this, {
            with(binding) {
                tvMainDescription.text =
                    it.weather[0].description.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                tvTemp.text =
                    it.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                tvSunriseTime.text = unixTime(it.sys.sunrise)
                tvSunsetTime.text = unixTime(it.sys.sunset)
                tvCountry.text = it.sys.country
                tvName.text = it.name
                tvMax.text =
                    it.main.temp_max.toString() +
                            getUnit(application.resources.configuration.locales.toString()) + getString(
                        R.string.max
                    )
                tvMin.text =
                    it.main.temp_min.toString() +
                            getUnit(application.resources.configuration.locales.toString()) + getString(
                        R.string.min
                    )
                tvSpeed.text = it.wind.speed.toString()
                tvHumidity.text =
                    getString(R.string.humidity) + it.main.humidity.toString() + "%"

                setIcon(it.weather[0].icon, ivMain)
            }
        })

        viewModel.errorLoading.observe(this, {
            with(binding) {
                tvError.setVisibility(it)
                clWeather.setVisibility(!it)
                btnRefresh.setVisibility(it)
            }
//            if (it) {
//                binding.tvError.visibility = View.VISIBLE
//                binding.clWeather.visibility = View.GONE
//                binding.btnRefresh.visibility = View.VISIBLE
//            } else {
//                binding.clWeather.visibility = View.VISIBLE
//                binding.tvError.visibility = View.GONE
//                binding.btnRefresh.visibility = View.GONE
//            }
        })

        viewModel.loading.observe(this, {
            with(binding) {
                clWeather.setVisibility(!it)
                progressBar.setVisibility(it)
            }
//            if (it) {
//                binding.progressBar.visibility = View.VISIBLE
//                binding.clWeather.visibility = View.GONE
//            } else {
//                binding.progressBar.visibility = View.GONE
//                binding.clWeather.visibility = View.VISIBLE
//            }
        })

    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.WEATHER_RESPONSE_DATA, city).apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        city = sharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, null)
        city?.let { viewModel.getWeather(it) }
    }
}