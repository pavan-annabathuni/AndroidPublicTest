package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.MyFarmsEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.MyFarmsNetwork

class MyFarmsEntityMapper : EntityMapper<MyFarmsEntity, MyFarmsNetwork> {
    override fun mapToEntity(dto: MyFarmsNetwork): MyFarmsEntity {
        return MyFarmsEntity(
            id = dto.id,
            farmName = dto.farmName,
            farmCenter = dto.farmCenter,
            farmArea = dto.farmArea,
            farmJson = dto.farmJson,
            farmWaterSource = dto.farmWaterSource,
            farmPumpHp = dto.farmPumpHp,
            farmPumpType = dto.farmPumpType,
            farmPumpDepth = dto.farmPumpDepth,
            farmPumpPipeSize = dto.farmPumpPipeSize,
            farmPumpFlowRate = dto.farmPumpFlowRate,
            isPrimary = dto.isPrimary,
            updatedAt = dto.updatedAt,
            accountNoId = dto.accountNoId
        )
    }

    fun toEntityList(initial: List<MyFarmsNetwork>): List<MyFarmsEntity> {
        return initial.map { mapToEntity(it) }
    }
}