package com.example.himmeltitting

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.himmeltitting.databinding.ActivityShowDataBinding
import com.example.himmeltitting.locationforecast.CompactTimeSeriesData
import com.example.himmeltitting.nilu.LuftKvalitet
import com.google.android.gms.maps.model.LatLng

class ShowDataActivity : AppCompatActivity() {

    private val viewModel: ShowDataActivityViewModel by viewModels()
    private lateinit var binding: ActivityShowDataBinding
    private lateinit var currentLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lat = intent.getDoubleExtra("Lat", 60.0)
        val lon = intent.getDoubleExtra("Lon", 10.0)

        currentLatLng = LatLng(lat, lon)
        viewData()
    }

    private fun viewData() {
        //gjor om til data class
        viewModel.getCompactForecast(currentLatLng.latitude, currentLatLng.longitude).observe(this) {
            setForecastText(binding.textView, it)
        }
    }

    private fun setForecastText(textView: TextView, data: CompactTimeSeriesData?) {
        if (data == null){
            textView.text = "Kunne ikke hente data"
            return
        }
        val luftKvalitet = getLuftkvalitet()
        val sunsetTime = getSunrise()
        val outText = "Nå (${data.time}):\n" +
                "Temperatur: ${data.temperature}, Skydekke: ${data.cloudCover}, Vindhastighet: ${data.wind_speed}\n" +
                "Precipation neste 6 timene: ${data.precipitation6Hours}\n" +
                "SymbolSummary neste 12 timer: ${data.summary12Hour}\n" +
                "Luftkvalitet: ${luftKvalitet}\n" +
                "Solnedgang: $sunsetTime"

        textView.text = outText
    }

    private fun getLuftkvalitet(): String{
        viewModel.fetchNiluMedRadius(currentLatLng.latitude, currentLatLng.longitude, 20)
        val liste = viewModel.getNilu()
        var theOne: LuftKvalitet? = null
        liste.observe(this){
            //This is wrong, finner ikke nærmeste værstasjon :/
            if (it.isNotEmpty()){
                theOne = it.last()
            }
        }
        if (theOne == null) {
            return "Fant ikke luftkvalitet"
        }
        return theOne?.value.toString()
    }

    private fun getSunrise(): String {
        var sunsetTime : String? = null
        viewModel.getSunriseData(currentLatLng.latitude, currentLatLng.longitude).observe(this){
            if (it != null) {
                sunsetTime = it.sunsetTime

            }
        }
        if (sunsetTime == null) {
            return "Fant ikke solnedgang"
        }
        return sunsetTime as String
    }
}