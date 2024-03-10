package com.example.googleservicesandfirebase

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/maps/api/directions/json?")
    suspend fun getSimpleRoute(
        @Query("origin") originId: String = "49.842957,24.031111",
        @Query("destination") destinationId: String,
        @Query("key") key: String = "AIzaSyB2qkthllFpLegExs4u-87BGJq3aFaEHFc"
    ): Response<DirectionsResponse>

    @GET("/maps/api/place/nearbysearch/json?location=49.842957,24.031111&radius=2000&type=tourist_attractions&key=AIzaSyB2qkthllFpLegExs4u-87BGJq3aFaEHFc")
    suspend fun getNearbyPlaces(): Response<PlacesResponse>

    @GET("/maps/api/directions/json?")
    suspend fun getComplexRoute(
        @Query("origin") originId: String,
        @Query("destination") destinationId: String,
        @Query("waypoints") waypoints: String,
        @Query("key") key: String = "AIzaSyB2qkthllFpLegExs4u-87BGJq3aFaEHFc"
    ): Response<DirectionsResponse>


}
