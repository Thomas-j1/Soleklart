package com.example.himmeltitting

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.himmeltitting.databinding.ActivityMapsBinding
import com.example.himmeltitting.locationforecast.CompactTimeSeriesData
import com.example.himmeltitting.nilu.LuftKvalitet
import com.example.himmeltitting.sunrise.CompactSunriseData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // det vanlige
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var currentLatLng: LatLng
    private var marker: Marker? = null
    private val viewModel: MapsActivityViewModel by viewModels()

    //fused location privider er en api som brukes til å få siste kjente lokasjon.
    // Den er vist veldig bra å bruke, står mer om det her https://developer.android.com/training/location/retrieve-current
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    // variabler for koordinater


    // companion object som er litt som java sin statiske variabler (sa en dude på youtube)
    // bruker i permission sjekk
    companion object{
        private const val LOCATION_REQUEST_CODE = 1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // dette  trenges for å håndtere kart-fragmentet
        //Map vises i appen som en fragment, fordi det skal vist være enklest(?): lifecycles av kartet osv handles automatisk og asyklisk
        // kan ikke så mye om dette, men her står det bra:https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setDefaultMapLocationNorway(mMap)
        setUpMap()
        addOnMapClickListener()
        addSearchView()
    }

    //setter start lokasjon, hvis stedlokasjon ikke er satt, til Norge
    private fun setDefaultMapLocationNorway(googleMap: GoogleMap) {
        val mPoint : CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(62.47, 8.46), 4f)
        // moves camera to coordinates
        googleMap.moveCamera(mPoint)
    }


    private fun setUpMap() {
        // sjekker permissions fra brukeren
        if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }

        // får lokasjon, setter markøren på kartet
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if(location != null){
                lastLocation = location
                currentLatLng = LatLng(location.latitude, location.longitude)

                // egendefinert metode
                //placeMarkerOnMap(currentLatLng)

                // zoom effekt som skjer når lokasjon blir funnet
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }



    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        marker?.remove()
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        marker = mMap.addMarker(markerOptions)
        marker?.showInfoWindow()
        viewData()
    }



    override fun onMarkerClick(p0: Marker)= false


    private fun addOnMapClickListener(){
        // gjør at vi kan klikke på et sted og få koordinater
        mMap.setOnMapClickListener { latlng ->
            val location = LatLng(latlng.latitude, latlng.longitude)
            currentLatLng = location
            placeMarkerOnMap(location)
            // kan velge hvor mye vi vil zoome inn
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        }
    }


    // copy paste fra https://www.geeksforgeeks.org/how-to-add-searchview-in-google-maps-in-android/
    private fun addSearchView(){
        val searchView = binding.idSearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are getting the
                // location name from search view.
                val location: String = searchView.query.toString()

                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null

                // checking if the entered location is null or not.
                // on below line we are creating and initializing a geo coder.
                val geocoder = Geocoder(this@MapsActivity)
                try {
                    // on below line we are getting location from the
                    // location name and adding that location to address list.
                    addressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                // on below line we are getting the location
                // from our list a first position.
                val address: Address = addressList!![0]

                // on below line we are creating a variable for our location
                // where we will add our locations latitude and longitude.
                val latLng = LatLng(address.latitude, address.longitude)
                currentLatLng = latLng

                // on below line we are adding marker to that position.
                placeMarkerOnMap(latLng)

                // below line is to animate camera to that position.
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    //
    private fun viewData() {
        viewModel.getNilu(currentLatLng.latitude, currentLatLng.longitude, 20).observe(this@MapsActivity){ luftKvalitet ->
            val luftK = getLuftkvalitet(luftKvalitet)
            viewModel.getSunriseData(currentLatLng.latitude, currentLatLng.longitude).observe(this@MapsActivity){ sun ->
                val sun = getSunrise(sun)

                viewModel.getCompactForecast(currentLatLng.latitude, currentLatLng.longitude).observe(this@MapsActivity) { locationForecast ->
                    setForecastText(binding.text, locationForecast, luftK, sun)
                }
                binding.text.visibility = View.VISIBLE
            }
        }
    }

    private fun setForecastText(textView: TextView, data: CompactTimeSeriesData?, luftKvalitet: String, sunsetTime: String) {
        if (data == null){
            textView.text = "Kunne ikke hente data"
            return
        }
        val outText = "Nå (${data.time}):\n" +
                "Temperatur: ${data.temperature}, Skydekke: ${data.cloudCover}, Vindhastighet: ${data.wind_speed}\n" +
                "Precipation neste 6 timene: ${data.precipitation6Hours}\n" +
                "SymbolSummary neste 12 timer: ${data.summary12Hour}\n" +
                "Luftkvalitet: ${luftKvalitet}\n" +
                "Solnedgang: $sunsetTime"

        textView.text = outText
    }

    private fun getLuftkvalitet(liste : List<LuftKvalitet>): String{
        val her = createLocation(currentLatLng.latitude, currentLatLng.longitude)
        var theOne: LuftKvalitet? = null
        var smallestDistance = 100000.0.toFloat()
        liste.forEach {
            val location = it.latitude?.let { it1 -> it.longitude?.let { it2 ->
                createLocation(it1,
                    it2
                )
            } }
            val done = her.distanceTo(location)
            if (done < smallestDistance) {
                smallestDistance = done
                theOne = it
            }
        }
        if (theOne == null) {
            return "Fant ikke luftkvalitet"
        }
        return theOne?.value.toString()
    }

    //Hjelpemetode for å lage et location object
    private fun createLocation(latitude : Double, longitude : Double) : Location{
        val her = Location("")
        her.latitude = latitude
        her.longitude = longitude
        return her
    }

    private fun getSunrise(data: CompactSunriseData): String {
        var sunsetTime : String? = null
        if (data != null) {
            sunsetTime = data.sunsetTime
        }
        if (sunsetTime == null) {
            return "Fant ikke solnedgang"
        }
        return sunsetTime
    }
}



