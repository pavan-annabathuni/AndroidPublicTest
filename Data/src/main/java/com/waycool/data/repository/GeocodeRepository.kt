package com.waycool.data.repository
import com.waycool.data.Network.NetworkModels.GeocodeDTO
import com.waycool.data.Network.NetworkSource
import com.waycool.data.repository.DomainMapper.GeocodeDomainMapper
import com.waycool.data.repository.domainModels.GeocodeDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object GeocodeRepository {

    fun getGeocode(address:String):Flow<GeocodeDomain>{
        return NetworkSource.getGeocode(address).map {
            GeocodeDomainMapper().mapToDomain(it?: GeocodeDTO())
        }
    }

    fun getReverseGeocode(latlon:String):Flow<GeocodeDomain>{
        return NetworkSource.getReverseGeocode(latlon).map {
            GeocodeDomainMapper().mapToDomain(it?: GeocodeDTO())
        }
    }
}