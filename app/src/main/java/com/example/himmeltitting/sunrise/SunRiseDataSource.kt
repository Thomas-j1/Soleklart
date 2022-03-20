package com.example.himmeltitting.sunrise

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson

class SunRiseDataSource {

    // Et gson-objekt som hjelper med aa parse data fra JSON til Kotlin
    private val gson = Gson()

    // Ett eksempel paa API-kall
    private val path = "https://in2000-apiproxy.ifi.uio.no/weatherapi/sunrise/2.0/.json?"

    // Returnerer "Location" siden den er viktigst aa plukke ut
    suspend fun fetchLocation(lat: Double?, long: Double?, date: String?, days: Int?, height: Double?, offset: String?): Location? {
        try {
            // Forsikre at man ikke skriver over å se 15 dager fremover
            if(days!! <= 15){
                val parametere = "lat=${lat}&lon=${long}&date=${date}&days=${days}&height=${height}&offset=${offset}"
                val fetchObjekt = gson.fromJson(Fuel.get(path+parametere).awaitString(), SunriseBase::class.java)
                return fetchObjekt.location
            }
            else{
                println("Value of 'days' were over 15, must be 15 or under")
                return null
            }
        } catch(exception: Exception) {
            println("A network request exception was thrown: ${exception.message}")
            return null
        }
    }

    suspend fun getCompactSunriseData (latitude: Double, longitude: Double): CompactSunriseData? {
        val date = "2022-03-20"
        val time = "14:00"
        val days = 1
        val height = 0.2
        val data = fetchLocation(latitude, longitude, date, days, height ,time)

        return data?.let { makeCompactSunriseData(it) }
    }

    fun makeCompactSunriseData(data : Location): CompactSunriseData {
        val sunsetTime = data.time?.get(0)?.sunset?.time.toString()
        return CompactSunriseData(sunsetTime)
    }
}
