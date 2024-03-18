package com.example.googleservicesandfirebase


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.PolyUtil
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.NullPointerException


class MyViewModel : ViewModel() {
    private val _state = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _state


    private val repo = MyApp.getInstance().repo
    val locations = mutableListOf<Location>()

    val name = mutableListOf<String>()
    val rating = mutableListOf<Float>()




    fun getData(location: String) {
        _state.postValue(UiState.Processing)
        CoroutineScope(Dispatchers.IO).launch {
            val nearbyPlaces = async { repo.getNearbyPlaces(location) }.await()
            _state.postValue(UiState.AdapterResult(nearbyPlaces.body()!!))
                nearbyPlaces.body()?.let {
                    it.results.forEach { result ->
                        rating.add(result.rating)
                        name.add(result.name)
                        val location = result.geometry.location
                        locations.add(location)

                    }
                    _state.postValue(UiState.ResultLocations(locations))
                }
            }
    }

    fun getRoute(m: Marker, s:String) {
        viewModelScope.launch{
            val route = async { repo.getSimpleRoute(m, s) }.await()
            route.body()?.let {
                val polylinePoints = it.routes[0].overviewPolyline.points
                val decodedPath = PolyUtil.decode(polylinePoints)
                _state.postValue(UiState.ResultSimpleRoute(decodedPath, it))
            }
        }
    }

     fun showCustomDialog(s: String, n: String, f: Float) {
        val dialog = Dialog(MainActivity.instance)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_layout)
        val rating = dialog.findViewById<TextView>(R.id.rating)
        val dialogImage = dialog.findViewById<ImageView>(R.id.dialogImage)
        val dialogText = dialog.findViewById<TextView>(R.id.vinicity)
        rating.text = f.toString()
        dialogText.text = n
        val request =
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=$s&key=AIzaSyB2qkthllFpLegExs4u-87BGJq3aFaEHFc"
        Picasso.get()
            .load(request).into(dialogImage)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancelBtn = dialog.findViewById<ImageView>(R.id.cancelBtn)
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

     fun checkPermissions(){
        if (ActivityCompat.checkSelfPermission(
                MainActivity.instance,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                MainActivity.instance,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                MainActivity.instance,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                MainActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }

    fun loadingAnimation(animation: LottieAnimationView){
        GlobalScope.launch(Dispatchers.Main) {
            animation.visibility = View.VISIBLE
            animation.playAnimation()
            animation.repeatMode = LottieDrawable.RESTART
        }
    }

    fun hideAllViewsInContainer(container: ViewGroup, viewToExclude: View) {
        for (i in 0 until container.childCount) {
            val childView = container.getChildAt(i)
            if (childView !== viewToExclude) {
                childView.visibility = View.GONE
            }
        }
    }

    fun showAllViewsInContainer(container: ViewGroup) {
        for (i in 0 until container.childCount) {
            val childView = container.getChildAt(i)
            childView.visibility = View.VISIBLE
        }
    }


    sealed class UiState {
        data object Processing : UiState()
        class AdapterResult (val full: PlacesResponse) : UiState()
        class ResultSimpleRoute(val routePath: MutableList<LatLng>, val res: DirectionsResponse ) : UiState()
        class ResultLocations(val locations: MutableList<Location>) : UiState()

    }
}