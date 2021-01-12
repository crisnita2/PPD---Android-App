package com.example.mobileprojectfinal

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.example.mobileprojectfinal.core.TAG
import androidx.annotation.RequiresApi
import com.example.mobileprojectfinal.core.LocationHelper
import com.example.mobileprojectfinal.core.Properties
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.InternalCoroutinesApi

class MainActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager

    @RequiresApi(Build.VERSION_CODES.M)
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        Log.i(TAG, "onCreate")
        connectivityManager = getSystemService(android.net.ConnectivityManager::class.java)

        Properties.instance.toastMessage.observe(
            this,
            { Toast.makeText(this, it, Toast.LENGTH_LONG).show() })

        LocationHelper.init(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStop() {
        super.onStop()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Properties.instance.internetActive.postValue(true)
            runOnUiThread {
                networkTxt.text = getString(R.string.active_network)
                networkIc.setImageResource(R.drawable.ic_active_network)
            }
        }

        override fun onLost(network: Network) {
            Properties.instance.internetActive.postValue(false)
            runOnUiThread {
                networkTxt.text = getString(R.string.inactive_network)
                networkIc.setImageResource(R.drawable.ic_inactive_network)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}