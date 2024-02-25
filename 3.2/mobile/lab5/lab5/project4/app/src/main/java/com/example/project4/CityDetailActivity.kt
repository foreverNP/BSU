package com.example.project4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project4.databinding.ActivityCityDetailBinding
import kotlinx.coroutines.*

class CityDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCityDetailBinding
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val city = intent.getStringExtra("city") ?: return
        binding.tvCityName.text = city

        scope.launch {
            try {
                val currentWeather = withContext(Dispatchers.IO) {
                    RetrofitClient.weatherService.getCurrentWeather(city)
                }
                binding.tvTemperature.text = "${currentWeather.main.temp} °C"
                binding.tvDescription.text = currentWeather.weather[0].description
                binding.tvHumidity.text = "Влажность: ${currentWeather.main.humidity}%"
                binding.tvWind.text = "Ветер: ${currentWeather.wind.speed} м/с"

                val oneCallResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.weatherService.getOneCall(
                        currentWeather.coord.lat,
                        currentWeather.coord.lon
                    )
                }
                val forecastText = StringBuilder()
                oneCallResponse.daily.take(7).forEachIndexed { i, day ->
                    forecastText.append("День ${i + 1}: ${day.temp.day} °C, ${day.weather[0].description}\n")
                }
                binding.tvForecast.text = forecastText.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
