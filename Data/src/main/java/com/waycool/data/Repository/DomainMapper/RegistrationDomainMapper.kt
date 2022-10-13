package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Network.NetworkModels.RegisterData
import com.waycool.data.Repository.DomainModels.RegisterDomain
import com.waycool.data.Repository.util.DomainMapper

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