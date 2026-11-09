package com.example.data.weather

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.TimeZone

data class WeatherInfo(
    val temperatureC: Int,
    val description: String,
    val emoji: String,
    val isAvailable: Boolean = true
) {
    val displayString: String
        get() = "$temperatureC°C $emoji"
}

object WeatherService {

    // Default pleasant weather while offline or on cold start
    private var cachedWeather: WeatherInfo = WeatherInfo(
        temperatureC = 22,
        description = "Mild & Calm",
        emoji = "🌤️",
        isAvailable = true
    )

    fun getCachedWeather(): WeatherInfo = cachedWeather

    suspend fun fetchCurrentWeather(context: Context? = null): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            // Determine approximate coordinates from system timezone for accuracy without needing GPS permission
            val tz = TimeZone.getDefault().id
            val (lat, lon) = getCoordinatesForTimezone(tz)

            val endpoint = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "DailyPulse-Android/1.0")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                connection.disconnect()

                val json = JSONObject(response)
                val currentObj = if (json.has("current")) {
                    json.getJSONObject("current")
                } else if (json.has("current_weather")) {
                    json.getJSONObject("current_weather")
                } else null

                if (currentObj != null) {
                    val temp = if (currentObj.has("temperature_2m")) {
                        currentObj.getDouble("temperature_2m").toInt()
                    } else if (currentObj.has("temperature")) {
                        currentObj.getDouble("temperature").toInt()
                    } else 22

                    val code = if (currentObj.has("weather_code")) {
                        currentObj.getInt("weather_code")
                    } else if (currentObj.has("weathercode")) {
                        currentObj.getInt("weathercode")
                    } else 0

                    val (desc, icon) = mapWeatherCode(code)
                    val result = WeatherInfo(
                        temperatureC = temp,
                        description = desc,
                        emoji = icon,
                        isAvailable = true
                    )
                    cachedWeather = result
                    return@withContext result
                }
            }
        } catch (e: Throwable) {
            Log.d("WeatherService", "Weather fetch offline/silent fallback: ${e.message}")
        }
        return@withContext cachedWeather
    }

    private fun mapWeatherCode(code: Int): Pair<String, String> {
        return when (code) {
            0 -> Pair("Clear Sky", "☀️")
            1 -> Pair("Mainly Clear", "🌤️")
            2 -> Pair("Partly Cloudy", "⛅")
            3 -> Pair("Overcast", "☁️")
            45, 48 -> Pair("Foggy", "🌫️")
            51, 53, 55 -> Pair("Light Drizzle", "🌦️")
            61, 63, 65 -> Pair("Rain", "🌧️")
            71, 73, 75 -> Pair("Snow", "❄️")
            80, 81, 82 -> Pair("Showers", "🌦️")
            95, 96, 99 -> Pair("Thunderstorm", "⛈️")
            else -> Pair("Pleasant", "🌤️")
        }
    }

    private fun getCoordinatesForTimezone(tz: String): Pair<Double, Double> {
        return when {
            tz.contains("Calcutta") || tz.contains("Kolkata") || tz.contains("India") -> Pair(28.6139, 77.2090)
            tz.contains("London") || tz.contains("Europe/London") -> Pair(51.5074, -0.1278)
            tz.contains("New_York") -> Pair(40.7128, -74.0060)
            tz.contains("Los_Angeles") -> Pair(34.0522, -118.2437)
            tz.contains("Chicago") -> Pair(41.8781, -87.6298)
            tz.contains("Tokyo") -> Pair(35.6762, 139.6503)
            tz.contains("Paris") || tz.contains("Berlin") -> Pair(48.8566, 2.3522)
            tz.contains("Sydney") -> Pair(-33.8688, 151.2093)
            tz.contains("Dubai") -> Pair(25.2048, 55.2708)
            tz.contains("Singapore") -> Pair(1.3521, 103.8198)
            else -> Pair(28.6139, 77.2090)
        }
    }
}
