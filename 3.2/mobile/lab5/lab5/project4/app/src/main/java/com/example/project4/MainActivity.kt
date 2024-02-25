package com.example.project4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project4.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val cities = mutableListOf("Minsk", "Sarajevo")
    private lateinit var adapter: CityAdapter
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CityAdapter(cities) { city ->
            val intent = Intent(this, CityDetailActivity::class.java)
            intent.putExtra("city", city)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val dialog = AddCityDialog { newCity ->
                if (newCity.isNotBlank()) {
                    cities.add(newCity)
                    adapter.addCity()
                    adapter.notifyItemInserted(cities.size - 1)
                    updateCityWeather(cities.size - 1, newCity)
                }
            }
            dialog.show(supportFragmentManager, "AddCityDialog")
        }

        cities.forEachIndexed { index, city ->
            updateCityWeather(index, city)
        }
    }

    private fun updateCityWeather(index: Int, city: String) {
        scope.launch {
            try {
                val weatherResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.weatherService.getCurrentWeather(city)
                }
                adapter.updateTemperature(index, weatherResponse.main.temp)
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
