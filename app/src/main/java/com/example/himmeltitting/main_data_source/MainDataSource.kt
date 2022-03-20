package com.example.himmeltitting.main_data_source

import com.example.himmeltitting.locationforecast.CompactTimeSeriesData
import com.example.himmeltitting.locationforecast.LocationforecastDS
import com.example.himmeltitting.nilu.NiluDataSource
import com.example.himmeltitting.sunrise.CompactSunriseData
import com.example.himmeltitting.sunrise.SunRiseDataSource
import java.text.SimpleDateFormat
import java.util.*

class MainDataSource {

    private val sunriseDS = SunRiseDataSource()
    private val niluDS = NiluDataSource()
    private val locationforecastDS = LocationforecastDS()

    private lateinit var observations : MutableList<Observation>


    suspend fun getObservation(latitude: Double, longitude: Double, radius: Int) : List<Observation>{
        observations = mutableListOf()

        val locationForecastData =
            latitude.let {
                locationforecastDS.getCompactTimeseriesData(it, longitude)
            } as CompactTimeSeriesData

        //val data = longitude?.let { locationforecastDS.getAllForecastData(latitude, it) }
        //val locationForecastData = locationforecastDS.createCompactData()



        val niluData = niluDS.fetchNilusMedRadius(latitude, longitude, radius)
        println("NILU: "+ niluData.toString())

        val sunriseData = latitude.let {
            sunriseDS.getCompactSunriseData(it, longitude)
        } as CompactSunriseData

        val temperature =  locationForecastData.temperature
        val cloudCover = locationForecastData.cloudCover
        val windSpeed = locationForecastData.wind_speed
        lateinit var airQuality: String

        // nilu return empty list if nothing has been fetched
        if (niluData != null) {
            airQuality = if(niluData.isEmpty()){ "ingen data" }
            else{
                niluData[0].value.toString()
            }
        }



        val sunsetTime = sunriseData.sunsetTime
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val time = formatter.parse(sunsetTime)
        val timeString = time.hours.toString()+":"+time.minutes.toString()

        val observation = Observation(temperature, cloudCover,  windSpeed, airQuality.toString(), time.toString());

        observations.add(observation)

        return observations


    }
}