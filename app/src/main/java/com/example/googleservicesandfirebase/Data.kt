package com.example.googleservicesandfirebase

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(val routes:List<Routes>)

data class Routes(@SerializedName("overview_polyline") val overviewPolyline:OverviewPolyline)

data class OverviewPolyline(val points:String)

data class PlacesResponse(val results:ArrayList<Results>)

data class Results(val geometry:Geometry, val photos:ArrayList<Photos>, val name:String, val rating: Float)

data class Geometry(val location:Location)

data class Location(val lat:Double, val lng:Double)

data class Photos (@SerializedName("photo_reference") val photoReference:String?)
