package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.RegisterData
import com.waycool.data.repository.domainModels.RegisterDomain
import com.waycool.data.repository.util.DomainMapper

class RegistrationDomainMapper : DomainMapper<RegisterDomain, RegisterData> {
    override fun mapToDomain(dto: RegisterData): RegisterDomain {
        return RegisterDomain(
            name = dto.name,
            contact = dto.contact,
            email = dto.email,
            approved = dto.approved,
            id = dto.id
        )
    }
}