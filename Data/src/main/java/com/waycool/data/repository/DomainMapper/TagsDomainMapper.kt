package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.TagsEntity
import com.waycool.data.repository.domainModels.TagsAndKeywordsDomain
import com.waycool.data.repository.util.DomainMapper

class TagsKeywordsDomainMapper : DomainMapper<TagsAndKeywordsDomain, TagsEntity> {
    override fun mapToDomain(dto: TagsEntity): TagsAndKeywordsDomain {
        return TagsAndKeywordsDomain(
            id = dto.id,
            tag_name = dto.tag_name,
            translation = dto.translation
        )
    }

    fun toDomainList(initial: List<TagsEntity>): List<TagsAndKeywordsDomain> {
        return initial.map { mapToDomain(it) }
    }
}