package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.CropMasterEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.CropMasterData

class CropMasterEntityMapper : EntityMapper<CropMasterEntity, CropMasterData> {
     fun mapFromEntity(entity: CropMasterEntity): CropMasterData {
        return CropMasterData(
            cropId = entity.cropId,
            cropName = entity.cropName,
            cropCategory_id = entity.cropCategory_id,
            cropLogo = entity.cropLogo,
            cropType = entity.cropType,
            aiCropHealth = entity.aiCropHealth,
            isWaterModel = entity.waterModel,
            isCropInfo = entity.cropInfo,
            isPestDiseaseInfo = entity.pestDiseaseInfo,
            isDiseasePrediction = entity.diseasePrediction,
            isGdd = entity.gdd,
            translation = entity.translation
        )
    }

    override fun mapToEntity(dto: CropMasterData): CropMasterEntity {
        return CropMasterEntity(
            cropId = dto.cropId?:0,
            cropName = dto.cropName,
            cropCategory_id = dto.cropCategory_id,
            cropLogo = dto.cropLogo,
            cropType = dto.cropType,
            aiCropHealth = dto.aiCropHealth,
            waterModel = dto.isWaterModel,
            cropInfo = dto.isCropInfo,
            pestDiseaseInfo = dto.isPestDiseaseInfo,
            diseasePrediction = dto.isDiseasePrediction,
            gdd = dto.isGdd,
            translation = dto.translation,
            cropNameTag = dto.cropNameTag
        )
    }

    fun fromEntityList(initial: List<CropMasterEntity>): List<CropMasterData> {
        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<CropMasterData>): List<CropMasterEntity> {
        return initial.map { mapToEntity(it) }
    }

}