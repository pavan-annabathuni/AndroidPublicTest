package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.LoginDTO
import com.waycool.data.repository.domainModels.LoginDomain
import com.waycool.data.repository.util.DomainMapper

class LoginDomainMapper : DomainMapper<LoginDomain, LoginDTO> {
    override fun mapToDomain(dto: LoginDTO): LoginDomain {
        return LoginDomain(
            status = dto.status,
            message = dto.message,
            data = dto.data
        )
    }
}