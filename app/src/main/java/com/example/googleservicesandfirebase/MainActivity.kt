package com.example.googleservicesandfirebase



import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior




class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val f = findViewById<FrameLayout>(R.id.sheet)




        BottomSheetBehavior.from(f).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }


//        Picasso.get()
//            .load("https://binomen.ru/photo/uploads/posts/2023-11/1700844732_binomen-ru-p-litofiti-ostrova-vrangelya-krasivo-2.jpg")
//            .into(image)

        val viewModel = ViewModelProvider(this)[MyViewModel::class.java]

            val supportMapFragment = supportFragmentManager.findFragmentById(R.id.container) as SupportMapFragment
            supportMapFragment.getMapAsync { map ->

            onMapReady(map)

            map.setOnMarkerClickListener { marker ->
                Toast.makeText(this, "${marker.position}", Toast.LENGTH_SHORT).show()
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 8F))
                viewModel.getRoute(marker)
                false
            }




               viewModel.getData()
                    viewModel.uiState.observe(this) {
                        when (it) {
                            is MyViewModel.UiState.ResultLocations -> {
//                                val adapter = MyRcAdapter(it.photos)
//                                rcView.adapter = adapter
//                                     Toast.makeText(this@MainActivity, reference.length, Toast.LENGTH_SHORT).show()

//                                val request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photo_reference=$reference&key=AIzaSyB2qkthllFpLegExs4u-87BGJq3aFaEHFc"
//                                Log.e("MyLog", reference)
                                it.locations.forEach {location->
                            val coordinates = LatLng(location.lat, location.lng)
                            map.addMarker(MarkerOptions().position(coordinates))!!
                                }
                            }
                            is MyViewModel.UiState.ResultSimpleRoute -> {
                                map.addPolyline(PolylineOptions().addAll(it.routePath))
                            }
                        }
                    }


                // val a = map.addMarker(MarkerOptions().position(coordinates_Lviv))

//            CoroutineScope(Dispatchers.IO).launch {
//                val result = Client.client.create(ApiInterface::class.java).getNearbyPlaces()
//                if (result.isSuccessful) {
//                    var locations = mutableListOf<Location>()
//                    result.body()?.let {
//                        it.results.forEach { result ->
//                            val location = result.geometry.location
//                            locations.add(location)
//                        }
//                    }
//                    withContext(Dispatchers.Main) {
//                        locations.forEach {
//                            val coordinates = LatLng(it.lat, it.lng)
//                            val b = map.addMarker(MarkerOptions().position(coordinates))
//                        }
//                    }
//                }
//            }



               // withContext(Dispatchers.IO) {
                //val placeCoordinates = mutableListOf<String>()
//                    result.body()?.let { result ->
//                        result.results.forEach {
//                            placeCoordinates.add("${it.geometry.location.lat},${it.geometry.location.lng}")
//                        }
//                    }



//                    map!!.setOnMarkerClickListener {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            val routeResult = Client.client.create(ApiInterface::class.java).getSimpleRoute(destinationId = "${it.position.latitude} ${it.position.longitude}")
//                            if (routeResult.isSuccessful) {
//
//                            routeResult.body()?.let {
//                                val polylinePoints = it.routes[0].overviewPolyline.points
//                                val decodedPath = PolyUtil.decode(polylinePoints)
//                                map.addPolyline(PolylineOptions().addAll(decodedPath))
//                            }
//
//                    }
//                        }
//                        false
//                    }
//                    if (routeResult.isSuccessful) {
//                        withContext(Dispatchers.Main) {
//                            routeResult.body()?.let {
//                                val polylinePoints = it.routes[0].overviewPolyline.points
//                                val decodedPath = PolyUtil.decode(polylinePoints)
//                                map.addPolyline(PolylineOptions().addAll(decodedPath))
//                            }
//                        }
//                    }
//                }

               // }

        }


    }

        override fun onMapReady(googleMap: GoogleMap) {
            try {
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map))
            } catch (e: NullPointerException ) {
                Log.e("Mylog", "Cant find style.")
            }
        }

        override fun onMarkerClick(p0: Marker): Boolean {
            Toast.makeText(this, p0.title, Toast.LENGTH_SHORT).show()
            return false
        }
    }







//            CoroutineScope(Dispatchers.IO).launch {
//                val result = Client.client.create(ApiInterface::class.java).getNearbyPlaces()
//                if (result.isSuccessful){
//                    var locations = mutableListOf<Location>()
//                    result.body()?.let {
//                        it.results.forEach {result ->
//                            val location = result.geometry.location
//                            locations.add(location)
//                        }
//                    }
//                    withContext(Dispatchers.Main){
//                        locations.forEach {
//                            val coordinates = LatLng(it.lat, it.lng)
//                            map.addMarker(MarkerOptions().position(coordinates))
//                        }
//                    }
//                }
//                withContext(Dispatchers.IO) {
//                    val placeCoordinates = mutableListOf<String>()
//                    result.body()?.let { result ->
//                        result.results.forEach {
//                            placeCoordinates.add("${it.geometry.location.lat},${it.geometry.location.lng}")
//                        }
//                    }
//                    val waypointCoordinates = placeCoordinates.drop(0).take(10)
//                    val waypointCoordinatesString =
//                        waypointCoordinates.joinToString(separator = "|")
//                    val routeResult = Client.client.create(ApiInterface::class.java)
//                        .getComplexRoute(
//                            originId = placeCoordinates[0],
//                            destinationId = placeCoordinates.last(),
//                            waypoints = waypointCoordinatesString
//                        )
//                    if (routeResult.isSuccessful) {
//                        withContext(Dispatchers.Main) {
//                            routeResult.body()?.let {
//                                val polylinePoints = it.routes[0].overviewPolyline.points
//                                val decodedPath = PolyUtil.decode(polylinePoints)
//                                map.addPolyline(PolylineOptions().addAll(decodedPath))
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
//    }
//}


//    override fun launch(intent: Intent) {
//       startActivityForResult(intent, 1)
//    }
//
//    override fun showListFragment() {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, ListFragment())
//                .commit()
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == 1){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//                try {
//                    val result = task.result
//                    val credential = GoogleAuthProvider.getCredential(result.idToken, null)
//                    val auth = Firebase.auth
//                    auth.signInWithCredential(credential)
//                        .addOnCompleteListener{
//                            if (it.isSuccessful){
//                                showListFragment()
//                            } else {
//                                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
//                                Log.e("myLog", "fail")
//
//                            }
//                        }
//                } catch (e: ApiException){
//                    Toast.makeText(this, "Error $e", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    override fun onFabClick() {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, AddEmployeeFragment())
//            .commit()
//    }



//interface OnAuthLaunch{
//    fun launch(intent: Intent)
//    fun showListFragment()
//
//}
//
//interface OnAddClick {
//    fun onFabClick()
//}