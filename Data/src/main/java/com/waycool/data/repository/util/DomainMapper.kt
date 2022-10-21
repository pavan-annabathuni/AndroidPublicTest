package com.waycool.data.repository.util


interface DomainMapper<Domain, DTO> {
    fun mapToDomain(dto: DTO): Domain
}