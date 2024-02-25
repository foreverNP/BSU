package com.example.project4

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val name: String
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Wind(
    val speed: Double
)

data class OneCallResponse(
    val daily: List<Daily>
)

data class Daily(
    val temp: Temp,
    val weather: List<Weather>
)

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double
)

