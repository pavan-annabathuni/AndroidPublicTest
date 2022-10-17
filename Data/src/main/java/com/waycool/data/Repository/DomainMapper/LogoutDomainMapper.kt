package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Network.NetworkModels.LogoutDTO
import com.waycool.data.Repository.DomainModels.LogoutDomain
import com.waycool.data.Repository.util.DomainMapper

class LogoutDomainMapper : DomainMapper<LogoutDomain, LogoutDTO> {
    override fun mapToDomain(dto: LogoutDTO): LogoutDomain {
        return LogoutDomain(
            status = dto.status,
            message = dto.message,
            data = dto.data
        )
    }
}