package com.waycool.data.Network.ApiInterface

import com.waycool.data.Network.NetworkModels.GeocodeDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApiInterface {


    @GET("maps/api/geocode/json")
    suspend fun getReverseGeocode(
        @Query("latlng") latlng: String, @Query("key") key: String
    ): Response<GeocodeDTO>


    @GET("maps/api/geocode/json")
    suspend fun getGeocode(
        @Query("address") address: String, @Query("key") key: String
    ): Response<GeocodeDTO>


}