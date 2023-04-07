package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.CropInformationEntityData
import com.waycool.data.Local.Entity.MyCropDataEntity
import com.waycool.data.Local.mappers.MyCropEntityMapper
import com.waycool.data.Network.NetworkModels.CropModel
import com.waycool.data.repository.domainModels.CropInformationDomainData
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.util.DomainMapper

class MyCropDomainMapper:DomainMapper<MyCropDataDomain,MyCropDataEntity> {
    override fun mapToDomain(dto: MyCropDataEntity): MyCropDataDomain {
        return MyCropDataDomain(id = dto.id,
            plot=dto.plot,
            plotNickname=dto.plotNickname,
            isActive = dto.isActive,
            cropShade=dto.cropShade,
            sowingDate = dto.sowingDate,
            irrigationType= dto.irrigationType,
            noOfPlants= dto.noOfPlants,
            lenDrip=dto.lenDrip,
            widthDrip=dto.widthDrip,
            dripEmitterRate=dto.dripEmitterRate,
            area= dto.area,
            cropYear=dto.cropYear,
            cropSeason = dto.cropSeason,
            cropStage = dto.cropStage,
            cropId= dto.cropId,
            accountNoId = dto.accountNoId,
            farmId= dto.farmId,
            soilTypeId =dto.soilTypeId,
            irrigationRequired  =dto.irrigationRequired,
            disease           =dto.disease,
            cropVarietyId = dto.cropVarietyId,
            idd = dto.cropIdd,
            cropName = dto.cropName,
            cropNameTag = dto.cropNameTag,
            cropLogo = dto.cropLogo,
             soilType = dto.soilType,
        actualHarvestDate = dto.actualHarvestDate)
    }

//    class CropDomainMapper:DomainMapper<CropDomain,MyCropEntity> {
//        override fun mapToDomain(dto: MyCropEntity): CropDomain {
//            return CropDomain(id = dto.id,
//                cropName = dto.cropName,
//                cropLogo = dto.cropLogo)
//        }
//    }
    fun toDomainList(initial: List<MyCropDataEntity>): List<MyCropDataDomain> {
        return initial.map { mapToDomain(it) }
    }
}