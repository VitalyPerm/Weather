package com.elvitalya.weather.models

import java.io.Serializable

data class WeatherResponse(
    val coord: Coord, //object {}
    val weather: List<Weather>, // list[]
    val base: String,
    val main: Main, // object {}
    val visibility: Int,
    val wind : Wind,
    val clouds: Clouds,
    val dt : Int,
    val sys: Sys,
    val id: Int,
    val name: String,
    val cod: Int

): Serializable


//Response example
//{"coord": { "lon": 139,"lat": 35},
//    "weather": [
//    {
//        "id": 800,
//        "main": "Clear",
//        "description": "clear sky",
//        "icon": "01n"
//    }
//    ],
//    "base": "stations",
//    "main": {
//    "temp": 281.52,
//    "feels_like": 278.99,
//    "temp_min": 280.15,
//    "temp_max": 283.71,
//    "pressure": 1016,
//    "humidity": 93
//},
//    "wind": {
//    "speed": 0.47,
//    "deg": 107.538
//},
//    "clouds": {
//    "all": 2
//},
//    "dt": 1560350192,
//    "sys": {
//    "type": 3,
//    "id": 2019346,
//    "message": 0.0065,
//    "country": "JP",
//    "sunrise": 1560281377,
//    "sunset": 1560333478
//},
//    "timezone": 32400,
//    "id": 1851632,
//    "name": "Shuzenji",
//    "cod": 200
//}