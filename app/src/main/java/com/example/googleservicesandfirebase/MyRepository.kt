package com.example.googleservicesandfirebase


import com.google.android.gms.maps.model.Marker
import retrofit2.Response
import retrofit2.Retrofit

class MyRepository (private val client: Retrofit){
    private val apiInterface = client.create(ApiInterface::class.java)

    suspend fun getSimpleRoute(m:Marker, s:String): Response<DirectionsResponse> {
        return apiInterface.getSimpleRoute(originId = s,destinationId = "${m.position.latitude} ${m.position.longitude}")
    }

    suspend fun getNearbyPlaces(location: String): Response<PlacesResponse> {
        return apiInterface.getNearbyPlaces(location)
    }

}