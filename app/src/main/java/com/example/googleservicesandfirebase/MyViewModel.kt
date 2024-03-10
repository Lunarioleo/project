package com.example.googleservicesandfirebase


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.NullPointerException


class MyViewModel: ViewModel(){
    private val _state = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _state
    private val repo = MyApp.getInstance().repo







    fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            val nearbyPlaces = async { repo.getNearbyPlaces() }.await()
            val locations = mutableListOf<Location>()
            nearbyPlaces.body()?.let {
                it.results.forEach { result ->
                    val location = result.geometry.location
                    locations.add(location)
                    _state.postValue(UiState.ResultLocations(locations))
                }
            }

        }
    }

    fun getRoute(m:Marker){
        viewModelScope.launch {
            val route = async { repo.getSimpleRoute(m)}.await()
            route.body()?.let {
                val polylinePoints = it.routes[0].overviewPolyline.points
                val decodedPath = PolyUtil.decode(polylinePoints)
                _state.postValue(UiState.ResultSimpleRoute(decodedPath))
            }
        }
    }



    sealed class UiState {
        //class Processing (val msg: String) : UiState()
        class ResultSimpleRoute(val routePath: MutableList<LatLng>): UiState()
        class ResultLocations(val locations: MutableList<Location>) : UiState()
        //class Crash(val msg: String): UiState()
    }




}