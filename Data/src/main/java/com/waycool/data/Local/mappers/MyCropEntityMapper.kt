package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.DailyEntity
import com.waycool.data.Local.Entity.MyCropDataEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.*

class MyCropEntityMapper:EntityMapper<MyCropDataEntity,MyCropDataModel> {

    override fun mapToEntity(dto: MyCropDataModel): MyCropDataEntity {
        return MyCropDataEntity(id = dto.id,
            plot=dto.plot,
            plotNickname=dto.plotNickname,
            isActive = dto.isActive,
            cropShade=dto.cropShade,
            sowingDate = dto.sowingDate,
            irrigationType= dto.irrigationType,
            noOfPlants= dto.noOfPlants,
            lenDrip=dto.lenDrip,
            widthDrip=dto.widthDrip,
            area= dto.area,
            cropYear=dto.cropYear,
            cropSeason = dto.cropSeason,
            cropStage = dto.cropStage,
            cropId= dto.cropId,
            accountNoId = dto.accountNoId,
            farmId= dto.farmId,
            expectedHarvestDate=dto.expectedHarvestDate,
            estimatedYield=dto.estimatedYield,
            actualYield=dto.actualYield,
            soilTypeId =dto.soilTypeId,
            cropVarietyId = dto.cropVarietyId,
            dripEmitterRate=dto.dripEmitterRate,
            //widthDrip=dto.widthDrip,

            irrigationRequired  =dto.irrigationRequired,
           disease           =dto.disease,
            cropIdd = dto.crop?.id,
        cropName = dto.crop?.cropName,
            cropNameTag=dto.crop?.cropTagName,
        cropLogo = dto.crop?.cropLogo,
        soilType = dto.soilType?.soilType,
        actualHarvestDate = dto.actualHarvestDate)

    }
    fun toEntityList(initial: List<MyCropDataModel>): List<MyCropDataEntity> {
        return initial.map { mapToEntity(it) }
    }
}