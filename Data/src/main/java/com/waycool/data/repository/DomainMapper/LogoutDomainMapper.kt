package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.LogoutDTO
import com.waycool.data.repository.domainModels.LogoutDomain
import com.waycool.data.repository.util.DomainMapper

class LogoutDomainMapper : DomainMapper<LogoutDomain, LogoutDTO> {
    override fun mapToDomain(dto: LogoutDTO): LogoutDomain {
        return LogoutDomain(
            status = dto.status,
            message = dto.message,
            data = dto.data
        )
    }
}