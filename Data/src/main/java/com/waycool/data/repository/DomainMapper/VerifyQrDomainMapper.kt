package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.VerifyQrDTO
import com.waycool.data.repository.domainModels.VerifyQrDomain
import com.waycool.data.repository.util.DomainMapper

class VerifyQrDomainMapper : DomainMapper<VerifyQrDomain, VerifyQrDTO> {
    override fun mapToDomain(dto: VerifyQrDTO): VerifyQrDomain {
        return VerifyQrDomain(
            status = dto.status,
            message = dto.message,
            data = dto.data
        )
    }

}
