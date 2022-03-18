package com.example.himmeltitting.main_data_source

import com.example.himmeltitting.locationforecast.CompactTimeSeriesData
import com.example.himmeltitting.locationforecast.LocationforecastDS
import com.example.himmeltitting.nilu.NiluDataSource
import com.example.himmeltitting.sunrise.CompactSunriseData
import com.example.himmeltitting.sunrise.SunRiseDataSource

class MainDataSource {

    private val sunriseDS = SunRiseDataSource()
    private val niluDS = NiluDataSource()
    private val locationforecastDS = LocationforecastDS()

    private lateinit var observations : MutableList<Observation>


    suspend fun getObservation(latitude: Double, longitude: Double, radius: Int) : List<Observation>{
        observations = mutableListOf()

        val locationForecastData =
            latitude?.let {
                locationforecastDS.getCompactTimeseriesData(it, longitude)
            } as CompactTimeSeriesData

        //val data = longitude?.let { locationforecastDS.getAllForecastData(latitude, it) }
        //val locationForecastData = locationforecastDS.createCompactData()



        val niluData = niluDS.fetchNilusMedRadius(latitude, longitude, radius)

        val sunriseData = latitude?.let {
            sunriseDS.getCompactSunriseData(it, longitude)
        } as CompactSunriseData

        val temperature =  locationForecastData.temperature
        val cloudCover = locationForecastData.cloudCover
        val windSpeed = locationForecastData.wind_speed
        val airQuality = niluData?.get(0)?.value
        val sunsetTime = sunriseData.sunsetTime

        val observation = Observation(temperature, cloudCover,  windSpeed, airQuality.toString(), sunsetTime.toString());

        observations.add(observation)

        return observations


    }
}