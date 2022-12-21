package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.MyFarmsEntity
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.util.DomainMapper

class MyFarmsDomainMapper:DomainMapper<MyFarmsDomain,MyFarmsEntity> {
    override fun mapToDomain(dto: MyFarmsEntity): MyFarmsDomain {

        return MyFarmsDomain(
            id=dto.id,
                    farmName=dto.farmName,
                    farmCenter=dto.farmCenter,
                    farmArea=dto.farmArea,
                    farmJson=dto.farmJson,
                    farmWaterSource=dto.farmWaterSource,
                    farmPumpHp=dto.farmPumpHp,
                    farmPumpType=dto.farmPumpType,
                    farmPumpDepth=dto.farmPumpDepth,
                    farmPumpPipeSize=dto.farmPumpPipeSize,
                    farmPumpFlowRate=dto.farmPumpFlowRate,
                    isPrimary=dto.isPrimary,
                    updatedAt=dto.updatedAt,
                    accountNoId=dto.accountNoId
        )
    }

    fun toDomainList(initial: List<MyFarmsEntity>): List<MyFarmsDomain> {
        return initial.map { mapToDomain(it) }
    }
}