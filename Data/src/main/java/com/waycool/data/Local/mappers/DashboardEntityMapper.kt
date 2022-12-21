package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.DashboardEntity
import com.waycool.data.Local.Entity.SubscriptionEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.DashboardNetwork
import com.waycool.data.Network.NetworkModels.ProfileNetwork
import com.waycool.data.Network.NetworkModels.SubscriptionNetwork

class DashboardEntityMapper:EntityMapper<DashboardEntity,DashboardNetwork> {
    override fun mapToEntity(dto: DashboardNetwork): DashboardEntity {
        return DashboardEntity(
            id=dto.id,
                    name=dto.name,
                    contact=dto.contact,
                    email=dto.email,
                    emailVerifiedAt=dto.emailVerifiedAt,
                    approved=dto.approved,
                    settings=dto.settings,
                    subscription=SubscriptionEntityMapper().mapToEntity(dto.subscription?:SubscriptionNetwork())
        )
    }

    class SubscriptionEntityMapper:EntityMapper<SubscriptionEntity,SubscriptionNetwork>{
        override fun mapToEntity(dto: SubscriptionNetwork): SubscriptionEntity {
            return SubscriptionEntity(
                iot = dto.iot
            )
        }

    }
}