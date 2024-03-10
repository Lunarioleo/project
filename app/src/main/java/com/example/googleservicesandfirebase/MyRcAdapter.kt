package com.example.googleservicesandfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyRcAdapter(val data: List<Photos>) : RecyclerView.Adapter<RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val listItemView = LayoutInflater.from(parent.context).inflate(R.layout.rc_item, parent, false)
        return RecyclerViewHolder(listItemView)
    }


    override fun getItemCount(): Int  = data.count()



    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val reference = data[position].photoReference
//        val request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photo_reference=$reference&key=AIzaSyAbZFMlKGcXK9E1yn_BzwPh-m2Kdle3Ky4"
//            Picasso.get()
//                .load(request).into(holder.rcImage)
    }
}

class RecyclerViewHolder(item: View) : RecyclerView.ViewHolder(item){
    val rcImage = item.findViewById<ImageView>(R.id.rcImage)
}
