package com.elvitalya.weather

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.elvitalya.weather.Constants.getUnit
import com.elvitalya.weather.Constants.hideProgressDialog
import com.elvitalya.weather.Constants.showCustomProgressDialog
import com.elvitalya.weather.Constants.unixTime
import com.elvitalya.weather.databinding.WeatherBinding
import com.elvitalya.weather.models.byCity.WeatherResponseCity
import com.elvitalya.weather.models.byLocation.WeatherResponse
import com.elvitalya.weather.network.WeatherService
import com.elvitalya.weather.network.WeatherServiceByCity
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: WeatherBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSharedPreferences: SharedPreferences
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Constants.APP_ACTIVITY = this
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        setUpUi()


        if (!isLocationEnabled()) {
            Toast.makeText(this, "Нет доступа к местоположению", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if (p0!!.areAllPermissionsGranted()) {
                            requestLocationData()
                        }
                        if (p0.isAnyPermissionPermanentlyDenied) {
                            Constants.showToast("Нет доступа к местоположению")
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }

                }).onSameThread()
                .check()
        }
        binding.btnChangeCity.setOnClickListener {
            val city = binding.etChangeCity.text.toString()
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: WeatherServiceByCity = retrofit.create(WeatherServiceByCity::class.java)

            val listCall: Call<WeatherResponseCity> =
                if (this.resources.configuration.locale.language == "ru") {
                    service.getWeatherByCity(city, "ru", Constants.APP_ID, Constants.METRIC_UNIT)
                } else {
                    service.getWeatherByCity(city, "en", Constants.APP_ID, Constants.METRIC_UNIT)
                }

            showCustomProgressDialog()
            listCall.enqueue(object : Callback<WeatherResponseCity> {
                @SuppressLint("SetTextI18n")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<WeatherResponseCity>,
                    response: Response<WeatherResponseCity>
                ) {
                    if (response.isSuccessful) {
                        hideProgressDialog()
                        val weatherListByCity: WeatherResponseCity = response.body()!!
                        for (i in weatherListByCity.weather.indices) {
                            with(binding) {
                                tvMainDescription.text =
                                    weatherListByCity.weather[i].description.capitalize()
                                tvTemp.text =
                                    weatherListByCity.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                                tvSunriseTime.text = unixTime(weatherListByCity.sys.sunrise)
                                tvSunsetTime.text = unixTime(weatherListByCity.sys.sunset)
                                tvCountry.text = weatherListByCity.sys.country
                                tvName.text = weatherListByCity.name
                                tvMax.text =
                                    weatherListByCity.main.temp_max.toString() +
                                            getUnit(application.resources.configuration.locales.toString()) + getString(
                                        R.string.max
                                    )
                                tvMin.text =
                                    weatherListByCity.main.temp_min.toString() +
                                            getUnit(application.resources.configuration.locales.toString()) + getString(
                                                                                    R.string.min)
                                tvSpeed.text = weatherListByCity.wind.speed.toString()
                                tvHumidity.text =
                                    getString(R.string.humidity) + weatherListByCity.main.humidity.toString() + "%"

                                when (weatherListByCity.weather[i].icon) {
                                    "01d" -> ivMain.setImageResource(R.drawable.sunny)
                                    "02d" -> ivMain.setImageResource(R.drawable.cloud)
                                    "03d" -> ivMain.setImageResource(R.drawable.cloud)
                                    "04d" -> ivMain.setImageResource(R.drawable.cloud)
                                    "04n" -> ivMain.setImageResource(R.drawable.cloud)
                                    "10d" -> ivMain.setImageResource(R.drawable.rain)
                                    "11d" -> ivMain.setImageResource(R.drawable.storm)
                                    "13d" -> ivMain.setImageResource(R.drawable.snowflake)
                                    "01n" -> ivMain.setImageResource(R.drawable.cloud)
                                    "02n" -> ivMain.setImageResource(R.drawable.cloud)
                                    "03n" -> ivMain.setImageResource(R.drawable.cloud)
                                    "10n" -> ivMain.setImageResource(R.drawable.cloud)
                                    "11n" -> ivMain.setImageResource(R.drawable.rain)
                                    "13n" -> ivMain.setImageResource(R.drawable.snowflake)
                                    else -> ivMain.setImageResource(R.drawable.sunny)
                                }

                            }
                        }


                    } else {
                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad connection")
                            }
                            404 -> {
                                Log.e("Error 404", "Not found")
                            }
                            else -> {
                                Log.e("Error", "Generic error")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponseCity>, t: Throwable) {
                    hideProgressDialog()
                    Log.e("Errrorrrrr", t.message.toString())
                }

            })

        }

    }


    fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It's look like you didn't granted all permission")
            .setPositiveButton("Go to Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun getLocationWeatherDetails() {
        if (Constants.isNetworkAvailable(this)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService = retrofit.create(WeatherService::class.java)
            val listCall: Call<WeatherResponse> =
                if (this.resources.configuration.locale.language == "ru") {
                    service.getWeather(mLatitude, mLongitude, Constants.METRIC_UNIT, "ru", Constants.APP_ID)
                } else {
                    service.getWeather(mLatitude, mLongitude, Constants.METRIC_UNIT, "en", Constants.APP_ID)
                }
            showCustomProgressDialog()
            listCall.enqueue(object : Callback<WeatherResponse> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        hideProgressDialog()
                        val weatherList: WeatherResponse = response.body()!!
                        val weatherResponseJsonString = Gson().toJson(weatherList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                            .apply()
                        setUpUi()
                        Log.i("Response!!!!", "$weatherList")
                    } else {
                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad connection")
                            }
                            404 -> {
                                Log.e("Error 404", "Not found")
                            }
                            else -> {
                                Log.e("Error", "Generic error")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    hideProgressDialog()
                    Log.e("Errrorrrrr", t.message.toString())
                }

            })
        } else {
            Constants.showToast("You have not connected to internet")
        }
    }

    private val mLocationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            mLatitude = mLastLocation.latitude
            Log.i("current latitude", "$mLatitude")
            mLongitude = mLastLocation.longitude
            Log.i("current longitude", "$mLongitude")
            getLocationWeatherDetails()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallBack,
            Looper.myLooper()!!
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpUi() {
        val weatherResponseJsonString =
            mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")
        if (!weatherResponseJsonString.isNullOrEmpty()) {
            val weatherList =
                Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
            for (i in weatherList.weather.indices) {
                with(binding) {
                    tvMainDescription.text = weatherList.weather[i].description
                    tvTemp.text =
                        weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                    tvSunriseTime.text = unixTime(weatherList.sys.sunrise)
                    tvSunsetTime.text = unixTime(weatherList.sys.sunset)
                    tvCountry.text = weatherList.sys.country
                    tvName.text = weatherList.name
                    tvMax.text =
                        weatherList.main.temp_max.toString() +
                                getUnit(application.resources.configuration.locales.toString()) + getString(R.string.max)


                    tvMin.text =
                        weatherList.main.temp_min.toString() +
                                getUnit(application.resources.configuration.locales.toString()) + getString(R.string.min)
                    tvSpeed.text = weatherList.wind.speed.toString()
                    tvHumidity.text = getString(R.string.humidity) + weatherList.main.humidity.toString() + "%"

                    when (weatherList.weather[i].icon) {
                        "01d" -> ivMain.setImageResource(R.drawable.sunny)
                        "02d" -> ivMain.setImageResource(R.drawable.cloud)
                        "03d" -> ivMain.setImageResource(R.drawable.cloud)
                        "04d" -> ivMain.setImageResource(R.drawable.cloud)
                        "04n" -> ivMain.setImageResource(R.drawable.cloud)
                        "10d" -> ivMain.setImageResource(R.drawable.rain)
                        "11d" -> ivMain.setImageResource(R.drawable.storm)
                        "13d" -> ivMain.setImageResource(R.drawable.snowflake)
                        "01n" -> ivMain.setImageResource(R.drawable.cloud)
                        "02n" -> ivMain.setImageResource(R.drawable.cloud)
                        "03n" -> ivMain.setImageResource(R.drawable.cloud)
                        "10n" -> ivMain.setImageResource(R.drawable.cloud)
                        "11n" -> ivMain.setImageResource(R.drawable.rain)
                        "13n" -> ivMain.setImageResource(R.drawable.snowflake)
                        else -> ivMain.setImageResource(R.drawable.sunny)
                    }

                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                getLocationWeatherDetails()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}