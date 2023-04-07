package com.waycool.data.repository.util

import androidx.annotation.Keep

@Keep
interface DomainMapper<Domain, DTO> {
    fun mapToDomain(dto: DTO): Domain
}