package com.waycool.data.Repository.DomainMapper

import com.google.gson.annotations.SerializedName
import com.waycool.data.Network.NetworkModels.LoginDTO
import com.waycool.data.Repository.DomainModels.LoginDomain
import com.waycool.data.Repository.util.DomainMapper

class LoginDomainMapper : DomainMapper<LoginDomain, LoginDTO> {
    override fun mapToDomain(dto: LoginDTO): LoginDomain {
        return LoginDomain(
            status = dto.status,
            message = dto.message,
            data = dto.data
        )
    }
}