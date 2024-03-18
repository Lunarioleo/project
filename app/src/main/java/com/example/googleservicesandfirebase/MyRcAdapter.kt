package com.example.googleservicesandfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso

class MyRcAdapter(var result: ArrayList<Results>, private val listener: (String, String, Float, LatLng) -> Unit) :
    RecyclerView.Adapter<RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val listItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.rc_item, parent, false)
        return RecyclerViewHolder(listItemView)
    }


    //data.size
    override fun getItemCount(): Int = result.size - 1


    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = result[position]
        val name = item.name
        val rate = item.rating
        val latLng = LatLng(item.geometry.location.lat, item.geometry.location.lng)
        //val photoReference = item.photos[0].photoReference!!
        if (!item.photos.isNullOrEmpty()) {
            val photoReference = item.photos[0].photoReference!!
            val request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photo_reference=$photoReference&key=AIzaSyB2qkthllFpLegExs4u-87BGJq3aFaEHFc"
            Picasso.get().load(request).into(holder.rcImage)
            holder.itemView.setOnClickListener { listener(name, photoReference, rate, latLng) }
        }

    }

    fun updateData(newResults: ArrayList<Results>) {
        result.clear()
        result.addAll(newResults)
        notifyDataSetChanged()
    }
}

class RecyclerViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    val rcImage: ImageView = item.findViewById(R.id.rcImage)
}
