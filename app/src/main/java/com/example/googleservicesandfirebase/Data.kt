package com.example.googleservicesandfirebase

import com.google.gson.annotations.SerializedName
import java.nio.file.Path

data class DirectionsResponse(val routes:List<Routes>)

data class Routes(@SerializedName("overview_polyline") val overviewPolyline:OverviewPolyline, val legs: List<Leg>)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    @SerializedName("end_address")
    val endAddress: String,
    @SerializedName("start_address")
    val startAddress: String,
    val steps: List<Leg>,
)
data class Distance(
    val text: String,
)

data class Duration(
    val text: String,
)



data class OverviewPolyline(val points:String)





data class PlacesResponse(val results:ArrayList<Results>)

data class Results(val geometry:Geometry, val photos:ArrayList<Photos>, val name:String, val rating: Float)

data class Geometry(val location:Location)

data class Location(val lat:Double, val lng:Double)

data class Photos (@SerializedName("photo_reference") val photoReference:String?)
