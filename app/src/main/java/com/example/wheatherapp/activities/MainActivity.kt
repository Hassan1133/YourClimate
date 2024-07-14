package com.example.wheatherapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wheatherapp.R
import com.example.wheatherapp.data_classes.WeatherApp
import com.example.wheatherapp.databinding.ActivityMainBinding
import com.example.wheatherapp.interfaces.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiKey: String = "b356d4d9a2cf2b881c78f34fb2fba641"
    private val baseUrl: String = "https://api.openweathermap.org/data/2.5/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init()
    {
        fetchWeatherData("Faisalabad")
        searchCity()
    }

    private fun searchCity() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, apiKey, "metric")

        response.enqueue(object: Callback<WeatherApp>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null)
                {
                    binding.temperature.text = "${responseBody.main.temp.roundToInt()}°C"
                    binding.humidity.text = "${responseBody.main.humidity} %"
                    binding.windSpeed.text = "${responseBody.wind.speed} m/s"
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    binding.condition.text = condition
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: ${responseBody.main.temp_max}°C"
                    binding.minTemp.text = "Min Temp: ${responseBody.main.temp_min}°C"
                    binding.feelsLike.text = "${responseBody.main.feels_like}°C"
                    binding.sunrise.text = time(responseBody.sys.sunrise.toLong())
                    binding.sunset.text = time(responseBody.sys.sunset.toLong())
                    binding.day.text = dayName()
                    binding.date.text = date()
                    binding.city.text = cityName
                    changeImagesBasedOnWeather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun changeImagesBasedOnWeather(condition: String)
    {
        when(condition)
        {
            "Haze" -> {
                binding.root.setBackgroundResource(R.drawable.hazy_bg)
                binding.lottieAnimationView.setAnimation(R.raw.hazy_animation)
            }
            "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.main_screen_bg)
                binding.lottieAnimationView.setAnimation(R.raw.home_weather)
            }
            "Clouds" -> {
                binding.root.setBackgroundResource(R.drawable.clouds_bg)
                binding.lottieAnimationView.setAnimation(R.raw.hazy_animation)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun dayName(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(time: Long) : String
    {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format((Date(time*1000)))
    }
}