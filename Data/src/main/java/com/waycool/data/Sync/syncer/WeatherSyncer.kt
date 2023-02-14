package com.waycool.data.Sync.syncer

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.WeatherMasterEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.WeatherEntityMapper
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncInterface
import com.waycool.data.Sync.SyncKey
import com.waycool.data.Sync.SyncManager
import com.waycool.data.Sync.SyncRate
import com.waycool.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WeatherSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.WEATHER

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getSyncKeyForLatLon(lat: String, lon: String): Preferences.Key<String>{
        return SyncKey.createDynamicSyncKey("weather","${lat}_${lon}")
    }

    fun getData(
        lat: String,
        lon: String,
        lang: String = "en"
    ): Flow<Resource<WeatherMasterEntity?>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (SyncManager.shouldSync(getSyncKeyForLatLon(lat, lon), getRefreshRate())) {
                makeNetworkCall(lat, lon, lang)
            }
        }
        return getDataFromLocal(lat, lon)
    }

    private fun getDataFromLocal(lat: String, lon: String): Flow<Resource<WeatherMasterEntity?>> {
        return LocalSource.getWeather(lat, lon)?.map {
            Log.d("Weather-local", it.toString())
            Resource.Success(it)
        } ?: emptyFlow()
    }

    private suspend fun makeNetworkCall(lat: String, lon: String, lang: String = "en") {

        NetworkSource.getWeatherForecast(lat, lon, lang)
            .collect {
                when (it) {
                    is Resource.Success -> {
                        Log.d("Weather-network", it.data.toString())
                        LocalSource.insertWeatherData(
                            WeatherEntityMapper().mapToEntity(it.data!!),
                            lat,
                            lon
                        )
                        SyncManager.syncSuccess(getSyncKeyForLatLon(lat, lon))
                    }

                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {
                        SyncManager.syncFailure(getSyncKeyForLatLon(lat, lon))
                    }
                }
            }

    }
}