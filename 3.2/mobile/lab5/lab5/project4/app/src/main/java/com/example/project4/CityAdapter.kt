package com.example.project4

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project4.databinding.ItemCityBinding

class CityAdapter(
    private val cities: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    private val temperatures = MutableList<Double?>(cities.size) { null }

    inner class CityViewHolder(val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: String, temperature: Double?) {
            binding.tvCity.text = city
            binding.tvTemperature.text = temperature?.let { "$it °C" } ?: "Загрузка..."
            binding.root.setOnClickListener { onClick(city) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position], temperatures[position])
    }

    fun addCity() {
        temperatures.add(null)
    }

    override fun getItemCount(): Int = cities.size

    fun updateTemperature(position: Int, temp: Double) {
        temperatures[position] = temp
        notifyItemChanged(position)
    }
}
