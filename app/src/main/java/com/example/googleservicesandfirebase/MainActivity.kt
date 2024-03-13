package com.example.googleservicesandfirebase



import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Я не встиг зробити все красиво, якщо можливо то фінальна версія буте трохи відрізнятись від поточної. Вибачаюсь
class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {
    private lateinit var currentLocation: LatLng
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        var polyline: Polyline? = null
        instance = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val markerAdress = findViewById<TextView>(R.id.markerAdress)
        val image = findViewById<ImageView>(R.id.imageView)
        val rcView = findViewById<RecyclerView>(R.id.rcView)
        val f = findViewById<FrameLayout>(R.id.sheet)

        BottomSheetBehavior.from(f).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        val viewModel = ViewModelProvider(this)[MyViewModel::class.java]








        image.setOnClickListener {
            fetchLocation().addOnCompleteListener {
                currentLocation = LatLng(it.result!!.latitude, it.result!!.longitude)
            }
        }




        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.container) as SupportMapFragment
         supportMapFragment.getMapAsync { map ->
             onMapReady(map)
             val adapter = MyRcAdapter(ArrayList()) { name, item, rate ->
                 viewModel.showCustomDialog(item, name, rate)
             }
             rcView.adapter = adapter

             image.setOnClickListener {
                 fetchLocation().addOnCompleteListener { loc ->
                     currentLocation = LatLng(loc.result!!.latitude, loc.result!!.longitude)
             val coordinates1 = currentLocation
             var currentCoordinates = "${coordinates1.latitude},${coordinates1.longitude}"

             Log.e("Loc", "${coordinates1.latitude},${coordinates1.longitude}")

             map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates1, 8F))


            map.setOnMarkerClickListener { marker ->
                Toast.makeText(this, "${marker.position}", Toast.LENGTH_SHORT).show()
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16F))
                    viewModel.getRoute(marker, currentCoordinates)
                //currentCoordinates = "${marker.position.latitude} ${marker.position.longitude}"
                false
            }
                     viewModel.getData(currentCoordinates)
                 }
             }


             map.addMarker(MarkerOptions().position(LatLng(49.842957,24.031111)))
                viewModel.uiState.observe(this@MainActivity) {
                    when (it) {
                        is MyViewModel.UiState.ResultLocations -> {
                            it.locations.forEach { location ->
                                val coordinates = LatLng(location.lat, location.lng)
                                 map.addMarker(MarkerOptions().position(coordinates))!!
                                map.addMarker(MarkerOptions().position(LatLng(49.842957,24.031111)))
                            }
                        }

                        is MyViewModel.UiState.ResultSimpleRoute -> {
                            polyline?.remove()
                            polyline =  map.addPolyline(PolylineOptions().addAll(it.routePath))
                            polyline?.color = Color.CYAN
                        }

                        is MyViewModel.UiState.ResultPhotos -> {
                    //            val a = it.photos
                      //          adapter.updateData(a, it.name, it.rating)
                        }

                        is MyViewModel.UiState.Processing -> {
                            markerAdress.text = it.full.results[0].name
                            adapter.updateData(it.full.results)
                        }
                    }
                }
            }
        }


    private fun fetchLocation(): Task<Location?> {
        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }
        return task
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map))
        } catch (e: NullPointerException) {
            Log.e("Mylog", "Cant find style.")
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        Toast.makeText(this, p0.title, Toast.LENGTH_SHORT).show()
        return false
    }



    companion object {
        lateinit var instance: MainActivity
    }
}