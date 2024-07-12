package com.example.wheatherapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wheatherapp.data_classes.WeatherApp
import com.example.wheatherapp.databinding.ActivityMainBinding
import com.example.wheatherapp.interfaces.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init()
    {
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData("Faisalabad", "b356d4d9a2cf2b881c78f34fb2fba641", "metric")

        response.enqueue(object: Callback<WeatherApp>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null)
                {
                    binding.temperature.text = "${responseBody.main.temp.roundToInt()}°C"
                    binding.humidity.text = "${responseBody.main.humidity} %"
                    binding.windSpeed.text = "${responseBody.wind.speed} m/s"
                    binding.condition.text = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    binding.weather.text = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    binding.maxTemp.text = "Max Temp: ${responseBody.main.temp_max}°C"
                    binding.minTemp.text = "Min Temp: ${responseBody.main.temp_min}°C"
                    binding.feelsLike.text = "${responseBody.main.feels_like}°C"
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }
}