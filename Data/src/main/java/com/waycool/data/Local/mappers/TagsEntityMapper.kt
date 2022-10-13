package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.TagsEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.TagsData

class TagsEntityMapper:EntityMapper<TagsEntity, TagsData> {

     fun mapFromEntity(entity: TagsEntity): TagsData {
       return TagsData(
           id=entity.id,
           tag_name = entity.tag_name,
           translation = entity.translation
       )
    }


    override fun mapToEntity(dto: TagsData): TagsEntity {

        return TagsEntity(
            id=dto.id,
            tag_name = dto.tag_name,
            translation = dto.translation
        )
    }

    fun fromEntityList(initial: List<TagsEntity>): List<TagsData>{
        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<TagsData>): List<TagsEntity>{
        return initial.map { mapToEntity(it) }
    }
}