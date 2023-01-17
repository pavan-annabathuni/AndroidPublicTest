package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.RangesEntity
import com.waycool.data.Local.Entity.ViewDeviceEntity
import com.waycool.data.Local.mappers.ViewDeviceEntityMapper
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.RangesData
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.repository.domainModels.RangesDomain
import com.waycool.data.repository.domainModels.ViewDeviceDomain
import com.waycool.data.repository.util.DomainMapper

class ViewDeviceDomainMapper:DomainMapper<ViewDeviceDomain,ViewDeviceEntity> {
    override fun mapToDomain(dto: ViewDeviceEntity): ViewDeviceDomain {
        return ViewDeviceDomain(
            id = dto.id,
            dataTimestamp = dto.dataTimestamp,
            temperature = dto.temperature,
            humidity = dto.humidity,
            pressure = dto.pressure,
            rainfall = dto.rainfall,
            windspeed = dto.windspeed,
            soilMoisture1 = dto.soilMoisture1,
            soilMoisture2 = dto.soilMoisture2,
            leafWetness = dto.leafWetness,
            soilTemperature1 = dto.soilTemperature1,
            lux = dto.lux,
            modelId = dto.modelId,
            serialNoId = dto.serialNoId,
            iotDevicesDataId = dto.iotDevicesDataId,
            farmId = dto.farmId,
            deviceName = dto.deviceName,
            deltaT = dto.deltaT,
            deltaTRange = RangeDomainMapper().mapToDomain(dto.deltaTRange?: RangesEntity()),
            soilMoisture1Range = RangeDomainMapper().mapToDomain(dto.soilMoisture1Range?: RangesEntity()),
            soilMoisture2Range = RangeDomainMapper().mapToDomain(dto.soilMoisture2Range?: RangesEntity()),
            battery = dto.battery,
            modelName = dto.modelName,
            modelSeries = dto.modelSeries,
            deviceNumber = dto.deviceNumber,
            isApproved = dto.isApproved
        )
    }

    fun toDomainList(initial: List<ViewDeviceEntity>): List<ViewDeviceDomain> {
        return initial.map { mapToDomain(it) }
    }

    class RangeDomainMapper : DomainMapper<RangesDomain, RangesEntity> {

        override fun mapToDomain(dto: RangesEntity): RangesDomain {
           return RangesDomain(
               min = dto.min,
               max = dto.max,
               ranges = dto.ranges
           )
        }

    }
}