package com.example.mobileprojectfinal.core

import android.content.Context
import android.content.SharedPreferences

object LocationHelper {

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("com.example.mobileprojectfinal", Context.MODE_PRIVATE)
    }

    fun setLocation(latitude: Float, longitude: Float) {
        val editor = prefs!!.edit()
        editor.putFloat("latitude", latitude)
        editor.putFloat("longitude", longitude)
        editor.apply()
    }

    fun getLocationAndClear(): Pair<Float, Float> {
        val latitude = prefs!!.getFloat("latitude", 0f)
        val longitude = prefs!!.getFloat("longitude", 0f)

        val editor = prefs!!.edit()
        editor.putFloat("latitude", 0f)
        editor.putFloat("longitude", 0f)
        editor.apply()

        return Pair(latitude, longitude)
    }

    fun setPinLocation(latitude: Float, longitude: Float) {
        val editor = prefs!!.edit()
        editor.putFloat("latitudePin", latitude)
        editor.putFloat("longitudePin", longitude)
        editor.apply()
    }

    fun getPinLocation(): Pair<Float, Float> {
        val latitude = prefs!!.getFloat("latitudePin", 0f)
        val longitude = prefs!!.getFloat("longitudePin", 0f)

        return Pair(latitude, longitude)

    }
}