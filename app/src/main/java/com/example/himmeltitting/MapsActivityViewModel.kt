package com.example.himmeltitting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.himmeltitting.locationforecast.*
import com.example.himmeltitting.nilu.*
import com.example.himmeltitting.sunrise.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsActivityViewModel : ViewModel() {

    private val sunriseDS = SunRiseDataSource()
    private val niluDS = NiluDataSource()
    private val locationforecastDS = LocationforecastDS()


    //Sunrise
    private val location = MutableLiveData<Location>()

    fun getLocation(): MutableLiveData<Location> {
        return location
    }

    private fun fetchLocation(lat: Double?, long: Double?, date: String?, days: Int?, height: Double?, offset: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            sunriseDS.fetchLocation(lat, long, date, days, height, offset).also{
                location.postValue(it)
            }
        }
    }

    private val sunriseData = MutableLiveData<CompactSunriseData>()

    fun getSunriseData(lat: Double, long: Double): MutableLiveData<CompactSunriseData> {
        fetchSunriseData(lat, long)
        return sunriseData
    }

    private fun fetchSunriseData(lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            sunriseDS.getCompactSunriseData(lat, long).also{
                sunriseData.postValue(it)
            }
        }
    }

    //Nilu
    private val nilu = MutableLiveData<List<LuftKvalitet>>()

    fun getNilu(latitude: Double?, longitude: Double?, radius: Int): LiveData<List<LuftKvalitet>> {
        fetchNiluMedRadius(latitude, longitude, radius)
        return nilu
    }

    fun fetchNilu() {
        viewModelScope.launch(Dispatchers.IO) {
            niluDS.fetchNilus().also {
                nilu.postValue(it)
            }
        }
    }

    //Radius er nødt til å være int, altså et heltall siden det er slik APIet fungerer
    fun fetchNiluMedRadius(latitude: Double?, longitude: Double?, radius: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            niluDS.fetchNilusMedRadius(latitude, longitude, radius).also {
                nilu.postValue(it)
            }
        }
    }

    //Locationforecast
    private val forecastData: MutableLiveData<Locationforecast> by lazy {
        MutableLiveData<Locationforecast>()
    }

    fun getForecast(lat: Double, lon: Double): LiveData<Locationforecast> {
        loadForecast(lat, lon)
        return forecastData
    }

    private fun loadForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            locationforecastDS.getAllForecastData(lat, lon).also {
                forecastData.postValue(it)
            }
        }
    }

    private val compactForecastData: MutableLiveData<CompactTimeSeriesData?> by lazy {
        MutableLiveData<CompactTimeSeriesData?>()
    }

    fun getCompactForecast(lat: Double, lon: Double): LiveData<CompactTimeSeriesData?> {
        loadCompactForecast(lat, lon)
        return compactForecastData
    }

    private fun loadCompactForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            locationforecastDS.getCompactTimeseriesData(lat, lon).also {
                compactForecastData.postValue(it)
            }
        }
    }

}