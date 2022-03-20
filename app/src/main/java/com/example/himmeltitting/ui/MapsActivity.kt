package com.example.himmeltitting.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.himmeltitting.R
import com.example.himmeltitting.databinding.ActivityMapsBinding
import com.example.himmeltitting.main_data_source.ObservationAdapter
import com.example.himmeltitting.nav_fragments.FavoritesFragment
import com.example.himmeltitting.nav_fragments.InfoFragment
import com.example.himmeltitting.nav_fragments.SettingsFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, DateSelected{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var currentLatLng: LatLng
    private var marker: Marker? = null
    private val mainViewModel: MainActivityViewModel by viewModels()
    private lateinit var recyclerView : RecyclerView

    //fused location privider gets last location
    private lateinit var fusedLocationClient : FusedLocationProviderClient


    // bruker i permission sjekk
    companion object{
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initDatePicker()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val favoritesFrafment = FavoritesFragment()
        val settingsFragment = SettingsFragment()
        val infoFragment = InfoFragment()

        //legge til egen metode
        binding.bottomNavigation.setOnItemReselectedListener { item ->
            when(item.itemId){
                R.id.search -> startMapsActivity()
                R.id.fav -> makeCuurentFragment(favoritesFrafment)
                R.id.info -> makeCuurentFragment(infoFragment)
                R.id.settings -> makeCuurentFragment(settingsFragment)
            }
            true
        }
    }

    // initializes fragments from navigation bar
    private fun makeCuurentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    // starts main activity
    private fun startMapsActivity(){
        val intent = Intent (this, MapsActivity::class.java )
        startActivity(intent)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        initRecyclerView()
        createData()
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


                // zoom effekt som skjer når lokasjon blir funnet
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                mainViewModel.loadObservations(currentLatLng.latitude, currentLatLng.longitude, 50)
            }
        }
    }



    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        marker?.remove()
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        marker = mMap.addMarker(markerOptions)
        marker?.showInfoWindow()
        mainViewModel.loadObservations(currentLatLng.latitude, currentLatLng.longitude, 10)

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
                // location name from search view.
                val location: String = searchView.query.toString()
                // where we will store the list of all address.
                var addressList: List<Address>? = null
                // on below line we are creating and initializing a geo coder.
                val geocoder = Geocoder(this@MapsActivity)
                try {
                    // location name and adding that location to address list.
                    addressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                // from our list a first position.
                val address: Address = addressList!![0]

                // where we will add our locations latitude and longitude.
                val latLng = LatLng(address.latitude, address.longitude)
                currentLatLng = latLng

               //adding marker to that position.
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



    private fun initRecyclerView(){
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

    }


    private fun createData(){
        mainViewModel.getObservations().observe(this){ observationList ->
            recyclerView.adapter = observationList?.let{ ObservationAdapter(this,it) }

        }
    }

    private fun initDatePicker(){
        binding.datePicker.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val datePickerFragment = DatePickerFragment(this)
        datePickerFragment.show(getSupportFragmentManager(), "datePicker")

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = GregorianCalendar()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        val viewFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("dd-MMM-YYYY")
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        var viewFormattedDate = viewFormatter.format(calendar.getTime())
        binding.datePicker.setText(viewFormattedDate)

    }


}



