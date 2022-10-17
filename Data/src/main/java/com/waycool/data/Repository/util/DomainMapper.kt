package com.waycool.data.Repository.util


interface DomainMapper<Domain, DTO> {
    fun mapToDomain(dto: DTO): Domain
}