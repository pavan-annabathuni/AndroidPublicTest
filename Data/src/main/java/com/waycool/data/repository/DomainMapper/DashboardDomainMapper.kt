package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.DashboardEntity
import com.waycool.data.Local.Entity.SubscriptionEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.repository.domainModels.SubscriptionDomain
import com.waycool.data.repository.util.DomainMapper

class DashboardDomainMapper : DomainMapper<DashboardDomain, DashboardEntity> {
    override fun mapToDomain(dto: DashboardEntity): DashboardDomain {
        return DashboardDomain(
            id = dto.id,
            name = dto.name,
            contact = dto.contact,
            email = dto.email,
            emailVerifiedAt = dto.emailVerifiedAt,
            approved = dto.approved,
            settings = dto.settings,
            subscription = SubscriptionDomainMapper()
                .mapToEntity(dto.subscription ?: SubscriptionEntity())
        )
    }

    class SubscriptionDomainMapper : EntityMapper<SubscriptionDomain, SubscriptionEntity> {


        override fun mapToEntity(dto: SubscriptionEntity): SubscriptionDomain {
            return SubscriptionDomain(
                iot = dto.iot
            )
        }

    }
}