package com.example.googleservicesandfirebase

import android.app.Application

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApp: Application() {
    lateinit var repo: MyRepository

    override fun onCreate() {
        super.onCreate()
        instance = this
        repo = MyRepository(getApiClient())
    }

    private fun getApiClient(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    companion object {
        private lateinit var instance: MyApp
        fun getInstance() = instance
    }
}