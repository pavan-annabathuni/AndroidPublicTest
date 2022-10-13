package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.CropMasterEntity
import com.waycool.data.Local.Entity.ModuleMasterEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.CropMasterData
import com.waycool.data.Network.NetworkModels.ModuleData

class ModuleMasterEntityMapper : EntityMapper<ModuleMasterEntity, ModuleData> {
    fun mapFromEntity(entity: ModuleMasterEntity): ModuleData {
        return ModuleData(
            id = entity.id,
            moduleType = entity.moduleType,
            moduleIcon = entity.moduleIcon,
            moduleDesc = entity.moduleDesc,
            Premium = entity.Premium,
            tittle = entity.tittle,
            price = entity.price,
            audioURl = entity.audioURl,
            translation = entity.translation,
        )
    }

    override fun mapToEntity(dto: ModuleData): ModuleMasterEntity {
        return ModuleMasterEntity(
            id = dto.id,
            moduleType = dto.moduleType,
            moduleIcon = dto.moduleIcon,
            moduleDesc = dto.moduleDesc,
            Premium = dto.Premium,
            tittle = dto.tittle,
            price = dto.price,
            audioURl = dto.audioURl,
            translation = dto.translation,
        )
    }

    fun fromEntityList(initial: List<ModuleMasterEntity>): List<ModuleData> {
        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<ModuleData>): List<ModuleMasterEntity> {
        return initial.map { mapToEntity(it) }
    }
}