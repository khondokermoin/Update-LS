package com.example.locationsharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationsharing.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fireStoreViewModel: FireStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fireStoreViewModel = ViewModelProvider(this).get(FireStoreViewModel::class.java)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap

        fireStoreViewModel.getAllUser { userList->
            for(user in userList){
                val userLocation = user.location
                if(userLocation.isNotEmpty()){
                    val latLng = parseLocation(userLocation)
                    val markerOption = MarkerOptions().position(latLng).title("${user.displayName}\n${user.email}")
                    googleMap.addMarker(markerOption)
                }
            }
        }
    }

    private fun parseLocation(userLocation: String): LatLng {
        val latLngSplit = userLocation.split(",")
        val latitude = latLngSplit[0].substringAfter("Lat: ").toDouble()
        val longitude = latLngSplit[1].substringAfter("Long: ").toDouble()
        return LatLng(latitude, longitude)

    }
}