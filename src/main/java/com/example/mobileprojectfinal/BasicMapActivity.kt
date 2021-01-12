package com.example.mobileprojectfinal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileprojectfinal.core.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class BasicMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val pinLocation = LocationHelper.getPinLocation()
        val position = LatLng(pinLocation.first.toDouble(), pinLocation.second.toDouble())
        mMap.addMarker(MarkerOptions().position(position).title("Marker"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
    }
}