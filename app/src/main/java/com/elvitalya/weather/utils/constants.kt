package com.elvitalya.weather

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    lateinit var APP_ACTIVITY: MainActivity
    const val APP_ID = "95b07f01d986c7644f2812866d87885c"
    const val BASE_URL: String = "http://api.openweathermap.org/data/"
    const val METRIC_UNIT: String = "metric"
    const val PREFERENCE_NAME = "WeatherPreference"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"
    private var mProgressDialog: Dialog? = null


    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network      = connectivityManager.activeNetwork ?: return false
            val activeNetWork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }


    fun showToast(str: String){
        Toast.makeText(APP_ACTIVITY, str, Toast.LENGTH_SHORT).show()
    }

    fun unixTime(time: Long): String? {
        val date = Date(time * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.UK)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
    fun getUnit(value: String): String? {
        var value = "°С"
        if ("US" == value || "LR" == value || "MM" == value) {
            value = "°F"
        }
        return value
    }

    fun showCustomProgressDialog() {
        mProgressDialog = Dialog(APP_ACTIVITY)
        mProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog?.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog?.dismiss()
        }
    }







}