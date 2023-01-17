package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.RangesEntity
import com.waycool.data.Local.Entity.VansCategoryEntity
import com.waycool.data.Local.Entity.ViewDeviceEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.RangesData
import com.waycool.data.Network.NetworkModels.VansCategoryData
import com.waycool.data.Network.NetworkModels.ViewDeviceDTO
import com.waycool.data.Network.NetworkModels.ViewDeviceData

class ViewDeviceEntityMapper : EntityMapper<ViewDeviceEntity, ViewDeviceData> {
    override fun mapToEntity(dto: ViewDeviceData): ViewDeviceEntity {
        return ViewDeviceEntity(
            id = dto.id,
            dataTimestamp = dto.dataTimestamp,
            temperature = dto.temperature,
            humidity = dto.humidity,
            pressure = dto.pressure,
            rainfall = dto.rainfall,
            windspeed = dto.windspeed,
            soilMoisture1 = dto.soilMoisture1Kpa,
            soilMoisture2 = dto.soilMoisture2Kpa,
            leafWetness = dto.leafWetness,
            soilTemperature1 = dto.soilTemperature1,
            lux = dto.lux,
            modelId = dto.modelId,
            serialNoId = dto.serialNoId,
            iotDevicesDataId = dto.iotDevicesDataId,
            farmId = dto.farmId,
            deviceName = dto.deviceName,
            deltaT = dto.deltaT,
            deltaTRange = RangeEntityMapper().mapToEntity(dto.deltaTRange?: RangesData()),
            soilMoisture1Range = RangeEntityMapper().mapToEntity(dto.soilMoisture1Range?:RangesData()),
            soilMoisture2Range = RangeEntityMapper().mapToEntity(dto.soilMoisture2Range?:RangesData()),
            battery = dto.iotDevicesData?.battery,
            modelName = dto.model?.modelName,
            modelSeries = dto.model?.series,
            deviceNumber = dto.deviceNumber,
            isApproved = dto.isApproved
        )
    }

    fun toEntityList(initial: List<ViewDeviceData>): List<ViewDeviceEntity> {
        return initial.map { mapToEntity(it) }
    }

    class RangeEntityMapper : EntityMapper<RangesEntity, RangesData> {
        override fun mapToEntity(dto: RangesData): RangesEntity {
            return RangesEntity(
                min = dto.min,
                max = dto.max,
                ranges = dto.ranges
            )
        }

    }
}