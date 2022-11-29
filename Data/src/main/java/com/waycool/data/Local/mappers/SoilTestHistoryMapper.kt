package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.SoilTestHistoryEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.SoilTestData

class SoilTestHistoryMapper : EntityMapper<SoilTestHistoryEntity,SoilTestData> {
    override fun mapToEntity(dto: SoilTestData): SoilTestHistoryEntity {
        return SoilTestHistoryEntity(
            id = dto.id,
            plot_no = dto.plotNo,
            soil_test_number = dto.soilTestNumber,
            status = dto.status,
            updated_at = dto.updatedAt,
            approve_status = dto.approveStatus


        )
    }
    fun toEntityList(initial: List<SoilTestData>): List<SoilTestHistoryEntity> {
        return initial.map {
            mapToEntity(it)
        }
    }
}