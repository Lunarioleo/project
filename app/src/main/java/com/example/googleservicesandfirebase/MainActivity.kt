package com.example.googleservicesandfirebase



import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.googleservicesandfirebase.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
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
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

//Сделать обновление локации постоянное ++
// Вывести реал тайм маршрут(куда идти) ++
//Более менее сделать внешний вид ++
// Приближение маркера по клику на картинку Recycler ++
// пофиксить вылет приложения при запросе доступа к геопозиции ??


//Я не встиг зробити все красиво, якщо можливо то фінальна версія буте трохи відрізнятись від поточної. Вибачаюсь
class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {
    private var clickedMarker: Marker? = null
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var viewModel: MyViewModel

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        instance = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val distanceHint = findViewById<TextView>(R.id.distanceHint)
        val timeHint = findViewById<TextView>(R.id.timeHint)
        var polyline: Polyline? = null
        val time = findViewById<TextView>(R.id.time)
        val distance = findViewById<TextView>(R.id.distance)
        val startAdress = findViewById<TextView>(R.id.startAdress)
        val endAdress = findViewById<TextView>(R.id.endAdress)
        val image = findViewById<FloatingActionButton>(R.id.imageView)
        val markerAdress = findViewById<TextView>(R.id.destinName)
        val rcView = findViewById<RecyclerView>(R.id.rcView)
        val f = findViewById<FrameLayout>(R.id.sheet)
        val animation = findViewById<LottieAnimationView>(R.id.animLoading)


        viewModel = ViewModelProvider(this@MainActivity)[MyViewModel::class.java]

        BottomSheetBehavior.from(f).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        animation.visibility = View.INVISIBLE

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
         supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.container) as SupportMapFragment


        viewModel.checkPermissions()
        startLocationUpdates()


        val adapter = MyRcAdapter(ArrayList()) { name, item, rate, latLtn ->
            viewModel.showCustomDialog(item, name, rate)
            supportMapFragment.getMapAsync {
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLtn, 20F))
            }
        }
        rcView.adapter = adapter



        image.setOnClickListener {
        supportMapFragment.getMapAsync {map->

            fetchLocation().addOnCompleteListener {
                val currentCoordinates = "${it.result.latitude},${it.result.longitude}"
                viewModel.getData(currentCoordinates)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.result.latitude, it.result.longitude), 14F))
            }


        viewModel.uiState.observe(this@MainActivity){
            when (it){
                is MyViewModel.UiState.ResultLocations->{
                    viewModel.showAllViewsInContainer(f)
                    animation.visibility = View.INVISIBLE
                    it.locations.forEach { location ->
                        val coordinates = LatLng(location.lat, location.lng)
                        map.addMarker(MarkerOptions().position(coordinates))!!
                    }
                }
                is MyViewModel.UiState.AdapterResult -> {
                    viewModel.showAllViewsInContainer(f)
                    animation.visibility = View.INVISIBLE
                        markerAdress.text = it.full.results[0].name
                        adapter.updateData(it.full.results)
                }
                is MyViewModel.UiState.ResultSimpleRoute -> {
                    viewModel.showAllViewsInContainer(f)
                    animation.visibility = View.INVISIBLE
                    polyline?.remove()
                    polyline = map.addPolyline(PolylineOptions().addAll(it.routePath))
                    polyline?.color = Color.CYAN
                    distance.text = "Distance: ${it.res.routes[0].legs[0].distance.text}"
                    startAdress.text = it.res.routes[0].legs[0].startAddress
                    endAdress.text = it.res.routes[0].legs[0].endAddress
                    time.text = it.res.routes[0].legs[0].duration.text
                    distanceHint.text = it.res.routes[0].legs[0].steps[0].distance.text
                    timeHint.text = it.res.routes[0].legs[0].steps[0].duration.text
                    // works on marker click
                }

                MyViewModel.UiState.Processing -> {
                    viewModel.hideAllViewsInContainer(f, animation)
                    viewModel.loadingAnimation(animation)
                }
            }
        }
        }
    } }





     fun fetchLocation(): Task<Location> {
        val task = fusedLocationProviderClient.lastLocation
         fusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }
        return task
    }


    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                var currentCoordinates = "${location.latitude},${location.longitude}"
                supportMapFragment.getMapAsync { map ->

                    onMapReady(map)
                    map.setOnMarkerClickListener { marker ->

                        clickedMarker = marker
                        false
                    }
                    clickedMarker?.let { viewModel.getRoute(it, currentCoordinates) }

                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
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
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

