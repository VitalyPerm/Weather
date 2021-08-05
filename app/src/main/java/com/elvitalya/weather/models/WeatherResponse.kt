package com.elvitalya.weather.models

import java.io.Serializable

data class WeatherResponse(
    val coord: Coord, //object {}
    val weather: List<Weather>, // list[]
    val base: String, // object {}
    val main: Main,
    val visibility: Int,
    val wind : Wind,
    val clouds: Clouds,
    val dt : Int,
    val sys: Sys,
    val id: Int,
    val name: String,
    val cod: Int

): Serializable