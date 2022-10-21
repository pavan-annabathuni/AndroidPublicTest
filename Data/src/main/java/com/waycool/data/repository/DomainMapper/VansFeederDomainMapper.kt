package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.TagsData
import com.waycool.data.Network.NetworkModels.VansFeederListNetwork
import com.waycool.data.Network.NetworkModels.VansNetwork
import com.waycool.data.repository.domainModels.TagsAndKeywordsDomain
import com.waycool.data.repository.domainModels.VansFeederDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.repository.util.DomainMapper

class VansFeederDomainMapper : DomainMapper<VansFeederDomain, VansNetwork> {
    override fun mapToDomain(dto: VansNetwork): VansFeederDomain {
        return VansFeederDomain(
            currentPage = dto.currentPage,
            total = dto.total,
            vansFeederList = VansFeederListDomainMapper().toDomainList(dto.vansFeederList),

            )
    }

    class VansFeederListDomainMapper : DomainMapper<VansFeederListDomain, VansFeederListNetwork> {
        override fun mapToDomain(dto: VansFeederListNetwork): VansFeederListDomain {
            return VansFeederListDomain(
                id=dto.id,
           vansType=dto.vansType,
           title=dto.title,
           desc=dto.desc,
           contentUrl=dto.contentUrl,
           thumbnailUrl=dto.thumbnailUrl,
           isActive=dto.isActive,
           audioUrl=dto.audioUrl,
           startDate=dto.startDate,
           endDate=dto.endDate,
           priorityOrder=dto.priorityOrder,
           sourceName=dto.sourceName,
           platform=dto.platform,
           platformType=dto.platformType,
           createdAt=dto.createdAt,
           updatedAt=dto.updatedAt,
           deletedAt=dto.deletedAt,
           langId=dto.langId,
           categoryId=dto.categoryId,
           cropId=dto.cropId,
           tags= TagsDomainMapper().toDomainList(dto.tags)
            )
        }

        fun toDomainList(initial: List<VansFeederListNetwork>): List<VansFeederListDomain> {
            return initial.map { mapToDomain(it) }
        }

    }

    class TagsDomainMapper : DomainMapper<TagsAndKeywordsDomain, TagsData> {
        override fun mapToDomain(dto: TagsData): TagsAndKeywordsDomain {
            return TagsAndKeywordsDomain(
                id = dto.id,
                tag_name = dto.tag_name,
                translation = dto.translation
            )
        }

        fun toDomainList(initial: List<TagsData>): List<TagsAndKeywordsDomain> {
            return initial.map { mapToDomain(it) }
        }
    }
}