package com.example.himmeltitting

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.himmeltitting.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // det vanlige
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private var marker: Marker? = null;

    //fused location privider er en api som brukes til å få siste kjente lokasjon.
    // Den er vist veldig bra å bruke, står mer om det her https://developer.android.com/training/location/retrieve-current
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    // variabler for koordinater
    lateinit var latitude: String
    lateinit var longitude: String


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
        setUpMap()
        addOnMapClickListener()
        addSearchView()
    }



    private fun setUpMap() {
        // sjekker permissions fra brukeren
        if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }

        // får lokasjon, setter markøren på kartet
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if(location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)

                // jeg tar vare på disse i tilfellet vil bruke senere
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()

                // egendefinert metode
                placeMarkerOnMap(currentLatLong)

                // zoom effekt som skjer når lokasjon blir funnet
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }
    }



    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        marker?.remove()
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        marker = mMap.addMarker(markerOptions)
        marker?.showInfoWindow()
    }



    override fun onMarkerClick(p0: Marker)= false


    private fun addOnMapClickListener(){
        // gjør at vi kan klikke på et sted og få koordinater
        mMap.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng :LatLng) {
                val location = LatLng(latlng.latitude,latlng.longitude)
                placeMarkerOnMap(location)
                // kan velge hvor mye vi vil zoome inn
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 50f))

            }
        })
    }





    // copy paste fra https://www.geeksforgeeks.org/how-to-add-searchview-in-google-maps-in-android/
    private fun addSearchView(){
        val searchView = binding.idSearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are getting the
                // location name from search view.
                val location: String = searchView.getQuery().toString()

                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null

                // checking if the entered location is null or not.
                if (location != null || location == "") {
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
                    val latLng = LatLng(address.getLatitude(), address.getLongitude())

                    // on below line we are adding marker to that position.
                    placeMarkerOnMap(latLng)

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}


