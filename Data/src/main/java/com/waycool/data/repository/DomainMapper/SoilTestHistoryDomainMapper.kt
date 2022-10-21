package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Local.Entity.AddCropTypeEntity
import com.waycool.data.Local.Entity.SoilTestHistoryEntity
import com.waycool.data.Repository.DomainModels.AddCropTypeDomain
import com.waycool.data.Repository.DomainModels.SoilTestHistoryDomain
import com.waycool.data.Repository.util.DomainMapper

class SoilTestHistoryDomainMapper : DomainMapper<SoilTestHistoryDomain, SoilTestHistoryEntity> {
    override fun mapToDomain(dto: SoilTestHistoryEntity): SoilTestHistoryDomain {
        return SoilTestHistoryDomain(
            id = dto.id,
            plot_no = dto.plot_no,
            soil_test_number = dto.soil_test_number,
            status = dto.status,
            updated_at = dto.updated_at
        )
    }
    fun toDomainList(initial: List<SoilTestHistoryEntity>): List<SoilTestHistoryDomain> {
        return initial.map {
            mapToDomain(it)
        }
    }

}
