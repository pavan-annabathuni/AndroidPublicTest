package com.waycool.data.repository

import android.util.Log
import com.waycool.data.repository.DomainMapper.WeatherDomainMapper
import com.waycool.data.repository.domainModels.WeatherMasterDomain
import com.waycool.data.Sync.syncer.WeatherSyncer
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object WeatherRepository {

    fun getWeather(lat:String,lon:String,lang:String="en"): Flow<Resource<WeatherMasterDomain?>> {
        return WeatherSyncer().getData(lat, lon, lang).map {
            when (it) {
                is Resource.Success -> {
                    Log.d("Weather",it.data.toString())
                    Resource.Success(
                        it.data?.let { it1 -> WeatherDomainMapper().mapToDomain(it1) }
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }

}