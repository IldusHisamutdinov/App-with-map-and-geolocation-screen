package com.example.mapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.mapapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException
import java.text.DecimalFormat

class MapActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private var currentMarker: Marker? = null
    private var mMap: GoogleMap? = null
    var adress: String? = null
    val newList = ArrayList<com.example.mapapp.Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        requestPemissions()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setOnMapLongClickListener { latLng ->
            addMarker(latLng)
            geocoder(latLng)
        }
    }

    // Добавление меток на карту
    private fun addMarker(location: LatLng): Marker {
        val title =
            java.lang.Double.toString(location.latitude) + "," + java.lang.Double.toString(location.longitude)
        val marker = mMap!!.addMarker(
            MarkerOptions()
                .position(location)
                .title(title)
        )
        mMap!!.addCircle(
            CircleOptions()
                .center(location)
                .radius(55000.0)
                .strokeColor(Color.BLUE)
        )
        return marker
    }

    //получение адреса и передача в список MarkerActivity
    fun geocoder(location: LatLng) {
        // 3 знака после запятой
        val df = DecimalFormat()
        df.setMaximumFractionDigits(3)
        val latit: Double = df.format(location.latitude).toDouble()
        val lontit: Double = df.format(location.longitude).toDouble()

        val geocoder = Geocoder(this)
        Thread {
            try {
                val addresses: List<Address> =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                textAddress.post(Runnable { textAddress.setText(addresses[0].getAddressLine(0)) })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
        // задержка для получения адреса
        Handler(Looper.getMainLooper()).postDelayed({
            adress = textAddress.text.toString()
            newList.add(Marker(latit.toString(), lontit.toString(), adress))
        }, 1000)
        markerShow()
    }

    // Запрос координат
    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val geocoder = Geocoder(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        val provider = locationManager.getBestProvider(criteria, true)
        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 10000, 10f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val df = DecimalFormat()
                    df.setMaximumFractionDigits(3)
                    val lat: Double = df.format(location.latitude).toDouble()
                    val lng: Double = df.format(location.longitude).toDouble()
                    val currentPosition = LatLng(lat, lng)
                    Thread {
                        try {
                            val addresses: List<Address> =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            textAddress.post(Runnable {
                                textAddress.setText(
                                    addresses[0].getAddressLine(
                                        0
                                    )
                                )
                            })
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }.start()
                    // задержка для получения адреса
                    Handler(Looper.getMainLooper()).postDelayed({
                        adress = textAddress.text.toString()
                        newList.add(Marker(lat.toString(), lng.toString(), adress))
                    }, 1000)
                    markerShow()
                    currentMarker = mMap!!.addMarker(
                        MarkerOptions()
                            .position(currentPosition)
                            .title("Текущая позиция")
                    )
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentPosition,
                            5.toFloat()
                        )
                    )
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            })
        }
    }

    // Запрос пермиссий
    private fun requestPemissions() {
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocation()
        } else {
            requestLocationPermissions()
        }
    }

    // Запрос пермиссии для геолокации
    private fun requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            || !ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    // Это результат запроса у пользователя пермиссии
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size == 2 &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)
            ) {
                requestLocation()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 10
    }

    fun markerShow() {
        binding.back.setOnClickListener {
            val intent = Intent(this, MarkerActivity::class.java)
            intent.putParcelableArrayListExtra("markerList", newList)
            startActivity(intent)
        }
    }
}

