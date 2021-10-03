package com.elvitalya.weather.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.elvitalya.weather.R
import java.text.SimpleDateFormat
import java.util.*

object Functions {
    fun showToast(str: String, context: Context) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
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


    fun setIcon(uri: String, image: ImageView) {
        when (uri) {
            "01d" -> image.setImageResource(R.drawable.sunny)
            "02d" -> image.setImageResource(R.drawable.cloud)
            "03d" -> image.setImageResource(R.drawable.cloud)
            "04d" -> image.setImageResource(R.drawable.cloud)
            "04n" -> image.setImageResource(R.drawable.cloud)
            "10d" -> image.setImageResource(R.drawable.rain)
            "11d" -> image.setImageResource(R.drawable.storm)
            "13d" -> image.setImageResource(R.drawable.snowflake)
            "01n" -> image.setImageResource(R.drawable.cloud)
            "02n" -> image.setImageResource(R.drawable.cloud)
            "03n" -> image.setImageResource(R.drawable.cloud)
            "10n" -> image.setImageResource(R.drawable.cloud)
            "11n" -> image.setImageResource(R.drawable.rain)
            "13n" -> image.setImageResource(R.drawable.snowflake)
            else -> image.setImageResource(R.drawable.sunny)
        }
    }

    fun View.setVisibility(boolean: Boolean) {
        visibility = if (boolean) View.VISIBLE else View.GONE
    }
}
