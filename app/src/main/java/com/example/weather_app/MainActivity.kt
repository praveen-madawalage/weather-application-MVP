package com.example.weather_app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weather_app.model.WeatherResponse
import com.example.weather_app.network.RetrofitClient
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val API_KEY = "faa8d463d91743830d3849ed83337125"

    private lateinit var cityInput: EditText
    private lateinit var searchButton: Button

    private lateinit var resultCard: MaterialCardView
    private lateinit var errorText: TextView
    private lateinit var cityNameText: TextView
    private lateinit var tempText: TextView
    private lateinit var conditionText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityInput = findViewById(R.id.cityInput)
        searchButton = findViewById(R.id.searchButton)
        resultCard = findViewById(R.id.resultCard)
        errorText = findViewById(R.id.errorText)
        cityNameText = findViewById(R.id.cityNameText)
        tempText = findViewById(R.id.tempText)
        conditionText = findViewById(R.id.conditionText)
        humidityText = findViewById(R.id.humidityText)
        windText = findViewById(R.id.windText)

        searchButton.setOnClickListener {
            val city = cityInput.text.toString().trim()

            // Case 1: empty input
            if (city.isEmpty()) {
                showError("Please enter a city name")
                return@setOnClickListener
            }
            fetchWeather(city)
        }
    }

    private fun fetchWeather(city: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getWeather(city, API_KEY)

                if (response.isSuccessful && response.body() != null) {
                    displayWeather(response.body()!!)
                } else if (response.code() == 404) {
                    // Case 2: invalid city
                    showError("City not found. Check the spelling and try again.")
                } else {
                    // Case 4: API returned an error status
                    showError("Something went wrong (error ${response.code()}). Please try again.")
                }
            } catch (e: IOException) {
                // Case 3: no internet / can't reach the server
                showError("Can't connect. Check your internet connection.")
            } catch (e: Exception) {
                showError("Unexpected error: ${e.message}")
            }
        }
    }

    private fun displayWeather(weather: WeatherResponse) {
        errorText.visibility = View.GONE
        resultCard.visibility = View.VISIBLE
        cityNameText.text = weather.name
        tempText.text = "${weather.main.temp}°C"
        conditionText.text = weather.weather.firstOrNull()?.description ?: "N/A"
        humidityText.text = "Humidity: ${weather.main.humidity}%"
        windText.text = "Wind Speed: ${weather.wind.speed} km/h"
    }

    private fun showError(message: String) {
        resultCard.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        errorText.text = message
    }
}