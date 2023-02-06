package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.SoilTestHistoryEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.SoilTestData

class SoilTestHistoryMapper : EntityMapper<SoilTestHistoryEntity,SoilTestData> {
    override fun mapToEntity(dto: SoilTestData): SoilTestHistoryEntity {
        return SoilTestHistoryEntity(
            id = dto.id,
            plotNo = dto.plotNo,
            soilTestNumber = dto.soilTestNumber,
            status = dto.status,
            updatedAt = dto.updatedAt,
            approveStatus = dto.approveStatus


        )
    }
    fun toEntityList(initial: List<SoilTestData>): List<SoilTestHistoryEntity> {
        return initial.map {
            mapToEntity(it)
        }
    }
}