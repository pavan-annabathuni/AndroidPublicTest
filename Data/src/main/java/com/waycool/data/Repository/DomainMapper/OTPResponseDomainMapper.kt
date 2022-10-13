package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Network.NetworkModels.OTPResponseDTO
import com.waycool.data.Repository.DomainModels.OTPResponseDomain
import com.waycool.data.Repository.util.DomainMapper

class OTPResponseDomainMapper : DomainMapper<OTPResponseDomain, OTPResponseDTO> {
    override fun mapToDomain(dto: OTPResponseDTO): OTPResponseDomain {

        return OTPResponseDomain(
            message = dto.message,
            type = dto.type
        )
    }
}