package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.OTPResponseDTO
import com.waycool.data.repository.domainModels.OTPResponseDomain
import com.waycool.data.repository.util.DomainMapper

class OTPResponseDomainMapper : DomainMapper<OTPResponseDomain, OTPResponseDTO> {
    override fun mapToDomain(dto: OTPResponseDTO): OTPResponseDomain {

        return OTPResponseDomain(
            message = dto.message,
            type = dto.type
        )
    }
}