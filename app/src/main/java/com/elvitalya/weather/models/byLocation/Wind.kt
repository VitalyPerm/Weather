package com.elvitalya.weather.models.byLocation

import java.io.Serializable

data class Wind(
    val speed: Double,
    val deg: Int
) : Serializable